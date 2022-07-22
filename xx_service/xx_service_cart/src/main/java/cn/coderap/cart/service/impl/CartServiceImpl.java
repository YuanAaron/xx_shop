package cn.coderap.cart.service.impl;

import cn.coderap.cart.feign.SkuFeign;
import cn.coderap.cart.feign.SpuFeign;
import cn.coderap.cart.pojo.OrderItem;
import cn.coderap.cart.service.CartService;
import cn.coderap.entity.Result;
import cn.coderap.goods.pojo.Sku;
import cn.coderap.goods.pojo.Spu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SkuFeign skuFeign;
    @Autowired
    private SpuFeign spuFeign;
    private static final String CART = "CART_";

    @Override
    public void add(String id, Integer num, String userName) {
        //RedisTemplates默认的序列化工具会对数据进行转义，导致根据key或者field删除失败
        //需要使用StringRedisSerializer工具进行序列化
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //特殊情况一：如果传递过来的num是<=0情况
        if (num <= 0) {
            //移除购物车中的该商品明细
            redisTemplate.boundHashOps(CART + userName).delete(id);
            return;
        }

        OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART + userName).get(id);
        //特殊情况二：判断购物车中是否已经存在了该商品
        if (orderItem != null) {
            //如果购物车中已经存在商品，修改num、money和payMoney即可
            orderItem.setNum(orderItem.getNum() + num);
            orderItem.setMoney(orderItem.getNum() * orderItem.getPrice());
            orderItem.setPayMoney(orderItem.getNum() * orderItem.getPrice());
        } else {
            //1.访问商品微服务获得SKU、SPU
            Result<Sku> skuResult = skuFeign.findById(id);
            Sku sku = skuResult.getData();
            Result<Spu> spuResult = spuFeign.findById(sku.getSpuId());
            Spu spu = spuResult.getData();

            //2.转换为OrderItem
            orderItem = parseToOrderItem(num, sku, spu);
        }

        //3.保存
        redisTemplate.boundHashOps(CART + userName).put(id, orderItem);
    }

    @Override
    public Map list(String userName) {
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //定义返回结果
        Map<String,Object> resultMap = new HashMap<>();
        List<OrderItem> values = redisTemplate.boundHashOps(CART + userName).values();
        resultMap.put("orderItemList",values);
        //商品总数及商品总的价格
        Integer totalNum = 0;
        Integer totalPrice = 0;
        for(OrderItem orderItem : values){
            if (orderItem.isChecked()) {
                totalNum += orderItem.getNum();
                totalPrice += orderItem.getMoney();
            }
        }
        resultMap.put("totalNum",totalNum);
        resultMap.put("totalPrice",totalPrice);
        return resultMap;
    }

    @Override
    public void delete(String skuId, String userName) {
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        redisTemplate.opsForHash().delete(CART+userName, skuId);
    }

    @Override
    public void updateCheckedStatus(String skuId, Boolean checked, String userName) {
        RedisSerializer stringRedisSerializer = new StringRedisSerializer();
        redisTemplate.setKeySerializer(stringRedisSerializer);
        redisTemplate.setHashKeySerializer(stringRedisSerializer);

        //读取出购物车中所有的key(一个又一个的skuId)
        Set keys = redisTemplate.boundHashOps(CART + userName).keys();
        for(Object key : keys){
            if(key.equals(skuId)){
                OrderItem orderItem = (OrderItem) redisTemplate.boundHashOps(CART + userName).get(skuId);
                orderItem.setChecked(checked);
                redisTemplate.boundHashOps(CART+userName).put(skuId,orderItem);
            }
        }
    }

    private OrderItem parseToOrderItem(Integer num, Sku sku, Spu spu) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCategoryId1(spu.getCategory1Id());
        orderItem.setCategoryId2(spu.getCategory2Id());
        orderItem.setCategoryId3(spu.getCategory3Id());
        orderItem.setSpuId(spu.getId());
        orderItem.setSkuId(sku.getId());
        orderItem.setName(sku.getName());
        orderItem.setPrice(sku.getPrice());
        orderItem.setNum(num);
        orderItem.setMoney(num * orderItem.getPrice());//Num * price
        orderItem.setPayMoney(num * orderItem.getPrice()); //如果没有优惠，money和payMoney相同
        orderItem.setImage(sku.getImage());
        return orderItem;
    }
}
