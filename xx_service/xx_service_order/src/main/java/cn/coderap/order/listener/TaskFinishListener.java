package cn.coderap.order.listener;

import cn.coderap.constant.RabbitmqConstant;
import cn.coderap.order.pojo.Task;
import cn.coderap.order.service.TaskService;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@RabbitListener(queues = RabbitmqConstant.POINT_FINISH_QUEUE)
@Component
public class TaskFinishListener {
    @Autowired
    private TaskService taskService;

    @RabbitHandler
    public void receiveMessage(String message){
        Task task = JSON.parseObject(message, Task.class);
        taskService.deleteTask(task);
    }
}
