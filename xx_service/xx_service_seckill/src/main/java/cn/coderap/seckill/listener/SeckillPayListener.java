package cn.coderap.seckill.listener;

import cn.coderap.seckill.service.SeckillOrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@RabbitListener(queues = "seckill_queue")
@Component
public class SeckillPayListener {
    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀订单支付成功后
     * @param message
     */
    @RabbitHandler
    public void receivePayStatus(Map<String,String> message){
        System.out.println("监听秒杀队列的消息:" + message);
        String username = message.get("username");
        //变更订单状态
        seckillOrderService.updateSeckillOrderStatus(username,message);
    }

}
