package cn.coderap.order.service;

import cn.coderap.order.pojo.ReturnOrder;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ReturnOrderService {

    List<ReturnOrder> findAll();

    ReturnOrder findById(String id);

    void add(ReturnOrder returnOrder);

    void update(ReturnOrder returnOrder);

    void delete(String id);

    List<ReturnOrder> findList(Map<String, Object> searchMap);

    Page<ReturnOrder> findPage(Map<String, Object> searchMap, int page, int size);




}
