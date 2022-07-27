package cn.coderap.user.service.impl;

import cn.coderap.user.dao.UserMapper;
import cn.coderap.user.pojo.User;
import cn.coderap.user.service.UserService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public List<User> findAll() {
        return userMapper.selectAll();
    }

    @Override
    public User findById(String username){
        return userMapper.selectByPrimaryKey(username);
    }

    @Override
    public void add(User user){
        userMapper.insertSelective(user);
    }

    @Override
    public void update(User user){
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void delete(String username){
        userMapper.deleteByPrimaryKey(username);
    }

    @Override
    public List<User> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return userMapper.selectByExample(example);
    }

    @Override
    public Page<User> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<User>)userMapper.selectByExample(example);
    }

    @Override
    public void addUserPoints(String username, Integer points) {
        int rows = userMapper.addPoints(username,points);
        System.out.println(rows);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 用户名
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andEqualTo("username",searchMap.get("username"));
           	}
            // 密码，加密存储
            if(searchMap.get("password")!=null && !"".equals(searchMap.get("password"))){
                criteria.andEqualTo("password",searchMap.get("password"));
           	}
            // 注册手机号
            if(searchMap.get("phone")!=null && !"".equals(searchMap.get("phone"))){
                criteria.andLike("phone","%"+searchMap.get("phone")+"%");
           	}
            // 注册邮箱
            if(searchMap.get("email")!=null && !"".equals(searchMap.get("email"))){
                criteria.andLike("email","%"+searchMap.get("email")+"%");
           	}
            // 会员来源：1:PC，2：H5，3：Android，4：IOS
            if(searchMap.get("sourceType")!=null && !"".equals(searchMap.get("sourceType"))){
                criteria.andEqualTo("sourceType",searchMap.get("sourceType"));
           	}
            // 昵称
            if(searchMap.get("nickName")!=null && !"".equals(searchMap.get("nickName"))){
                criteria.andLike("nickName","%"+searchMap.get("nickName")+"%");
           	}
            // 真实姓名
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 使用状态（1正常 0非正常）
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status",searchMap.get("status"));
           	}
            // 头像地址
            if(searchMap.get("headPic")!=null && !"".equals(searchMap.get("headPic"))){
                criteria.andLike("headPic","%"+searchMap.get("headPic")+"%");
           	}
            // QQ号码
            if(searchMap.get("qq")!=null && !"".equals(searchMap.get("qq"))){
                criteria.andLike("qq","%"+searchMap.get("qq")+"%");
           	}
            // 手机是否验证 （0否  1是）
            if(searchMap.get("isMobileCheck")!=null && !"".equals(searchMap.get("isMobileCheck"))){
                criteria.andEqualTo("isMobileCheck",searchMap.get("isMobileCheck"));
           	}
            // 邮箱是否检测（0否  1是）
            if(searchMap.get("isEmailCheck")!=null && !"".equals(searchMap.get("isEmailCheck"))){
                criteria.andEqualTo("isEmailCheck",searchMap.get("isEmailCheck"));
           	}
            // 性别，1男，0女
            if(searchMap.get("sex")!=null && !"".equals(searchMap.get("sex"))){
                criteria.andEqualTo("sex",searchMap.get("sex"));
           	}
            // 会员等级
            if(searchMap.get("userLevel")!=null ){
                criteria.andEqualTo("userLevel",searchMap.get("userLevel"));
            }
            // 积分
            if(searchMap.get("points")!=null ){
                criteria.andEqualTo("points",searchMap.get("points"));
            }
            // 经验值
            if(searchMap.get("experienceValue")!=null ){
                criteria.andEqualTo("experienceValue",searchMap.get("experienceValue"));
            }
        }
        return example;
    }

}
