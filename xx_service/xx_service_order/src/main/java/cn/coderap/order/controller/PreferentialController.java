package cn.coderap.order.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.PageResult;
import cn.coderap.entity.Result;
import cn.coderap.order.pojo.Preferential;
import cn.coderap.order.service.PreferentialService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/preferential")
public class PreferentialController {
    @Autowired
    private PreferentialService preferentialService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Preferential> preferentialList = preferentialService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",preferentialList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Preferential preferential = preferentialService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",preferential);
    }

    /***
     * 新增数据
     * @param preferential
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Preferential preferential){
        preferentialService.add(preferential);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 修改数据
     * @param preferential
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Preferential preferential,@PathVariable Integer id){
        preferential.setId(id);
        preferentialService.update(preferential);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 根据ID删除数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        preferentialService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Preferential> list = preferentialService.findList(searchMap);
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
        Page<Preferential> pageList = preferentialService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

}
