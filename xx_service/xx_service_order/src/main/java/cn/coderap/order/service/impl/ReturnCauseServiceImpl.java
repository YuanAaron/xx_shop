package cn.coderap.order.service.impl;

import cn.coderap.order.dao.ReturnCauseMapper;
import cn.coderap.order.pojo.ReturnCause;
import cn.coderap.order.service.ReturnCauseService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ReturnCauseServiceImpl implements ReturnCauseService {
    @Autowired
    private ReturnCauseMapper returnCauseMapper;

    @Override
    public List<ReturnCause> findAll() {
        return returnCauseMapper.selectAll();
    }

    @Override
    public ReturnCause findById(Integer id){
        return returnCauseMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(ReturnCause returnCause){
        returnCauseMapper.insertSelective(returnCause);
    }

    @Override
    public void update(ReturnCause returnCause){
        returnCauseMapper.updateByPrimaryKeySelective(returnCause);
    }

    @Override
    public void delete(Integer id){
        returnCauseMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<ReturnCause> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return returnCauseMapper.selectByExample(example);
    }

    @Override
    public Page<ReturnCause> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<ReturnCause>)returnCauseMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(ReturnCause.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 原因
            if(searchMap.get("cause")!=null && !"".equals(searchMap.get("cause"))){
                criteria.andLike("cause","%"+searchMap.get("cause")+"%");
           	}
            // 是否启用
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
           	}
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
        }
        return example;
    }

}
