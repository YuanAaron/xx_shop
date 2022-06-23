package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.SpecMapper;
import cn.coderap.goods.pojo.Spec;
import cn.coderap.goods.service.SpecService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {

    @Autowired
    private SpecMapper specMapper;

    @Override
    public List<Spec> findAll() {
        return specMapper.selectAll();
    }

    @Override
    public Spec findById(Integer id) {
        return specMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Spec spec) {
        specMapper.insertSelective(spec);
    }

    @Override
    public void update(Spec spec) {
        specMapper.updateByPrimaryKeySelective(spec);
    }

    @Override
    public void delete(Integer id) {
        specMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Spec> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return specMapper.selectByExample(example);
    }

    @Override
    public Page<Spec> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Spec>)specMapper.selectByExample(example);
    }

    @Override
    public List<Spec> findByCategoryName(String categoryName) {
        return specMapper.selectByCategoryName(categoryName);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Spec.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null ) {
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 规格选项
            if(searchMap.get("options")!=null && !"".equals(searchMap.get("options"))){
                criteria.andLike("options","%"+searchMap.get("options")+"%");
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
            // 模板ID
            if(searchMap.get("templateId")!=null ){
                criteria.andEqualTo("templateId",searchMap.get("templateId"));
            }
        }
        return example;
    }
}
