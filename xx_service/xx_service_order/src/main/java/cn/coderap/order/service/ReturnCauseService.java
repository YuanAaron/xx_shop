package cn.coderap.order.service;

import cn.coderap.order.pojo.ReturnCause;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ReturnCauseService {

    List<ReturnCause> findAll();

    ReturnCause findById(Integer id);

    void add(ReturnCause returnCause);

    void update(ReturnCause returnCause);

    void delete(Integer id);

    List<ReturnCause> findList(Map<String, Object> searchMap);

    Page<ReturnCause> findPage(Map<String, Object> searchMap, int page, int size);




}
