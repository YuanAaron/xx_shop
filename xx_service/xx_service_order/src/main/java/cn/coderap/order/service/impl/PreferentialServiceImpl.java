package cn.coderap.order.service.impl;

import cn.coderap.order.dao.PreferentialMapper;
import cn.coderap.order.pojo.Preferential;
import cn.coderap.order.service.PreferentialService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class PreferentialServiceImpl implements PreferentialService {
    @Autowired
    private PreferentialMapper preferentialMapper;

    @Override
    public List<Preferential> findAll() {
        return preferentialMapper.selectAll();
    }

    @Override
    public Preferential findById(Integer id){
        return preferentialMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Preferential preferential){
        preferentialMapper.insertSelective(preferential);
    }

    @Override
    public void update(Preferential preferential){
        preferentialMapper.updateByPrimaryKeySelective(preferential);
    }

    @Override
    public void delete(Integer id){
        preferentialMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Preferential> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return preferentialMapper.selectByExample(example);
    }

    @Override
    public Page<Preferential> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Preferential>)preferentialMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Preferential.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 状态
            if(searchMap.get("state")!=null && !"".equals(searchMap.get("state"))){
                criteria.andLike("state","%"+searchMap.get("state")+"%");
           	}
            // 类型1不翻倍 2翻倍
            if(searchMap.get("type")!=null && !"".equals(searchMap.get("type"))){
                criteria.andLike("type","%"+searchMap.get("type")+"%");
           	}
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 消费金额
            if(searchMap.get("buyMoney")!=null ){
                criteria.andEqualTo("buyMoney",searchMap.get("buyMoney"));
            }
            // 优惠金额
            if(searchMap.get("preMoney")!=null ){
                criteria.andEqualTo("preMoney",searchMap.get("preMoney"));
            }
            // 品类ID
            if(searchMap.get("categoryId")!=null ){
                criteria.andEqualTo("categoryId",searchMap.get("categoryId"));
            }

        }
        return example;
    }

}
