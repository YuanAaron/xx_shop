package cn.coderap.order.timer;

import cn.coderap.constant.RabbitmqConstant;
import cn.coderap.order.dao.TaskMapper;
import cn.coderap.order.pojo.Task;
import com.alibaba.fastjson.JSON;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Component
public class PointTask {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 5.2设置定时任务扫描任务表最新数据，并将其发送到mq
     */
    @Scheduled(cron = "0 0/2 * * * ?") //每隔2min
    public void queryPointTask(){
        //1.获取任务表中更新时间小于系统当前时间的数据
        Example example = new Example(Task.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andLessThan("updateTime", new Date());
        List<Task> taskList = taskMapper.selectByExample(example);
        if (taskList != null && taskList.size() > 0){
            //2.将任务数据发送到mq
            for (Task task : taskList) {
                rabbitTemplate.convertAndSend(RabbitmqConstant.POINT_EXCHANGE,RabbitmqConstant.POINT_ADD_ROUTING_KEY, JSON.toJSONString(task));
            }
        }
    }
}
