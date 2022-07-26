package cn.coderap.order.service;

import cn.coderap.order.pojo.OrderItem;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderItemService {

    List<OrderItem> findAll();

    OrderItem findById(String id);

    void add(OrderItem orderItem);

    void update(OrderItem orderItem);

    void delete(String id);

    List<OrderItem> findList(Map<String, Object> searchMap);

    Page<OrderItem> findPage(Map<String, Object> searchMap, int page, int size);

}
