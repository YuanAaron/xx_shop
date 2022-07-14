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
     * 修改数据
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
}
