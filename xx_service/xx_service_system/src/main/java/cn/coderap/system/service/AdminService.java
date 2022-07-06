package cn.coderap.system.service;

import cn.coderap.system.pojo.Admin;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AdminService {
    List<Admin> findAll();

    Admin findById(Integer id);

    void add(Admin admin);

    void update(Admin admin);

    void delete(Integer id);

    List<Admin> findList(Map searchMap);

    Page<Admin> findPage(Map searchMap, int page, int size);

    boolean login(Admin admin);
}
