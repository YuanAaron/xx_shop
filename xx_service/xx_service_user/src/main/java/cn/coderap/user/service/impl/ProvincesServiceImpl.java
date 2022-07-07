package cn.coderap.user.service.impl;

import cn.coderap.user.dao.ProvincesMapper;
import cn.coderap.user.pojo.Provinces;
import cn.coderap.user.service.ProvincesService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ProvincesServiceImpl implements ProvincesService {
    @Autowired
    private ProvincesMapper provincesMapper;

    @Override
    public List<Provinces> findAll() {
        return provincesMapper.selectAll();
    }

    @Override
    public Provinces findById(String provinceid){
        return provincesMapper.selectByPrimaryKey(provinceid);
    }

    @Override
    public void add(Provinces provinces){
        provincesMapper.insertSelective(provinces);
    }

    @Override
    public void update(Provinces provinces){
        provincesMapper.updateByPrimaryKeySelective(provinces);
    }

    @Override
    public void delete(String provinceid){
        provincesMapper.deleteByPrimaryKey(provinceid);
    }

    @Override
    public List<Provinces> findList(Map searchMap){
        Example example = createExample(searchMap);
        return provincesMapper.selectByExample(example);
    }

    @Override
    public Page<Provinces> findPage(Map searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Provinces>)provincesMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Provinces.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 省份ID
            if(searchMap.get("provinceid")!=null && !"".equals(searchMap.get("provinceid"))){
                criteria.andLike("provinceid","%"+searchMap.get("provinceid")+"%");
           	}
            // 省份名称
            if(searchMap.get("province")!=null && !"".equals(searchMap.get("province"))){
                criteria.andLike("province","%"+searchMap.get("province")+"%");
           	}
        }
        return example;
    }

}
