package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.ParaMapper;
import cn.coderap.goods.pojo.Para;
import cn.coderap.goods.service.ParaService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ParaServiceImpl implements ParaService {

    @Autowired
    private ParaMapper paraMapper;

    @Override
    public List<Para> findAll() {
        return paraMapper.selectAll();
    }

    @Override
    public Para findById(Integer id) {
        return paraMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Para para) {
        paraMapper.insertSelective(para);
    }

    @Override
    public void update(Para para) {
        paraMapper.updateByPrimaryKeySelective(para);
    }

    @Override
    public void delete(Integer id) {
        paraMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Para> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return paraMapper.selectByExample(example);
    }

    @Override
    public Page<Para> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Para>)paraMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Para.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 选项
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
