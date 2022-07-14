package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Goods;

public interface GoodsService {
    void add(Goods goods);

    Goods findById(String id);

    void update(Goods goods);

    void audit(String id);

    void put(String id);

    void pull(String id);

    void delete(String id);

    void restore(String id);

    void realDelete(String id);
}
