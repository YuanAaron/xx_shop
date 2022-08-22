package cn.coderap.user.listener;

import cn.coderap.constant.RabbitmqConstant;
import cn.coderap.order.pojo.Task;
import cn.coderap.user.service.UserService;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@RabbitListener(queues = RabbitmqConstant.POINT_ADD_QUEUE)
@Component
public class AddPointListener {

    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String ADD_POINT_COUNT = "addPointCount";

    @RabbitHandler
    public void receiveMessage(String message){
        Task task = JSON.parseObject(message, Task.class);
        if (task == null || StringUtils.isEmpty(task.getRequestBody())){
            return;
        }

        Map info = JSON.parseObject(task.getRequestBody(), Map.class);
        String orderId = String.valueOf(info.get("orderId"));
        //5.3 防止重复消费
        Long cnt = redisTemplate.boundHashOps(ADD_POINT_COUNT).increment(orderId, 1); // 每次进来都会自增1
        if (cnt > 1) {
            return;
        }

        //5.4判断当前订单是否操作过、更新用户积分、增加积分日志表记录、从redis中删除该任务
        int result = userService.addUserPoints(task);
        if (result <= 0){
            return;
        }
        //5.5返回通知
        rabbitTemplate.convertAndSend(RabbitmqConstant.POINT_EXCHANGE,RabbitmqConstant.POINT_FINISH_ROUTING_KEY,JSON.toJSONString(task));
    }
}
