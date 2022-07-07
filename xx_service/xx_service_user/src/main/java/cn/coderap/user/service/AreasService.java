package cn.coderap.user.service;

import cn.coderap.user.pojo.Areas;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

public interface AreasService {
    List<Areas> findAll();

    Areas findById(String areaid);

    void add(Areas areas);

    void update(Areas areas);

    void delete(String areaid);

    List<Areas> findList(Map searchMap);

    Page<Areas> findPage(Map searchMap, int page, int size);
}
