package cn.coderap.system.service;

import cn.coderap.system.pojo.Resource;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ResourceService {
    List<Resource> findAll();

    Resource findById(Integer id);

    void add(Resource resource);

    void update(Resource resource);

    void delete(Integer id);

    List<Resource> findList(Map searchMap);

    Page<Resource> findPage(Map searchMap, int page, int size);
}
