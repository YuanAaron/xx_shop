package cn.coderap.business.service.impl;

import cn.coderap.business.dao.ActivityMapper;
import cn.coderap.business.pojo.Activity;
import cn.coderap.business.service.ActivityService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public List<Activity> findAll() {
        return activityMapper.selectAll();
    }

    @Override
    public Activity findById(Integer id) {
        return activityMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Activity activity) {
        activityMapper.insertSelective(activity);
    }

    @Override
    public void update(Activity activity) {
        activityMapper.updateByPrimaryKeySelective(activity);
    }

    @Override
    public void delete(Integer id) {
        activityMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Activity> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return activityMapper.selectByExample(example);
    }

    @Override
    public Page<Activity> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Activity>)activityMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Activity.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 活动标题
            if(searchMap.get("title")!=null && !"".equals(searchMap.get("title"))){
                criteria.andLike("title","%"+searchMap.get("title")+"%");
            }
            // 状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
            }
            // 活动内容
            if(searchMap.get("content")!=null && !"".equals(searchMap.get("content"))){
                criteria.andLike("content","%"+searchMap.get("content")+"%");
            }

        }
        return example;
    }
}
