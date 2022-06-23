package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Brand;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface BrandService {

    List<Brand> findAll();

    Brand findById(Integer id);

    void add(Brand brand);

    void update(Brand brand);

    void delete(Integer id);

    List<Brand> findList(Map searchMap);

    Page<Brand> findPage(Map searchMap, int page, int size);

    List<Brand> findByCategoryName(String categoryName);
}
