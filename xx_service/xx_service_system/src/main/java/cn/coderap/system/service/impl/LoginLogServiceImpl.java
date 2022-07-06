package cn.coderap.system.service.impl;

import cn.coderap.system.dao.LoginLogMapper;
import cn.coderap.system.pojo.LoginLog;
import cn.coderap.system.service.LoginLogService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class LoginLogServiceImpl implements LoginLogService {

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Override
    public List<LoginLog> findAll() {
        return loginLogMapper.selectAll();
    }

    @Override
    public LoginLog findById(Integer id) {
        return loginLogMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(LoginLog loginLog) {
        loginLogMapper.insert(loginLog);
    }

    @Override
    public void update(LoginLog loginLog) {
        loginLogMapper.updateByPrimaryKeySelective(loginLog);
    }

    @Override
    public void delete(Integer id) {
        loginLogMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<LoginLog> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return loginLogMapper.selectByExample(example);
    }

    @Override
    public Page<LoginLog> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<LoginLog>)loginLogMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(LoginLog.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // login_name
            if(searchMap.get("login_name")!=null && !"".equals(searchMap.get("login_name"))){
                criteria.andLike("login_name","%"+searchMap.get("login_name")+"%");
            }
            // ip
            if(searchMap.get("ip")!=null && !"".equals(searchMap.get("ip"))){
                criteria.andLike("ip","%"+searchMap.get("ip")+"%");
            }
            // browser_name
            if(searchMap.get("browser_name")!=null && !"".equals(searchMap.get("browser_name"))){
                criteria.andLike("browser_name","%"+searchMap.get("browser_name")+"%");
            }
            // 地区
            if(searchMap.get("location")!=null && !"".equals(searchMap.get("location"))){
                criteria.andLike("location","%"+searchMap.get("location")+"%");
            }
        }
        return example;
    }
}
