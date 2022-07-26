package cn.coderap.user.service.impl;

import cn.coderap.user.dao.AddressMapper;
import cn.coderap.user.pojo.Address;
import cn.coderap.user.service.AddressService;
import cn.coderap.util.TokenDecode;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> findAll() {
        return addressMapper.selectAll();
    }

    @Override
    public Address findById(Integer id){
        return addressMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Address address){
        addressMapper.insertSelective(address);
    }

    @Override
    public void update(Address address){
        addressMapper.updateByPrimaryKeySelective(address);
    }

    @Override
    public void delete(Integer id){
        addressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Address> findList(Map searchMap){
        Example example = createExample(searchMap);
        return addressMapper.selectByExample(example);
    }

    @Override
    public Page<Address> findPage(Map searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Address>)addressMapper.selectByExample(example);
    }

    @Override
    public List<Address> list() {
        //获取到令牌中的username
        String username = TokenDecode.getUserInfo().get("username");

        Address address = new Address();
        address.setUsername(username);
        return addressMapper.select(address);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Address.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 用户名
            if(searchMap.get("username")!=null && !"".equals(searchMap.get("username"))){
                criteria.andEqualTo("username",searchMap.get("username"));
           	}
            // 省
            if(searchMap.get("provinceid")!=null && !"".equals(searchMap.get("provinceid"))){
                criteria.andLike("provinceid","%"+searchMap.get("provinceid")+"%");
           	}
            // 市
            if(searchMap.get("cityid")!=null && !"".equals(searchMap.get("cityid"))){
                criteria.andLike("cityid","%"+searchMap.get("cityid")+"%");
           	}
            // 县/区
            if(searchMap.get("areaid")!=null && !"".equals(searchMap.get("areaid"))){
                criteria.andLike("areaid","%"+searchMap.get("areaid")+"%");
           	}
            // 电话
            if(searchMap.get("phone")!=null && !"".equals(searchMap.get("phone"))){
                criteria.andLike("phone","%"+searchMap.get("phone")+"%");
           	}
            // 详细地址
            if(searchMap.get("address")!=null && !"".equals(searchMap.get("address"))){
                criteria.andLike("address","%"+searchMap.get("address")+"%");
           	}
            // 联系人
            if(searchMap.get("contact")!=null && !"".equals(searchMap.get("contact"))){
                criteria.andLike("contact","%"+searchMap.get("contact")+"%");
           	}
            // 是否是默认 1默认 0否
            if(searchMap.get("isDefault")!=null && !"".equals(searchMap.get("isDefault"))){
                criteria.andEqualTo("isDefault",searchMap.get("isDefault"));
           	}
            // 别名
            if(searchMap.get("alias")!=null && !"".equals(searchMap.get("alias"))){
                criteria.andLike("alias","%"+searchMap.get("alias")+"%");
           	}
        }
        return example;
    }

}
