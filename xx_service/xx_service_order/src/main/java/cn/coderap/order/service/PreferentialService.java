package cn.coderap.order.service;

import cn.coderap.order.pojo.Preferential;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface PreferentialService {

    List<Preferential> findAll();

    Preferential findById(Integer id);

    void add(Preferential preferential);

    void update(Preferential preferential);

    void delete(Integer id);

    List<Preferential> findList(Map<String, Object> searchMap);

    Page<Preferential> findPage(Map<String, Object> searchMap, int page, int size);




}
