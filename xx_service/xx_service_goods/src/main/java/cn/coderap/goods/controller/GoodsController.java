package cn.coderap.goods.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Brand;
import cn.coderap.goods.pojo.Goods;
import cn.coderap.goods.pojo.Spu;
import cn.coderap.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /***
     * 新增商品
     * @param goods
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Goods goods){
        goodsService.add(goods);
        return new Result(true, StatusCode.OK,"添加成功");
    }

    /***
     * 根据ID查询商品(id是SPU的id，返回的是商品对象(SPU对象和SKU列表))
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Goods> findById(@PathVariable String id) {
        Goods goods = goodsService.findById(id);
        return new Result(true, StatusCode.OK, "查询成功", goods);
    }

    /***
     * 修改商品
     * @param goods
     * @param id
     * @return
     */
    @PutMapping(value="/{id}")
    public Result update(@RequestBody Goods goods, @PathVariable String id){
        goods.getSpu().setId(id);
        goodsService.update(goods);
        return new Result(true,StatusCode.OK,"修改成功");
    }

    /**
     * 商品审核（商品->审核->上架（此时审核状态必须为1)->下架->删除）
     * @param id
     * @return
     */
    @PutMapping("/audit/{id}")
    public Result audit(@PathVariable String id){
        goodsService.audit(id);
        return new Result(true,StatusCode.OK,"审核成功");
    }

    /**
     * 商品上架
     */
    @PutMapping("/put/{id}")
    public Result put(@PathVariable String id){
        goodsService.put(id);
        return new Result(true,StatusCode.OK,"上架成功");
    }
    /**
     * 商品下架
     */
    @PutMapping("/pull/{id}")
    public Result pull(@PathVariable String id){
        goodsService.pull(id);
        return new Result(true,StatusCode.OK,"下架成功");
    }

    /***
     * 根据ID逻辑删除商品
     * @param id
     * @return
     */
    @DeleteMapping(value = "/{id}" )
    public Result delete(@PathVariable String id){
        goodsService.delete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    /**
     * 还原
     * @param id
     * @return
     */
    @PutMapping("/restore/{id}")
    public Result restore(@PathVariable String id){
        goodsService.restore(id);
        return new Result(true,StatusCode.OK,"还原成功");
    }

    /***
     * 根据ID物理删除商品
     * @param id
     * @return
     */
    @DeleteMapping(value = "/realDelete/{id}" )
    public Result realDelete(@PathVariable String id){
        goodsService.realDelete(id);
        return new Result(true,StatusCode.OK,"删除成功");
    }
}
