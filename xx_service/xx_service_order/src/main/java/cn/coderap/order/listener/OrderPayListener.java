package cn.coderap.order.listener;

import cn.coderap.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@RabbitListener(queues = "order_queue")
@Component
public class OrderPayListener {
    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void receivePayStatus(Map<String,String> message){
        System.out.println("=======================监听order_queue=======================");
        //变更订单状态&记录订单变动日志
        orderService.changeOrderStatusAndOrderLog(message);
    }

}
