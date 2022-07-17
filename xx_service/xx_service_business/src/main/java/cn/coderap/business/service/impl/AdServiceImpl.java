package cn.coderap.business.service.impl;

import cn.coderap.business.dao.AdMapper;
import cn.coderap.business.pojo.Ad;
import cn.coderap.business.service.AdService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdServiceImpl implements AdService {
    @Autowired
    private AdMapper adMapper;

    @Override
    public List<Ad> findAll() {
        return adMapper.selectAll();
    }

    @Override
    public Ad findById(Integer id) {
        return adMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Ad ad) {
        adMapper.insertSelective(ad);
    }

    @Override
    public void update(Ad ad) {
        adMapper.updateByPrimaryKeySelective(ad);
    }

    @Override
    public void delete(Integer id) {
        adMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Ad> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return adMapper.selectByExample(example);
    }

    @Override
    public Page<Ad> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Ad>)adMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Ad.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 广告名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 广告位置
            if(searchMap.get("position")!=null && !"".equals(searchMap.get("position"))){
                criteria.andLike("position","%"+searchMap.get("position")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status", searchMap.get("status"));
            }
            // 图片地址
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // URL
            if(searchMap.get("url")!=null && !"".equals(searchMap.get("url"))){
                criteria.andLike("url","%"+searchMap.get("url")+"%");
            }
            // 备注
            if(searchMap.get("remarks")!=null && !"".equals(searchMap.get("remarks"))){
                criteria.andLike("remarks","%"+searchMap.get("remarks")+"%");
            }
        }
        return example;
    }
}
