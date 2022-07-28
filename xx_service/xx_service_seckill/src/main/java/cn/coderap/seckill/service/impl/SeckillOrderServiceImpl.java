package cn.coderap.seckill.service.impl;

import cn.coderap.seckill.dao.SeckillGoodsMapper;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.pojo.SeckillOrder;
import cn.coderap.seckill.service.SeckillOrderService;
import cn.coderap.util.IdWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    private static final String SECKILL_KEY = "SeckillGoods_";

    @Override
    public void add(String time, Long id, String username) {
        //1.判断有没有库存
        SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.boundHashOps(SECKILL_KEY + time).get(id);
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
        redisTemplate.boundHashOps("SeckillOrder_").put(username, seckillOrder);

        //3.库存递减
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        if (seckillGoods.getStockCount() <= 0) {
            //3.1当前购买的商品就是最后一件，那么移除redis中该商品记录
            //3.1.1 同步库存到mysql中
            seckillGoodsMapper.updateByPrimaryKeySelective(seckillGoods);
            //3.1.2 移除redis中该商品的数据
            redisTemplate.boundHashOps(SECKILL_KEY + time).delete(id);
        } else {
            //3.2当前购买的商品不是最后一件
            //3.2.1将库存数据更新到redis中
            redisTemplate.boundHashOps(SECKILL_KEY + time).put(id, seckillGoods);
        }
    }
}
