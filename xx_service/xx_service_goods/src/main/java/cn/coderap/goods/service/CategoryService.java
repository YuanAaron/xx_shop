package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Category;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> findAll();

    Category findById(Integer id);

    void add(Category category);

    void update(Category category);

    void delete(Integer id);

    List<Category> findList(Map searchMap);

    Page<Category> findPage(Map searchMap, int page, int size);
}
