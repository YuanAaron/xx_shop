package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Sku;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SkuService {
    List<Sku> findAll();

    Sku findById(String id);

    void add(Sku sku);

    void update(Sku sku);

    void delete(String id);

    List<Sku> findList(Map searchMap);

    Page<Sku> findPage(Map searchMap, int page, int size);
}
