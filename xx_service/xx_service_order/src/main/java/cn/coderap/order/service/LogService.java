package cn.coderap.order.service;

import cn.coderap.order.pojo.Log;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface LogService {

    List<Log> findAll();

    Log findById(Long id);

    void add(Log log);

    void update(Log log);

    void delete(Long id);

    List<Log> findList(Map<String, Object> searchMap);

    Page<Log> findPage(Map<String, Object> searchMap, int page, int size);

}
