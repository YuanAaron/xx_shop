package cn.coderap.user.service;

import cn.coderap.user.pojo.Address;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AddressService {
    List<Address> findAll();

    Address findById(Integer id);

    void add(Address address);

    void update(Address address);

    void delete(Integer id);

    List<Address> findList(Map searchMap);

    Page<Address> findPage(Map searchMap, int page, int size);
}
