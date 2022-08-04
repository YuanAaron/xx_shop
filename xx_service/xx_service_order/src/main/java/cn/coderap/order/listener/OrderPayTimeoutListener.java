package cn.coderap.order.listener;

import cn.coderap.entity.Result;
import cn.coderap.order.service.OrderService;
import cn.coderap.order.feign.AlipayFeign;
import cn.coderap.order.util.AdminToken;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RabbitListener(queues = "ordertimeout_queue")
@Component
public class OrderPayTimeoutListener {
    @Autowired
    private AlipayFeign alipayFeign;
    @Autowired
    private OrderService orderService;
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 1.不扫码（支付宝返回TRADE_NOT_EXIST）,交易没有在支付宝服务器创建，本地关闭即可
     * 2.扫码不支付（支付宝返回WAIT_BUYER_PAY）,交易已经创建,执行关闭，且本地也要关闭
     * @param orderId
     * @throws Exception
     */
    @RabbitHandler
    public void orderTimeoutHandler(String orderId) throws Exception {
        //1.去支付宝查询该订单的支付状态，只有处于未支付状态（WAIT_BUYER_PAY）才关闭交易
//        String tradeStatus = alipayFeign.query(orderId);
        String tradeStatus = queryStatus(orderId);
        //如果交易已经关闭 || 交易支付成功 || 交易结束，不可退款，那么无需处理
        if ("TRADE_CLOSED".equals(tradeStatus)
                || "TRADE_SUCCESS".equals(tradeStatus)
                || "TRADE_FINISHED".equals(tradeStatus)) {
            return;
        }
        if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
            //已经扫码了，但没有支付，在支付宝服务器交易已经创建创建了
            //2.在支付宝服务器关闭该交易
//            Result result = alipayFeign.close(orderId);
            Result result = close(orderId);
            System.out.println("关闭支付宝服务器的交易: " + result);
        }
        //扫码/不扫码，本地都要关闭
        //3.本地（XX商城）关闭订单&记录订单日志&回滚库存&回滚销量&回滚积分
        orderService.close(orderId);
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
