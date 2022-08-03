package cn.coderap.order.listener;

import cn.coderap.entity.Result;
import cn.coderap.order.service.OrderService;
import cn.coderap.order.feign.AlipayFeign;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = "ordertimeout_queue")
@Component
public class OrderPayTimeoutListener {
    @Autowired
    private AlipayFeign alipayFeign;
    @Autowired
    private OrderService orderService;

    /**
     * 1.不扫码（支付宝返回TRADE_NOT_EXIST）,交易没有在支付宝服务器创建，本地关闭即可
     * 2.扫码不支付（支付宝返回WAIT_BUYER_PAY）,交易已经创建,执行关闭，且本地也要关闭
     * @param orderId
     * @throws Exception
     */
    @RabbitHandler
    public void orderTimeoutHandler(String orderId) throws Exception {
        //1.去支付宝查询该订单的支付状态，只有处于未支付状态（WAIT_BUYER_PAY）才关闭交易
        String tradeStatus = alipayFeign.query(orderId);
        //如果交易已经关闭 || 交易支付成功 || 交易结束，不可退款，那么无需处理
        if ("TRADE_CLOSED".equals(tradeStatus)
                || "TRADE_SUCCESS".equals(tradeStatus)
                || "TRADE_FINISHED".equals(tradeStatus)) {
            return;
        }
        if ("WAIT_BUYER_PAY".equals(tradeStatus)) {
            //已经扫码了，但没有支付，在支付宝服务器交易已经创建创建了
            //2.在支付宝服务器关闭该交易
            Result result = alipayFeign.close(orderId);
            System.out.println(result);
        }
        //扫码/不扫码，本地都要关闭
        //3.本地（XX商城）关闭订单&记录订单日志&回滚库存&回滚销量&回滚积分
        orderService.close(orderId);
    }

}
