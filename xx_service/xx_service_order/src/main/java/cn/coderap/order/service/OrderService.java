package cn.coderap.order.service;

import cn.coderap.order.pojo.Order;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderService {

    List<Order> findAll();

    Order findById(String id);

    void add(Order order);

    void update(Order order);

    void delete(String id);

    List<Order> findList(Map<String, Object> searchMap);

    Page<Order> findPage(Map<String, Object> searchMap, int page, int size);

    void changeOrderStatusAndOrderLog(Map<String, String> map);
}
