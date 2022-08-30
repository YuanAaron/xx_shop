package cn.coderap.goods.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.PageResult;
import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Category;
import cn.coderap.goods.pojo.vo.Category2Vo;
import cn.coderap.goods.service.CategoryService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

//@CrossOrigin
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 查询所有分类及子分类，并以树形结构组装起来
     * @return
     */
    @GetMapping
    public Result findAll(){
        List<Category> categoryList = categoryService.findAllWithTree();
        return new Result(true, StatusCode.OK,"查询成功",categoryList) ;
    }

    /***
     * 根据ID查询数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result findById(@PathVariable Integer id){
        Category category = categoryService.findById(id);
        return new Result(true,StatusCode.OK,"查询成功",category);
    }

    /***
     * 新增数据
     * @param category
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Category category){
        categoryService.add(category);
        return new Result(true,StatusCode.OK,"添加成功");
    }

    /***
     * 修改数据
     * @param category
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Category category,@PathVariable Integer id){
        category.setId(id);
        categoryService.update(category);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /***
     * 根据ID删除分类数据
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable Integer id){
        categoryService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /***
     * 多条件查询分类数据
     * @param searchMap
     * @return
     */
    @GetMapping(value = "/search" )
    public Result findList(@RequestParam Map searchMap){
        List<Category> list = categoryService.findList(searchMap);
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
        Page<Category> pageList = categoryService.findPage(searchMap, page, size);
        PageResult pageResult=new PageResult(pageList.getTotal(),pageList.getResult());
        return new Result(true,StatusCode.OK,"查询成功",pageResult);
    }

    /**
     * 获取一级分类
     */
    @GetMapping("/cat1")
    public Result cat1() {
        List<Category> category1List = categoryService.getCategory1List();
        return new Result(true,StatusCode.OK,"获取一级分类成功",category1List);
    }

    /**
     * 获取二级分类及三级小分类(懒加载）
     */
    @GetMapping("/subCat/{id}")
    public Result subCat(@PathVariable("id") Integer id) {
        List<Category2Vo> category2VoList = categoryService.getSubCategory2List(id);
        return new Result(true,StatusCode.OK,"获取二级分类及三级小分类成功",category2VoList);
    }

    /**
     * 获取二级分类及三级小分类(非懒加载）
     *
     * 1、哪些数据适合放入缓存？
     *    1.1、即时性、数据一致性要求不高的；
     *    1.2、访问量大但更新频率不高的数据（读多写少）；
     * 2、高并发下缓存失效
     *    2.1、缓存穿透：【查询一个一定不存在的数据】，由于缓存不命中，将去查询数据库，但数据库中也无此记录，而我们没有将这次查询返回的null写入缓存，导致这个不存在的数据每次请求都要到存储层去查询，失去了缓存的意义；
     *         2.1.1、风险：利用不存在的数据进行攻击，数据库瞬时压力增大，最终导致崩溃；
     *         2.1.2、解决办法：将null结果缓存，并加入短暂过期时间
     *    2.2、缓存雪崩：设置缓存时大量key使用了相同的过期时间，导致【大量key在某一时刻同时失效】，请求全部转发到数据库，导致数据库瞬时压力过大而雪崩
     *         2.2.1、解决办法：在原有的失效时间基础上增加一个随机值，比如1-5min，这样每个缓存的过期时间的重复率就会降低，就很难引发集体失效事件
     *    2.3、缓存击穿：对于一些设置了过期时间的key，如果这些key在某个时间点被超高并发访问，是一种非常热点的数据；如果【某个key在大量请求同时进来前正好失效】，那么所有对这个key的数据查询都落到数据库上
     *         2.3.1、解决办法：加锁，即大量并发只让一个去查，其他人等待，查到以后释放锁，其他人获取到锁，先查缓存，就会有数据，不用再去查数据库
     */
    @GetMapping("/subCat")
    public Result subCat() {
        Map<String,List<Category2Vo>> subCategory2Map = categoryService.getSubCategory2Map();
        return new Result(true,StatusCode.OK,"获取二级分类及三级小分类成功",subCategory2Map);
    }
}
