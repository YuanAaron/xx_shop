package cn.coderap.order.service.impl;

import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Sku;
import cn.coderap.order.dao.OrderItemMapper;
import cn.coderap.order.dao.OrderLogMapper;
import cn.coderap.order.dao.OrderMapper;
import cn.coderap.order.feign.CartFeign;
import cn.coderap.order.feign.SkuFeign;
import cn.coderap.order.pojo.Order;
import cn.coderap.order.pojo.OrderItem;
import cn.coderap.order.pojo.OrderLog;
import cn.coderap.order.service.OrderService;
import cn.coderap.order.util.AdminToken;
import cn.coderap.user.feign.UserFeign;
import cn.coderap.util.DateUtil;
import cn.coderap.util.IdWorker;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private CartFeign cartFeign;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private UserFeign userFeign;
    @Autowired
    private OrderLogMapper orderLogMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    // TODO 库存不足时，仍然会向tb_order和tb_order_item中插入记录，这肯定是不合适的，分布式事务？？？
    @GlobalTransactional(name="order_add")
    @Override
    public void add(Order order) {
        //1.获取购物车列表
        Map cartMap = cartFeign.list();
        List<OrderItem> orderItemList = JSON.parseArray(JSON.toJSONString(cartMap.get("orderItemList")), OrderItem.class);
        if (CollectionUtils.isEmpty(orderItemList)) {
            return;
        }

        //2.设置订单信息并保存
        order.setId(String.valueOf(idWorker.nextId()));
        //统计计算
        int totalNum = 0;
        int totalPrice = 0;
        for (OrderItem orderItem : orderItemList) {
            //只统计勾选的
            if (orderItem.isChecked()) {
                totalNum += orderItem.getNum();
                //通过商品微服务查询到当前的商品的价格，并以当前价格为准
                totalPrice += finalMoney(orderItem);
            }
        }
        order.setTotalNum(totalNum);
        order.setTotalMoney(totalPrice);
        order.setPayMoney(totalPrice);

        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setSourceType("1");//订单来源
        order.setBuyerRate("0");//评价状态
        order.setOrderStatus("0");//订单状态 未完成
        order.setPayStatus("0");//支付状态 未支付
        order.setConsignStatus("0"); //是否发货 未发货
        order.setIsDelete("0");//非删除
        orderMapper.insertSelective(order);

        //调用商品微服务完成存库及其销量变更
        skuFeign.changeInventoryAndSaleNumber(order.getUsername());

        //积分累加(这里自定义的积分规则为：积分=支付价格/10）
        //TODO 异步积分累积：mq中发送一个消息 username,point
        userFeign.addPoints(order.getPayMoney()/10);

        //3.设置订单明细信息并保存
        for (OrderItem orderItem : orderItemList) {
            if (orderItem.isChecked()) {
                orderItem.setId(String.valueOf(idWorker.nextId()));
                orderItem.setOrderId(order.getId());
                orderItem.setIsReturn("0");//退货状态
                orderItemMapper.insertSelective(orderItem);
                //4.删除购物车信息: isChecked = true
                cartFeign.delete(orderItem.getSkuId());
            }
        }

//        int a = 1 /0;

        //将订单编号发送到ordercreate_queue中
        rabbitTemplate.convertAndSend("", "ordercreate_queue", order.getId());
    }

    @Override
    public void close(String orderId) {
        //关闭订单
        Order order = orderMapper.selectByPrimaryKey(orderId);
        order.setUpdateTime(new Date());//更新时间
        order.setCloseTime(new Date());//关闭时间
        order.setOrderStatus("4");//关闭状态
        orderMapper.updateByPrimaryKeySelective(order);

        //记录订单日志变动
        OrderLog orderLog = new OrderLog();
        orderLog.setRemarks(orderId + "订单已关闭");
        orderLog.setOrderStatus("4");
        orderLog.setOperateTime(new Date());
        orderLog.setOperater("system");
        orderLog.setId(String.valueOf(idWorker.nextId()));
        orderLogMapper.insert(orderLog);

        //恢复库存&销量
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        List<OrderItem> orderItems = orderItemMapper.select(orderItem);
        for (OrderItem orderItem_ : orderItems) {
            //调用商品微服务
//            skuFeign.resumeStockNum(orderItem_.getSkuId(),orderItem_.getNum());
            Result result = resumeStockNum(orderItem_);
            System.out.println("恢复库存&销量: " + result);
        }
    }

    /**
     * 利用RestTemplate发送post请求恢复库存&销量
     * @param orderItem_
     */
    private Result resumeStockNum(OrderItem orderItem_) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("goods");
        if (serviceInstance == null) {
            throw new RuntimeException("找不到认证服务器");
        }
        //拼写目标地址
        String path = serviceInstance.getUri().toString()+"/sku/resumeStockNum";
        //封装参数
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        formData.add("skuId", orderItem_.getSkuId());
        formData.add("num", orderItem_.getNum()+"");
        //定义header
        MultiValueMap<String,String> header = new LinkedMultiValueMap<>();
        header.add("Authorization","bearer "+ AdminToken.create());
        //执行请求
        Result result = null;
        try {
            ResponseEntity<Result> mapResponseEntity =
                    restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<MultiValueMap<String, String>>(formData, header), Result.class);
            result = mapResponseEntity.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    private int finalMoney(OrderItem orderItem) {
        Result<Sku> result= skuFeign.findById(orderItem.getSkuId());
        Sku sku = result.getData();
        if (!orderItem.getPrice().equals(sku.getPrice())) {
            return sku.getPrice() * orderItem.getNum();
        }
        return orderItem.getMoney();
    }

    @Override
    public void update(Order order) {
        orderMapper.updateByPrimaryKeySelective(order);
    }

    @Override
    public void delete(String id) {
        orderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Order> findList(Map<String, Object> searchMap) {
        Example example = createExample(searchMap);
        return orderMapper.selectByExample(example);
    }

    @Override
    public Page<Order> findPage(Map<String, Object> searchMap, int page, int size) {
        PageHelper.startPage(page, size);
        Example example = createExample(searchMap);
        return (Page<Order>) orderMapper.selectByExample(example);
    }

    /**
     * 更改订单状态及记录订单日志
     * @param map
     */
    @Override
    public void changeOrderStatusAndOrderLog(Map<String,String> map) {
        //获取订单对象
        Order order = orderMapper.selectByPrimaryKey(map.get("out_trade_no"));
        //订单存在并且未支付
        if (order != null && "0".equals(order.getPayStatus())) {
            order.setPayStatus("1");//已支付
            order.setOrderStatus("1");//已支付
            order.setTransactionId(map.get("trade_no")); //支付宝流水号
            order.setUpdateTime(new Date());
            order.setPayTime(DateUtil.str2Date(map.get("gmt_payment"), DateUtil.PATTERN_YYYY_MM_DDHHMMSS)); //支付时间
            orderMapper.updateByPrimaryKeySelective(order);

            //记录订单变动日志
            OrderLog orderLog = new OrderLog();
            orderLog.setId(idWorker.nextId() + "");
            orderLog.setOperater("system");
            orderLog.setOrderId(order.getId());
            orderLog.setOperateTime(new Date());
            orderLog.setOrderStatus("1");
            orderLog.setPayStatus("1");
            orderLog.setRemarks("Alipay流水:" + map.get("trade_no"));
            orderLogMapper.insertSelective(orderLog);
        }
    }

    private Example createExample(Map<String, Object> searchMap) {
        Example example = new Example(Order.class);
        Example.Criteria criteria = example.createCriteria();
        if (!CollectionUtils.isEmpty(searchMap)) {
            // 订单id
            if (searchMap.get("id") != null && !"".equals(searchMap.get("id"))) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 支付类型，1、在线支付、0 货到付款
            if (searchMap.get("payType") != null && !"".equals(searchMap.get("payType"))) {
                criteria.andEqualTo("payType", searchMap.get("payType"));
            }
            // 物流名称
            if (searchMap.get("shippingName") != null && !"".equals(searchMap.get("shippingName"))) {
                criteria.andLike("shippingName", "%" + searchMap.get("shippingName") + "%");
            }
            // 物流单号
            if (searchMap.get("shippingCode") != null && !"".equals(searchMap.get("shippingCode"))) {
                criteria.andLike("shippingCode", "%" + searchMap.get("shippingCode") + "%");
            }
            // 用户名称
            if (searchMap.get("username") != null && !"".equals(searchMap.get("username"))) {
                criteria.andLike("username", "%" + searchMap.get("username") + "%");
            }
            // 买家留言
            if (searchMap.get("buyerMessage") != null && !"".equals(searchMap.get("buyerMessage"))) {
                criteria.andLike("buyerMessage", "%" + searchMap.get("buyerMessage") + "%");
            }
            // 是否评价
            if (searchMap.get("buyerRate") != null && !"".equals(searchMap.get("buyerRate"))) {
                criteria.andLike("buyerRate", "%" + searchMap.get("buyerRate") + "%");
            }
            // 收货人
            if (searchMap.get("receiverContact") != null && !"".equals(searchMap.get("receiverContact"))) {
                criteria.andLike("receiverContact", "%" + searchMap.get("receiverContact") + "%");
            }
            // 收货人手机
            if (searchMap.get("receiverMobile") != null && !"".equals(searchMap.get("receiverMobile"))) {
                criteria.andLike("receiverMobile", "%" + searchMap.get("receiverMobile") + "%");
            }
            // 收货人地址
            if (searchMap.get("receiverAddress") != null && !"".equals(searchMap.get("receiverAddress"))) {
                criteria.andLike("receiverAddress", "%" + searchMap.get("receiverAddress") + "%");
            }
            // 订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
            if (searchMap.get("sourceType") != null && !"".equals(searchMap.get("sourceType"))) {
                criteria.andEqualTo("sourceType", searchMap.get("sourceType"));
            }
            // 交易流水号
            if (searchMap.get("transactionId") != null && !"".equals(searchMap.get("transactionId"))) {
                criteria.andLike("transactionId", "%" + searchMap.get("transactionId") + "%");
            }
            // 订单状态
            if (searchMap.get("orderStatus") != null && !"".equals(searchMap.get("orderStatus"))) {
                criteria.andEqualTo("orderStatus", searchMap.get("orderStatus"));
            }
            // 支付状态
            if (searchMap.get("payStatus") != null && !"".equals(searchMap.get("payStatus"))) {
                criteria.andEqualTo("payStatus", searchMap.get("payStatus"));
            }
            // 发货状态
            if (searchMap.get("consignStatus") != null && !"".equals(searchMap.get("consignStatus"))) {
                criteria.andEqualTo("consignStatus", searchMap.get("consignStatus"));
            }
            // 是否删除
            if (searchMap.get("isDelete") != null && !"".equals(searchMap.get("isDelete"))) {
                criteria.andEqualTo("isDelete", searchMap.get("isDelete"));
            }

            // 数量合计
            if (searchMap.get("totalNum") != null) {
                criteria.andEqualTo("totalNum", searchMap.get("totalNum"));
            }
            // 金额合计
            if (searchMap.get("totalMoney") != null) {
                criteria.andEqualTo("totalMoney", searchMap.get("totalMoney"));
            }
            // 优惠金额
            if (searchMap.get("preMoney") != null) {
                criteria.andEqualTo("preMoney", searchMap.get("preMoney"));
            }
            // 邮费
            if (searchMap.get("postFee") != null) {
                criteria.andEqualTo("postFee", searchMap.get("postFee"));
            }
            // 实付金额
            if (searchMap.get("payMoney") != null) {
                criteria.andEqualTo("payMoney", searchMap.get("payMoney"));
            }

        }
        return example;
    }

}
