package cn.coderap.seckill.controller;

import cn.coderap.constant.StatusCode;
import cn.coderap.entity.Result;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.seckill.service.SeckillGoodsService;
import cn.coderap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/seckillGoods")
public class SeckillGoodsController {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 时间菜单
     * @return
     */
    @GetMapping("/timeMenus")
    public Result dateMenus(){
        List<Date> dateList = DateUtil.getDateMenus();
        List<String> dateStringList = new ArrayList<>(dateList.size());
        for (Date date : dateList) {
            dateStringList.add(DateUtil.data2str(date, "yyyy-MM-dd HH:mm:ss"));
        }
        return new Result(true, StatusCode.OK,"success",dateStringList);
    }

    /**
     * 通过时间项获得秒杀商品列表
     * @param time  格式:yyyyMMddHH
     * @return
     */
    @GetMapping("/list")
    public Result<List<SeckillGoods>> list(@RequestParam(name = "time") String time){
        List<SeckillGoods> list = seckillGoodsService.list(time);
        return new Result<>(true,StatusCode.OK,"success",list);
    }

    /**
     * 获取秒杀商品详情信息
     * @param time 时间项
     * @param id 秒杀商品id
     * @return
     */
    @GetMapping("/detail")
    public Result<SeckillGoods> queryByNamespaceAndKey(String time, Long id){
        SeckillGoods seckillGoods = seckillGoodsService.select(time,id);
        return new Result(true, StatusCode.OK,"success",seckillGoods);
    }

}
