package cn.coderap.system.service;

import cn.coderap.system.pojo.Menu;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface MenuService {
    List<Menu> findAll();

    Menu findById(String id);

    void add(Menu menu);

    void update(Menu menu);

    void delete(String id);

    List<Menu> findList(Map searchMap);

    Page<Menu> findPage(Map searchMap, int page, int size);
}
