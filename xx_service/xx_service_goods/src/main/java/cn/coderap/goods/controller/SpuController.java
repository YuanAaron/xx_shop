package cn.coderap.goods.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.PageResult;
import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Spu;
import cn.coderap.goods.service.SpuService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/spu")
public class SpuController {
    @Autowired
    private SpuService spuService;

    /**
     * 查询全部数据
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Spu> spuList = spuService.findAll();
        return new Result(true, StatusCode.OK,"查询成功",spuList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Spu> findById(@PathVariable String id){
        Spu spu = spuService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",spu);
    }

    /***
     * 新增数据
     * @param spu
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Spu spu){
        spuService.add(spu);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 修改数据
     * @param spu
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Spu spu,@PathVariable String id){
        spu.setId(id);
        spuService.update(spu);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 根据ID删除spu数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        spuService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件搜索spu数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Spu> list = spuService.findList(searchMap);
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
        Page<Spu> pageList = spuService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

}
