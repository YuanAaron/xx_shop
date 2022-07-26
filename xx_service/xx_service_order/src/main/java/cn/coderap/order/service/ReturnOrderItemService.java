package cn.coderap.order.service;

import cn.coderap.order.pojo.ReturnOrderItem;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ReturnOrderItemService {

    List<ReturnOrderItem> findAll();

    ReturnOrderItem findById(String id);

    void add(ReturnOrderItem returnOrderItem);

    void update(ReturnOrderItem returnOrderItem);

    void delete(String id);

    List<ReturnOrderItem> findList(Map<String, Object> searchMap);

    Page<ReturnOrderItem> findPage(Map<String, Object> searchMap, int page, int size);




}
