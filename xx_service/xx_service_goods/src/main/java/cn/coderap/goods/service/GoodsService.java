package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Goods;

public interface GoodsService {
    void add(Goods goods);

    Goods findById(String id);

    void update(Goods goods);
}
