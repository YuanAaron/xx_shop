package cn.coderap.system.service;

import cn.coderap.system.pojo.Role;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface RoleService {
    List<Role> findAll();

    Role findById(Integer id);

    void add(Role role);

    void update(Role role);

    void delete(Integer id);

    List<Role> findList(Map searchMap);

    Page<Role> findPage(Map searchMap, int page, int size);
}
