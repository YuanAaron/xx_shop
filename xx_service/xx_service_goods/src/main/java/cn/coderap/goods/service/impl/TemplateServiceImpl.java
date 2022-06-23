package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.TemplateMapper;
import cn.coderap.goods.pojo.Template;
import cn.coderap.goods.service.TemplateService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateMapper templateMapper;

    @Override
    public List<Template> findAll() {
        return templateMapper.selectAll();
    }

    @Override
    public Template findById(Integer id) {
        return templateMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Template template) {
        templateMapper.insertSelective(template);
    }

    @Override
    public void update(Template template) {
        templateMapper.updateByPrimaryKeySelective(template);
    }

    @Override
    public void delete(Integer id) {
        templateMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Template> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return templateMapper.selectByExample(example);
    }

    @Override
    public Page<Template> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Template>)templateMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Template.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 模板名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 规格数量
            if(searchMap.get("specNum")!=null ){
                criteria.andEqualTo("specNum",searchMap.get("specNum"));
            }
            // 参数数量
            if(searchMap.get("paraNum")!=null ){
                criteria.andEqualTo("paraNum",searchMap.get("paraNum"));
            }
        }
        return example;
    }
}
