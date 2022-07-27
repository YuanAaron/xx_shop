package cn.coderap.order.service.impl;

import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Sku;
import cn.coderap.order.dao.OrderItemMapper;
import cn.coderap.order.dao.OrderMapper;
import cn.coderap.order.feign.CartFeign;
import cn.coderap.order.feign.SkuFeign;
import cn.coderap.order.pojo.Order;
import cn.coderap.order.pojo.OrderItem;
import cn.coderap.order.service.OrderService;
import cn.coderap.user.feign.UserFeign;
import cn.coderap.util.IdWorker;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
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
//    @Autowired
//    private RabbitTemplate rabbitTemplate;

    @Override
    public List<Order> findAll() {
        return orderMapper.selectAll();
    }

    @Override
    public Order findById(String id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    // TODO 库存不足时，仍然会向tb_order和tb_order_item中插入记录，这肯定是不合适的，分布式事务？？？
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
//        //将订单编号发送到ordercreate_queue中
//        rabbitTemplate.convertAndSend("", "ordercreate_queue", order.getId());
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
