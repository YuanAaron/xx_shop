package cn.coderap.order.service;

import cn.coderap.order.pojo.OrderConfig;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface OrderConfigService {

    List<OrderConfig> findAll();

    OrderConfig findById(Integer id);

    void add(OrderConfig orderConfig);

    void update(OrderConfig orderConfig);

    void delete(Integer id);

    List<OrderConfig> findList(Map<String, Object> searchMap);

    Page<OrderConfig> findPage(Map<String, Object> searchMap, int page, int size);

}
