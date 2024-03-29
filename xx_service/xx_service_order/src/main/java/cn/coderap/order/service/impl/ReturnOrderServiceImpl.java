package cn.coderap.order.service.impl;

import cn.coderap.order.dao.ReturnOrderMapper;
import cn.coderap.order.pojo.ReturnOrder;
import cn.coderap.order.service.ReturnOrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class ReturnOrderServiceImpl implements ReturnOrderService {
    @Autowired
    private ReturnOrderMapper returnOrderMapper;

    @Override
    public List<ReturnOrder> findAll() {
        return returnOrderMapper.selectAll();
    }

    @Override
    public ReturnOrder findById(String id){
        return returnOrderMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(ReturnOrder returnOrder){
        returnOrderMapper.insertSelective(returnOrder);
    }

    @Override
    public void update(ReturnOrder returnOrder){
        returnOrderMapper.updateByPrimaryKeySelective(returnOrder);
    }

    @Override
    public void delete(String id){
        returnOrderMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<ReturnOrder> findList(Map<String, Object> searchMap){
        Example example = createExample(searchMap);
        return returnOrderMapper.selectByExample(example);
    }

    @Override
    public Page<ReturnOrder> findPage(Map<String,Object> searchMap, int page, int size){
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<ReturnOrder>)returnOrderMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(ReturnOrder.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 服务单号
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andLike("id","%"+searchMap.get("id")+"%");
           	}
            // 订单号
            if(searchMap.get("order_id")!=null && !"".equals(searchMap.get("order_id"))){
                criteria.andLike("order_id","%"+searchMap.get("order_id")+"%");
           	}
            // 用户账号
            if(searchMap.get("user_account")!=null && !"".equals(searchMap.get("user_account"))){
                criteria.andLike("user_account","%"+searchMap.get("user_account")+"%");
           	}
            // 联系人
            if(searchMap.get("linkman")!=null && !"".equals(searchMap.get("linkman"))){
                criteria.andLike("linkman","%"+searchMap.get("linkman")+"%");
           	}
            // 联系人手机
            if(searchMap.get("linkman_mobile")!=null && !"".equals(searchMap.get("linkman_mobile"))){
                criteria.andLike("linkman_mobile","%"+searchMap.get("linkman_mobile")+"%");
           	}
            // 类型
            if(searchMap.get("type")!=null && !"".equals(searchMap.get("type"))){
                criteria.andLike("type","%"+searchMap.get("type")+"%");
           	}
            // 是否退运费
            if(searchMap.get("is_return_freight")!=null && !"".equals(searchMap.get("is_return_freight"))){
                criteria.andLike("is_return_freight","%"+searchMap.get("is_return_freight")+"%");
           	}
            // 申请状态
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andLike("status","%"+searchMap.get("status")+"%");
           	}
            // 凭证图片
            if(searchMap.get("evidence")!=null && !"".equals(searchMap.get("evidence"))){
                criteria.andLike("evidence","%"+searchMap.get("evidence")+"%");
           	}
            // 问题描述
            if(searchMap.get("description")!=null && !"".equals(searchMap.get("description"))){
                criteria.andLike("description","%"+searchMap.get("description")+"%");
           	}
            // 处理备注
            if(searchMap.get("remark")!=null && !"".equals(searchMap.get("remark"))){
                criteria.andLike("remark","%"+searchMap.get("remark")+"%");
           	}

            // 退款金额
            if(searchMap.get("returnMoney")!=null ){
                criteria.andEqualTo("returnMoney",searchMap.get("returnMoney"));
            }
            // 退货退款原因
            if(searchMap.get("returnCause")!=null ){
                criteria.andEqualTo("returnCause",searchMap.get("returnCause"));
            }
            // 管理员id
            if(searchMap.get("adminId")!=null ){
                criteria.andEqualTo("adminId",searchMap.get("adminId"));
            }
        }
        return example;
    }

}
