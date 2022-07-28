package cn.coderap.seckill.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.service.SeckillOrderService;
import cn.coderap.util.TokenDecode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class SeckillOrderController {
    @Autowired
    private SeckillOrderService orderService;

    /**
     * 秒杀下单
     * @param time 时间项
     * @param id   秒杀商品id
     * @return
     */
    @GetMapping("/add")
    public Result<SeckillGoods> add(String time, Long id) {
        String username = TokenDecode.getUserInfo().get("username");
        orderService.add(time, id, username);
        return new Result(true, StatusCode.OK, "success");
    }

}
