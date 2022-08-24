package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Category;
import cn.coderap.goods.pojo.vo.Category2Vo;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface CategoryService {
    List<Category> findAllWithTree();

    Category findById(Integer id);

    void add(Category category);

    void update(Category category);

    void delete(Integer id);

    List<Category> findList(Map searchMap);

    Page<Category> findPage(Map searchMap, int page, int size);

    List<Category> getCategory1List();

    List<Category2Vo> getSubCategory2List(Integer id);

    Map<String, List<Category2Vo>> getSubCategory2Map();
}
