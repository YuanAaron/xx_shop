package cn.coderap.seckill.task;

import cn.coderap.entity.SeckillStatus;
import cn.coderap.seckill.dao.SeckillGoodsMapper;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.pojo.SeckillOrder;
import cn.coderap.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MultiThreadCreateOrderTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    private static final String SECKILL_GOODS = "SeckillGoods_";
    private static final String SECKILL_ORDER = "SeckillOrder_";
    private static final String SECKILL_ORDER_QUEUE = "SeckillOrderQueue";
    private static final String SECKILL_ORDER_STATUS_QUEUE = "SeckillOrderStatusQueue";

    /**
     * 以多线程的方式执行该方法
     */
    @Async
    public void createOrder() {
        System.out.println("MultiThreadCreateOrderTask....createOrder...start");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        //获取Redis中排队的用户信息
        SeckillStatus seckillStatus = (SeckillStatus) redisTemplate.boundListOps(SECKILL_ORDER_QUEUE).rightPop();
        if (seckillStatus == null) {
            return;
        }
        //TODO 这三个不能作为参数传过来？？？不是很理解！
        String username = seckillStatus.getUsername();
        String time = seckillStatus.getTime();
        Long id = seckillStatus.getGoodsId();

        //1.判断有没有库存
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_GOODS + time).get(id);
        if (seckillGoods == null || seckillGoods.getStockCount() <= 0) {
            throw new RuntimeException("卖完了!");
        }

        //2.创建订单信息
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(idWorker.nextId());
        seckillOrder.setSeckillId(id);
        seckillOrder.setMoney(seckillGoods.getCostPrice());
        seckillOrder.setUserId(username);
        seckillOrder.setCreateTime(new Date());
        seckillOrder.setStatus("0"); //未支付
        System.out.println("秒杀订单ID：" + seckillOrder.getId());
        //保存订单到Redis,每人只能秒杀一次
        redisTemplate.boundHashOps(SECKILL_ORDER).put(username, seckillOrder);

        //修改用户下单的状态
        seckillStatus.setStatus(2); //待付款
        seckillStatus.setMoney(Float.valueOf(seckillGoods.getCostPrice().toString()));
        seckillStatus.setOrderId(seckillOrder.getId());
        redisTemplate.boundHashOps(SECKILL_ORDER_STATUS_QUEUE).put(username, seckillStatus);

        //3.库存递减
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        if (seckillGoods.getStockCount() <= 0) {
            //3.1当前购买的商品就是最后一件，那么移除redis中该商品记录
            //3.1.1 同步库存到mysql中
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            //3.1.2 移除redis中该商品的数据
            redisTemplate.boundHashOps(SECKILL_GOODS + time).delete(id);
        } else {
            //3.2当前购买的商品不是最后一件
            //3.2.1将库存数据更新到redis中
            redisTemplate.boundHashOps(SECKILL_GOODS + time).put(id, seckillGoods);
        }
        System.out.println("MultiThreadCreateOrderTask....createOrder...end...下单成功");
    }
}
