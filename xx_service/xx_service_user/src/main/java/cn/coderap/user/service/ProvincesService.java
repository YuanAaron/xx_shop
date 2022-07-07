package cn.coderap.user.service;

import cn.coderap.user.pojo.Provinces;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ProvincesService {
    List<Provinces> findAll();

    Provinces findById(String provinceid);

    void add(Provinces provinces);

    void update(Provinces provinces);

    void delete(String provinceid);

    List<Provinces> findList(Map searchMap);

    Page<Provinces> findPage(Map searchMap, int page, int size);
}
