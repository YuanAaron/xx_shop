package cn.coderap.seckill.service;

import cn.coderap.entity.SeckillStatus;

import java.util.Map;

public interface SeckillOrderService {

    void add(String time, Long id, String username);

    SeckillStatus queryStatus(String username);

    void updateSeckillOrderStatus(String username, Map<String, String> map);
}
