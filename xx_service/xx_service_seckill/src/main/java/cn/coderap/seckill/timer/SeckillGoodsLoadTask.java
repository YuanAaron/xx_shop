package cn.coderap.seckill.timer;

import cn.coderap.seckill.dao.SeckillGoodsMapper;
import cn.coderap.seckill.pojo.SeckillGoods;
import cn.coderap.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class SeckillGoodsLoadTask {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    private static final String SECKILL_GOODS = "SeckillGoods_";

    /**
     * 定时将符合秒杀条件的商品存储到Redis库
     */
    @Scheduled(cron = "0/5 * * * * ?")
    public void loadGoodsToRedis() {
        //1.查询符合当前时间的时间菜单
        List<Date> dateMenus = DateUtil.getDateMenus();
        for (Date dateMenu : dateMenus) {
            //2.设置查询条件
            Example example = new Example(SeckillGoods.class);
            Example.Criteria criteria = example.createCriteria();
            //审核状态
            criteria.andEqualTo("status", "1");
            //库存大于0
            criteria.andGreaterThan("stockCount", "0");
            //菜单的开始时间 <= startTime && endTime < 菜单的开始时间+2小时
            criteria.andGreaterThanOrEqualTo("startTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(dateMenu));
            criteria.andLessThan("endTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(DateUtil.addDateHour(dateMenu, 2)));

            //设置namespace ：2021042920  -->  yyyyMMddHH
            String namespace = SECKILL_GOODS + DateUtil.data2str(dateMenu, "yyyyMMddHH");
            //排除掉已经存储到Redis的SeckillGoods,已经存储到Redis中的排除出查询结果
            Set keys = redisTemplate.boundHashOps(namespace).keys();
            if (keys != null && keys.size() > 0) {
                //排除
                criteria.andNotIn("id", keys);
            }

            //3.根据时间菜单查找匹配的数据
            List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectByExample(example);
            //4.将数据存储到redis中
            for (SeckillGoods seckillGood : seckillGoods) {
                redisTemplate.boundHashOps(namespace).put(seckillGood.getId(), seckillGood);
                //为每个商品维护一个队列List,该List的长度就是每个商品的库存数
                //List中存什么不重要,重要的是长度和库存数一致,将每个元素都存储该秒杀商品的id
//                redisTemplate.boundListOps("SeckillGoodsQueue_" + seckillGood.getId()).leftPushAll(getGoodsAllIds(seckillGood.getId(),seckillGood.getStockCount()));
            }
        }
    }

    /**
     * 为每个商品维护一个List集合,集合的每个元素都是该商品的ID,返回该商品的ID集合
     * @param id
     * @param num
     * @return
     */
    public Long[] getGoodsAllIds(Long id, Integer num) {
        Long[] ids = new Long[num];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = id;
        }
        return ids;
    }
}

