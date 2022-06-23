package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Para;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ParaService {
    List<Para> findAll();

    Para findById(Integer id);

    void add(Para para);

    void update(Para para);

    void delete(Integer id);

    List<Para> findList(Map searchMap);

    Page<Para> findPage(Map searchMap, int page, int size);
}
