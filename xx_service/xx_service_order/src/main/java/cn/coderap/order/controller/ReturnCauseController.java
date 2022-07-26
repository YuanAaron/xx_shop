package cn.coderap.order.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.PageResult;
import cn.coderap.entity.Result;
import cn.coderap.order.pojo.ReturnCause;
import cn.coderap.order.service.ReturnCauseService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/returnCause")
public class ReturnCauseController {
    @Autowired
    private ReturnCauseService returnCauseService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<ReturnCause> returnCauseList = returnCauseService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",returnCauseList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        ReturnCause returnCause = returnCauseService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",returnCause);
    }

    /***
     * 新增数据
     * @param returnCause
     * @return
     */
    @PostMapping
    public Result add(@RequestBody ReturnCause returnCause){
        returnCauseService.add(returnCause);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 修改数据
     * @param returnCause
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody ReturnCause returnCause,@PathVariable Integer id){
        returnCause.setId(id);
        returnCauseService.update(returnCause);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 根据ID删除数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        returnCauseService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<ReturnCause> list = returnCauseService.findList(searchMap);
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
        Page<ReturnCause> pageList = returnCauseService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

}
