package cn.coderap.goods.service;

import cn.coderap.goods.pojo.Template;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface TemplateService {
    List<Template> findAll();

    Template findById(Integer id);

    void add(Template template);

    void update(Template template);

    void delete(Integer id);

    List<Template> findList(Map searchMap);

    Page<Template> findPage(Map searchMap, int page, int size);
}
