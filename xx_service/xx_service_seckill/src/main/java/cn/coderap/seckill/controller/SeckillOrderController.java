package cn.coderap.seckill.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.entity.SeckillStatus;
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
    public Result<SeckillGoods> add(String time, Long id, String username) {
//        String username = TokenDecode.getUserInfo().get("username");
        orderService.add(time, id, username);
        return new Result(true, StatusCode.OK, "success");
    }

    /**
     * 用于查询用户下单的状态
     * @return
     */
    @GetMapping("/query")
    public Result<SeckillStatus> queryStatus() {
        String username = TokenDecode.getUserInfo().get("username");
        SeckillStatus seckillStatus = orderService.queryStatus(username);
        if (seckillStatus != null) {
            return new Result<>(true, StatusCode.OK, "抢单成功", seckillStatus);
        }
        return new Result<>(false, StatusCode.ERROR, "抢单失败");
    }

}
