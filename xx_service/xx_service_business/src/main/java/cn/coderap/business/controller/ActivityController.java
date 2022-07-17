package cn.coderap.business.controller;

import cn.coderap.business.pojo.Activity;
import cn.coderap.business.service.ActivityService;
import cn.coderap.constant.StatusCode;
import cn.coderap.entity.PageResult;
import cn.coderap.entity.Result;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Activity> activityList = activityService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",activityList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Activity activity = activityService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",activity);
    }

    /***
     * 新增数据
     * @param activity
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Activity activity){
        activityService.add(activity);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 修改数据
     * @param activity
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Activity activity,@PathVariable Integer id){
        activity.setId(id);
        activityService.update(activity);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 根据ID删除数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        activityService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Activity> list = activityService.findList(searchMap);
        return new Result(true,StatusCode.OK,"查询成功",list);
    }

    /***
     * 分页查询
     * @param searchMap
     * @param page
     * @param size
     * @return
     */
    @GetMapping(value = "/search/{page}/{size}" )
    public Result findPage(@RequestParam Map searchMap, @PathVariable  int page, @PathVariable  int size){
        Page<Activity> pageList = activityService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

}
