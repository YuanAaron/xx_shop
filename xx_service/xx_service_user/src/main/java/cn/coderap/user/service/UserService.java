package cn.coderap.user.service;

import cn.coderap.order.pojo.Task;
import cn.coderap.user.pojo.User;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> findAll();

    User findById(String username);

    void add(User user);

    void update(User user);

    void delete(String username);

    List<User> findList(Map searchMap);

    Page<User> findPage(Map searchMap, int page, int size);

    void addUserPoints(String username, Integer points);

    int addUserPoints(Task task);
}
