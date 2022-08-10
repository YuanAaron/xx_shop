package cn.coderap.seckill.listener;

import cn.coderap.entity.Result;
import cn.coderap.entity.SeckillStatus;
import cn.coderap.seckill.dao.SeckillGoodsMapper;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.util.AdminToken;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

@RabbitListener(queues = "seckilltimeout_queue")
@Component
public class SeckillPayTimeoutListener {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    private static final String SECKILL_ORDER_STATUS_QUEUE = "SeckillOrderStatusQueue";
    private static final String SECKILL_ORDER = "SeckillOrder_";
    private static final String SECKILL_ORDER_COUNT = "SeckillOrderCount";
    private static final String SECKILL_GOODS = "SeckillGoods_";
    private static final String SECKILL_GOODS_QUEUE = "SeckillGoodsQueue_";

    @RabbitHandler
    public void seckillTimeoutHandler(String message) {
        SeckillStatus seckillStatus = JSON.parseObject(message, SeckillStatus.class);
        //如果SECKILL_ORDER_STATUS_QUEUE没有排队信息，说明已经支付过了，否则未支付，需要进行如下处理
        seckillStatus = (SeckillStatus) redisTemplate.boundHashOps(SECKILL_ORDER_STATUS_QUEUE).get(seckillStatus.getUsername());
        if (seckillStatus != null) {
            String username = seckillStatus.getUsername();
            String time = seckillStatus.getTime();
            Long id = seckillStatus.getGoodsId();
            Long orderId = seckillStatus.getOrderId();

            //1、在支付宝服务器关闭该交易（调用支付微服务）
            String tradeStatus = queryStatus(String.valueOf(orderId));
            //如果交易已经关闭 || 交易支付成功 || 交易结束，不可退款，那么无需处理
            if ("TRADE_CLOSED".equals(tradeStatus)
                    || "TRADE_SUCCESS".equals(tradeStatus)
                    || "TRADE_FINISHED".equals(tradeStatus)) {
                return;
            }
            if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
                //已经扫码了，但没有支付，在支付宝服务器交易已经创建创建了
                //2.在支付宝服务器关闭该交易
                Result result = close(String.valueOf(orderId));
                System.out.println("关闭支付宝服务器的交易: " + result);
            }
            //2、从redis中删除订单
            redisTemplate.boundHashOps(SECKILL_ORDER).delete(username);
            //清理掉排队信息
            redisTemplate.boundHashOps(SECKILL_ORDER_COUNT).delete(username);
            redisTemplate.boundHashOps(SECKILL_ORDER_STATUS_QUEUE).delete(username);
            //3、回滚库存
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS + time).get(id);
            if (seckillGoods == null) {
                Example example = new Example(SeckillGoods.class);
                Example.Criteria criteria = example.createCriteria();
                criteria.andEqualTo("id", id);
                seckillGoods = seckillGoodsMapper.selectOneByExample(example);
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
                // 数据库中该商品库存为0，先将数据库中的库存+1
                seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            } else {
                //当前购买的商品不是最后一件
                seckillGoods.setStockCount(seckillGoods.getStockCount() + 1);
            }
            // 将数据库中的信息同步到redis
            redisTemplate.boundHashOps(SECKILL_GOODS + time).put(id, seckillGoods);
            redisTemplate.boundListOps(SECKILL_GOODS_QUEUE + seckillGoods.getId()).leftPush(seckillGoods.getId());
        }
    }

    /**
     * 利用RestTemplate发送get请求获取支付状态
     */
    private String queryStatus(String orderId) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("pay");
        if (serviceInstance == null) {
            throw new RuntimeException("找不到认证服务器");
        }
        //拼写目标地址
        String path = serviceInstance.getUri().toString() + "/alipay/queryStatus?orderId=" + orderId ;
        //定义header
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization","bearer "+ AdminToken.create());
        //执行请求
        String tradeStatus = null;
        try {
            ResponseEntity<String> mapResponseEntity =
                    restTemplate.exchange(path, HttpMethod.GET, new HttpEntity<MultiValueMap<String, Object>>(null, header), String.class);
            tradeStatus = mapResponseEntity.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return tradeStatus;
    }

    /**
     * 利用RestTemplate发送post请求关闭支付宝服务器的交易
     */
    private Result close(String orderId) {
        ServiceInstance serviceInstance = loadBalancerClient.choose("pay");
        if (serviceInstance == null) {
            throw new RuntimeException("找不到认证服务器");
        }
        //拼写目标地址
        String path = serviceInstance.getUri().toString() + "/alipay/close";
        //封装参数
        MultiValueMap<String,String> formData = new LinkedMultiValueMap<>();
        formData.add("orderId", orderId);
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


}
