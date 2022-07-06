package cn.coderap.system.service.impl;

import cn.coderap.system.dao.AdminMapper;
import cn.coderap.system.pojo.Admin;
import cn.coderap.system.service.AdminService;
import cn.coderap.util.BCrypt;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public List<Admin> findAll() {
        return adminMapper.selectAll();
    }

    @Override
    public Admin findById(Integer id) {
        return adminMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Admin admin) {
        String hashpw = BCrypt.hashpw(admin.getPassword(), BCrypt.gensalt());
        admin.setPassword(hashpw);
        adminMapper.insertSelective(admin);
    }

    @Override
    public void update(Admin admin) {
        adminMapper.updateByPrimaryKeySelective(admin);
    }

    @Override
    public void delete(Integer id) {
        adminMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Admin> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return adminMapper.selectByExample(example);
    }

    @Override
    public Page<Admin> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Admin>)adminMapper.selectByExample(example);
    }

    @Override
    public boolean login(Admin admin) {
        Admin query = new Admin();
        query.setLoginName(admin.getLoginName());
        query.setStatus("1");
        // 根据用户名和状态查询出Admin
        Admin res = adminMapper.selectOne(query);
        if (res == null) {
            return false;
        } else {
            return BCrypt.checkpw(admin.getPassword(),res.getPassword());
        }
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Admin.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 用户名
            if(searchMap.get("loginName")!=null && !"".equals(searchMap.get("loginName"))){
                criteria.andLike("loginName","%"+searchMap.get("loginName")+"%");
            }
            // 密码
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andLike("password","%"+searchMap.get("password")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
            }
        }
        return example;
    }
}
