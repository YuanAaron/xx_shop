package cn.coderap.order.service.impl;

import cn.coderap.order.dao.OrderConfigMapper;
import cn.coderap.order.pojo.OrderConfig;
import cn.coderap.order.service.OrderConfigService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class OrderConfigServiceImpl implements OrderConfigService {

    @Autowired
    private OrderConfigMapper orderConfigMapper;

    @Override
    public List<OrderConfig> findAll() {
        return orderConfigMapper.selectAll();
    }

    @Override
    public OrderConfig findById(Integer id){
        return  orderConfigMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(OrderConfig orderConfig){
        orderConfigMapper.insertSelective(orderConfig);
    }

    @Override
    public void update(OrderConfig orderConfig){
        orderConfigMapper.updateByPrimaryKeySelective(orderConfig);
    }

    @Override
    public void delete(Integer id){
        orderConfigMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<OrderConfig> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return orderConfigMapper.selectByExample(example);
    }

    @Override
    public Page<OrderConfig> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<OrderConfig>)orderConfigMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(OrderConfig.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // ID
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 正常订单超时时间（分）
            if(searchMap.get("orderTimeout")!=null ){
                criteria.andEqualTo("orderTimeout",searchMap.get("orderTimeout"));
            }
            // 秒杀订单超时时间（分）
            if(searchMap.get("seckillTimeout")!=null ){
                criteria.andEqualTo("seckillTimeout",searchMap.get("seckillTimeout"));
            }
            // 自动收货（天）
            if(searchMap.get("takeTimeout")!=null ){
                criteria.andEqualTo("takeTimeout",searchMap.get("takeTimeout"));
            }
            // 售后期限
            if(searchMap.get("serviceTimeout")!=null ){
                criteria.andEqualTo("serviceTimeout",searchMap.get("serviceTimeout"));
            }
            // 自动五星好评
            if(searchMap.get("commentTimeout")!=null ){
                criteria.andEqualTo("commentTimeout",searchMap.get("commentTimeout"));
            }
        }
        return example;
    }

}
