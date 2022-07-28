package cn.coderap.seckill.service;

import cn.coderap.entity.SeckillStatus;

public interface SeckillOrderService {

    void add(String time, Long id, String username);

    SeckillStatus queryStatus(String username);
}
