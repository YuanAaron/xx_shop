package cn.coderap.system.service;

import cn.coderap.system.pojo.LoginLog;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface LoginLogService {
    List<LoginLog> findAll();

    LoginLog findById(Integer id);

    void add(LoginLog loginLog);

    void update(LoginLog loginLog);

    void delete(Integer id);

    List<LoginLog> findList(Map searchMap);

    Page<LoginLog> findPage(Map searchMap, int page, int size);
}
