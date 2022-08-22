package cn.coderap.order.service.impl;

import cn.coderap.order.dao.TaskHisMapper;
import cn.coderap.order.dao.TaskMapper;
import cn.coderap.order.pojo.Task;
import cn.coderap.order.pojo.TaskHis;
import cn.coderap.order.service.TaskService;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private TaskHisMapper taskHisMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String ADD_POINT_COUNT = "addPointCount";

    @Transactional
    @Override
    public void deleteTask(Task task) {
        //清理redis中的该订单编号
        Map info = JSON.parseObject(task.getRequestBody(), Map.class);
        String orderId = String.valueOf(info.get("orderId"));
        redisTemplate.boundHashOps(ADD_POINT_COUNT).delete(orderId);

        Long id = task.getId();
        //设置删除时间
        task.setDeleteTime(new Date());

        //当前任务备份到tb_task_his表，用于后边的数据分析
        task.setId(null);
        TaskHis taskHis = new TaskHis();
        BeanUtils.copyProperties(task,taskHis);
        taskHisMapper.insertSelective(taskHis);

        //从tb_task表中删除当前任务
        task.setId(id);
        taskMapper.deleteByPrimaryKey(task);
    }
}
