package cn.coderap.order.service.impl;

import cn.coderap.order.dao.OrderItemMapper;
import cn.coderap.order.pojo.OrderItem;
import cn.coderap.order.service.OrderItemService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class OrderItemServiceImpl implements OrderItemService {
    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public List<OrderItem> findAll() {
        return orderItemMapper.selectAll();
    }

    @Override
    public OrderItem findById(String id){
        return orderItemMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(OrderItem orderItem){
        orderItemMapper.insertSelective(orderItem);
    }

    @Override
    public void update(OrderItem orderItem){
        orderItemMapper.updateByPrimaryKeySelective(orderItem);
    }

    @Override
    public void delete(String id){
        orderItemMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<OrderItem> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return orderItemMapper.selectByExample(example);
    }

    @Override
    public Page<OrderItem> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<OrderItem>)orderItemMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(OrderItem.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id",searchMap.get("id"));
           	}
            // SPU_ID
            if(searchMap.get("spuId")!=null && !"".equals(searchMap.get("spuId"))){
                criteria.andEqualTo("spuId",searchMap.get("spuId"));
           	}
            // SKU_ID
            if(searchMap.get("skuId")!=null && !"".equals(searchMap.get("skuId"))){
                criteria.andEqualTo("skuId",searchMap.get("skuId"));
           	}
            // 订单ID
            if(searchMap.get("orderId")!=null && !"".equals(searchMap.get("orderId"))){
                criteria.andEqualTo("orderId",searchMap.get("orderId"));
           	}
            // 商品名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
           	}
            // 图片地址
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
           	}
            // 是否退货
            if(searchMap.get("isReturn")!=null && !"".equals(searchMap.get("isReturn"))){
                criteria.andEqualTo("isReturn",searchMap.get("isReturn"));
           	}

            // 1级分类
            if(searchMap.get("categoryId1")!=null ){
                criteria.andEqualTo("categoryId1",searchMap.get("categoryId1"));
            }
            // 2级分类
            if(searchMap.get("categoryId2")!=null ){
                criteria.andEqualTo("categoryId2",searchMap.get("categoryId2"));
            }
            // 3级分类
            if(searchMap.get("categoryId3")!=null ){
                criteria.andEqualTo("categoryId3",searchMap.get("categoryId3"));
            }
            // 单价
            if(searchMap.get("price")!=null ){
                criteria.andEqualTo("price",searchMap.get("price"));
            }
            // 数量
            if(searchMap.get("num")!=null ){
                criteria.andEqualTo("num",searchMap.get("num"));
            }
            // 总金额
            if(searchMap.get("money")!=null ){
                criteria.andEqualTo("money",searchMap.get("money"));
            }
            // 实付金额
            if(searchMap.get("payMoney")!=null ){
                criteria.andEqualTo("payMoney",searchMap.get("payMoney"));
            }
            // 重量
            if(searchMap.get("weight")!=null ){
                criteria.andEqualTo("weight",searchMap.get("weight"));
            }
            // 运费
            if(searchMap.get("postFee")!=null ){
                criteria.andEqualTo("postFee",searchMap.get("postFee"));
            }
        }
        return example;
    }

}
