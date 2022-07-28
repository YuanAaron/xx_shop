package cn.coderap.seckill.service.impl;

import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private RedisTemplate redisTemplate;

    private static final String SECKILL_GOODS = "SeckillGoods_";

    @Override
    public List<SeckillGoods> list(String time) {
        return redisTemplate.boundHashOps(SECKILL_GOODS + time).values();
    }

    @Override
    public SeckillGoods select(String time, Long id) {
        return (SeckillGoods)redisTemplate.boundHashOps(SECKILL_GOODS + time).get(id);
    }
}
