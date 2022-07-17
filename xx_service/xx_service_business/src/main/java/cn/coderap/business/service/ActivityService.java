package cn.coderap.business.service;

import cn.coderap.business.pojo.Activity;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    List<Activity> findAll();

    Activity findById(Integer id);

    void add(Activity activity);

    void update(Activity activity);

    void delete(Integer id);

    List<Activity> findList(Map searchMap);

    Page<Activity> findPage(Map searchMap, int page, int size);
}
