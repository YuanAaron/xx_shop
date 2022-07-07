package cn.coderap.user.service.impl;

import cn.coderap.user.dao.CitiesMapper;
import cn.coderap.user.pojo.Cities;
import cn.coderap.user.service.CitiesService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class CitiesServiceImpl implements CitiesService {
    @Autowired
    private CitiesMapper citiesMapper;

    @Override
    public List<Cities> findAll() {
        return citiesMapper.selectAll();
    }

    @Override
    public Cities findById(String cityid){
        return citiesMapper.selectByPrimaryKey(cityid);
    }

    @Override
    public void add(Cities cities){
        citiesMapper.insertSelective(cities);
    }

    @Override
    public void update(Cities cities){
        citiesMapper.updateByPrimaryKeySelective(cities);
    }

    @Override
    public void delete(String cityid){
        citiesMapper.deleteByPrimaryKey(cityid);
    }

    @Override
    public List<Cities> findList(Map searchMap){
        Example example = createExample(searchMap);
        return citiesMapper.selectByExample(example);
    }

    @Override
    public Page<Cities> findPage(Map searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Cities>)citiesMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Cities.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 城市ID
            if(searchMap.get("cityid")!=null && !"".equals(searchMap.get("cityid"))){
                criteria.andLike("cityid","%"+searchMap.get("cityid")+"%");
           	}
            // 城市名称
            if(searchMap.get("city")!=null && !"".equals(searchMap.get("city"))){
                criteria.andLike("city","%"+searchMap.get("city")+"%");
           	}
            // 省份ID
            if(searchMap.get("provinceid")!=null && !"".equals(searchMap.get("provinceid"))){
                criteria.andLike("provinceid","%"+searchMap.get("provinceid")+"%");
           	}
        }
        return example;
    }

}
