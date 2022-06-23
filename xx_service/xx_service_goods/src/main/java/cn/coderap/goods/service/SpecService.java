package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Spec;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface SpecService {
    public List<Spec> findAll();

    public Spec findById(Integer id);

    public void add(Spec spec);

    public void update(Spec spec);

    public void delete(Integer id);

    public List<Spec> findList(Map searchMap);

    public Page<Spec> findPage(Map searchMap, int page, int size);

    List<Spec> findByCategoryName(String categoryName);
}
