package cn.coderap.user.service;

import cn.coderap.user.pojo.Cities;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface CitiesService {
    List<Cities> findAll();

    Cities findById(String cityid);

    void add(Cities cities);

    void update(Cities cities);

    void delete(String cityid);

    List<Cities> findList(Map searchMap);

    Page<Cities> findPage(Map searchMap, int page, int size);
}
