package cn.coderap.system.service.impl;

import cn.coderap.system.dao.ResourceMapper;
import cn.coderap.system.pojo.Resource;
import cn.coderap.system.service.ResourceService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public List<Resource> findAll() {
        return resourceMapper.selectAll();
    }

    @Override
    public Resource findById(Integer id) {
        return resourceMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Resource resource) {
        resourceMapper.insertSelective(resource);
    }

    @Override
    public void update(Resource resource) {
        resourceMapper.updateByPrimaryKey(resource);
    }

    @Override
    public void delete(Integer id) {
        resourceMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Resource> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return resourceMapper.selectByExample(example);
    }

    @Override
    public Page<Resource> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Resource>)resourceMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Resource.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // res_key
            if(searchMap.get("res_key")!=null && !"".equals(searchMap.get("res_key"))){
                criteria.andLike("res_key","%"+searchMap.get("res_key")+"%");
            }
            // res_name
            if(searchMap.get("res_name")!=null && !"".equals(searchMap.get("res_name"))){
                criteria.andLike("res_name","%"+searchMap.get("res_name")+"%");
            }
            // parent_id
            if(searchMap.get("parentId")!=null ){
                criteria.andEqualTo("parentId",searchMap.get("parentId"));
            }
        }
        return example;
    }
}
