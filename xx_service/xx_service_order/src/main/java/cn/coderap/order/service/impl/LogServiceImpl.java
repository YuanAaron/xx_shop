package cn.coderap.order.service.impl;

import cn.coderap.order.dao.LogMapper;
import cn.coderap.order.pojo.Log;
import cn.coderap.order.service.LogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {
    @Autowired
    private LogMapper logMapper;

    @Override
    public List<Log> findAll() {
        return logMapper.selectAll();
    }

    @Override
    public Log findById(Long id){
        return  logMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Log log){
        logMapper.insertSelective(log);
    }

    @Override
    public void update(Log log){
        logMapper.updateByPrimaryKeySelective(log);
    }

    @Override
    public void delete(Long id){
        logMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Log> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return logMapper.selectByExample(example);
    }

    @Override
    public Page<Log> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Log>)logMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Log.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // xid
            if(searchMap.get("xid")!=null && !"".equals(searchMap.get("xid"))){
                criteria.andLike("xid","%"+searchMap.get("xid")+"%");
           	}
            // ext
            if(searchMap.get("ext")!=null && !"".equals(searchMap.get("ext"))){
                criteria.andLike("ext","%"+searchMap.get("ext")+"%");
           	}
            // log_status
            if(searchMap.get("logStatus")!=null ){
                criteria.andEqualTo("logStatus",searchMap.get("logStatus"));
            }
        }
        return example;
    }

}
