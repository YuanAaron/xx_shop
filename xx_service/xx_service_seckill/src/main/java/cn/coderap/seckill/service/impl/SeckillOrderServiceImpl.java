package cn.coderap.seckill.service.impl;

import cn.coderap.entity.SeckillStatus;
import cn.coderap.seckill.service.SeckillOrderService;
import cn.coderap.seckill.task.MultiThreadCreateOrderTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private MultiThreadCreateOrderTask task;
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SECKILL_ORDER_QUEUE = "SeckillOrderQueue";
    private static final String SECKILL_ORDER_STATUS_QUEUE = "SeckillOrderStatusQueue";
    private static final String SECKILL_ORDER_COUNT = "SeckillOrderCount";

    /**
     * 基础秒杀下单存在的问题：
     * 1.恶意刷单
     * 2.规定一个商品只能秒杀一次，但当前实现一个商品可以秒杀第二次
     *
     * 秒杀时流量激增，比如增加了20倍，那么可以通过增加20倍的节点数来提供服务，但这样做
     * 成本很高；如果想要节点扩容的倍数少且能够正常提供服务，可以用多线程异步下单来实现，即每个节点
     * 在单位时间内能够处理更多请求，最终通过较少的节点也能正常提供服务。多线程异步下单存在的问题：
     * 1. 超卖---redis单线程
     * 2. 单击下单按钮的顺序（下单请求到达微服务的顺序）可能和真正下单（写到redis）中的顺序不一致，对于用户来说不公平---利用redis的列表的先进先出实现
     * @param time
     * @param id
     * @param username
     */
    @Override
    public void add(String time, Long id, String username) {
        //重复下单问题（一个用户最多只允许有一个排队信息存在，一个用户最多只允许有一个未支付的订单信息）
        Long userOrderCount = redisTemplate.boundHashOps(SECKILL_ORDER_COUNT).increment(username, 1); // 每次进来都会自增1
        if (userOrderCount > 1) {
            throw new RuntimeException("重复抢单！！！");
        }

        //使用redis list解决多线程下单产生的顺序问题
        //创建排队对象（存储用户和订单信息）
        SeckillStatus seckillStatus = new SeckillStatus(username, new Date(), 1, id, time);
        //存入redis list中进行排队
        redisTemplate.boundListOps(SECKILL_ORDER_QUEUE).leftPush(seckillStatus);
        redisTemplate.boundHashOps(SECKILL_ORDER_STATUS_QUEUE).put(username, seckillStatus.getStatus());
        task.createOrder();
        System.out.println("我不会等你，证明是异步的");
    }

    @Override
    public SeckillStatus queryStatus(String username) {
        return (SeckillStatus) redisTemplate.boundHashOps(SECKILL_ORDER_STATUS_QUEUE).get(username);
    }
}
