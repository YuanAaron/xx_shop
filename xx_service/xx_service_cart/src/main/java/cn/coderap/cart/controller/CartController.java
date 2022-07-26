package cn.coderap.cart.controller;

import cn.coderap.cart.service.CartService;
import cn.coderap.util.TokenDecode;
import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

//    private String userName = "coderap";

    /**
     * 添加到购物车分为两种情况：
     * 1、点击添加购物车
     * 2、在购物车列表页增加商品的数量
     * @param id skuId
     * @param num
     * @return
     */
    @GetMapping("/add")
    public Result add(String id, Integer num){
        String userName = TokenDecode.getUserInfo().get("username");
        cartService.add(id, num, userName);
        return new Result(true, StatusCode.OK,"添加成功");
    }

    @GetMapping("/list")
    public Map list(){
        String userName = TokenDecode.getUserInfo().get("username");
        return cartService.list(userName);
    }

    @DeleteMapping
    public Result delete(@RequestParam(name="skuId") String skuId){
        String userName = TokenDecode.getUserInfo().get("username");
        cartService.delete(skuId,userName);
        return new Result(true,StatusCode.OK,"删除成功");
    }

    @PutMapping
    public Result updateChecked(@RequestParam String skuId,@RequestParam Boolean checked){
        String userName = TokenDecode.getUserInfo().get("username");
        cartService.updateCheckedStatus(skuId,checked,userName);
        return new Result(true,StatusCode.OK,"操作成功");
    }

}

