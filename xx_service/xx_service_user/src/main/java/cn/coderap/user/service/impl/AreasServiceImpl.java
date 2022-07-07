package cn.coderap.user.service.impl;

import cn.coderap.user.dao.AreasMapper;
import cn.coderap.user.pojo.Areas;
import cn.coderap.user.service.AreasService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AreasServiceImpl implements AreasService {
    @Autowired
    private AreasMapper areasMapper;

    @Override
    public List<Areas> findAll() {
        return areasMapper.selectAll();
    }

    @Override
    public Areas findById(String areaid){
        return areasMapper.selectByPrimaryKey(areaid);
    }

    @Override
    public void add(Areas areas){
        areasMapper.insertSelective(areas);
    }

    @Override
    public void update(Areas areas){
        areasMapper.updateByPrimaryKey(areas);
    }

    @Override
    public void delete(String areaid){
        areasMapper.deleteByPrimaryKey(areaid);
    }

    @Override
    public List<Areas> findList(Map searchMap){
        Example example = createExample(searchMap);
        return areasMapper.selectByExample(example);
    }

    @Override
    public Page<Areas> findPage(Map searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Areas>)areasMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Areas.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 区域ID
            if(searchMap.get("areaid")!=null && !"".equals(searchMap.get("areaid"))){
                criteria.andLike("areaid","%"+searchMap.get("areaid")+"%");
           	}
            // 区域名称
            if(searchMap.get("area")!=null && !"".equals(searchMap.get("area"))){
                criteria.andLike("area","%"+searchMap.get("area")+"%");
           	}
            // 城市ID
            if(searchMap.get("cityid")!=null && !"".equals(searchMap.get("cityid"))){
                criteria.andLike("cityid","%"+searchMap.get("cityid")+"%");
           	}
        }
        return example;
    }

}
