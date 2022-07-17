package cn.coderap.business.service;

import cn.coderap.business.pojo.Ad;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AdService {
    List<Ad> findAll();

    Ad findById(Integer id);

    void add(Ad ad);

    void update(Ad ad);

    void delete(Integer id);

    List<Ad> findList(Map searchMap);

    Page<Ad> findPage(Map searchMap, int page, int size);
}
