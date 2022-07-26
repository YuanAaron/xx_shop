package cn.coderap.order.service;

import cn.coderap.order.pojo.OrderLog;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderLogService {

    List<OrderLog> findAll();

    OrderLog findById(String id);

    void add(OrderLog orderLog);

    void update(OrderLog orderLog);

    void delete(String id);

    List<OrderLog> findList(Map<String, Object> searchMap);

    Page<OrderLog> findPage(Map<String, Object> searchMap, int page, int size);




}
