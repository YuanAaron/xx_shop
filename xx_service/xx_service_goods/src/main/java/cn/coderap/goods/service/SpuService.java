package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Spu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpuService {
    List<Spu> findAll();

    Spu findById(String id);

    void add(Spu spu);

    void update(Spu spu);

    void delete(String id);

    List<Spu> findList(Map searchMap);

    Page<Spu> findPage(Map searchMap, int page, int size);
}
