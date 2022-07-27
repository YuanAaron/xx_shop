package cn.coderap.seckill.service;

import cn.coderap.seckill.pojo.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {
    SeckillGoods select(String time, Long id);

    List<SeckillGoods> list(String time);
}
