package cn.coderap.goods.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Goods;
import cn.coderap.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /***
     * 新增数据
     * @param goods
     * @return
     */
    @PostMapping
    public Result add(@RequestBody Goods goods){
        goodsService.add(goods);
        return new Result(true, StatusCode.OK,"添加成功");
    }
}
