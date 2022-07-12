package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.SkuMapper;
import cn.coderap.goods.pojo.Sku;
import cn.coderap.goods.service.SkuService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class SkuServiceImpl implements SkuService {
    @Autowired
    private SkuMapper skuMapper;

    @Override
    public List<Sku> findAll() {
        return skuMapper.selectAll();
    }

    @Override
    public Sku findById(String id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Sku sku) {
        skuMapper.insertSelective(sku);
    }

    @Override
    public void update(Sku sku) {
        skuMapper.updateByPrimaryKeySelective(sku);
    }

    @Override
    public void delete(String id) {
        skuMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Sku> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return skuMapper.selectByExample(example);
    }

    @Override
    public Page<Sku> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Sku>)skuMapper.selectByExample(example);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 商品id
            if(searchMap.get("id")!=null && !"".equals(searchMap.get("id"))){
                criteria.andEqualTo("id", searchMap.get("id"));
            }
            // 商品条码
            if(searchMap.get("sn")!=null && !"".equals(searchMap.get("sn"))){
                criteria.andEqualTo("sn",searchMap.get("sn"));
            }
            // SKU名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 商品图片
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 商品图片列表
            if(searchMap.get("images")!=null && !"".equals(searchMap.get("images"))){
                criteria.andLike("images","%"+searchMap.get("images")+"%");
            }
            // SPUID
            if(searchMap.get("spuId")!=null && !"".equals(searchMap.get("spuId"))){
                criteria.andEqualTo("spuId",searchMap.get("spuId"));
            }
            // 类目名称
            if(searchMap.get("categoryName")!=null && !"".equals(searchMap.get("categoryName"))){
                criteria.andLike("categoryName","%"+searchMap.get("categoryName")+"%");
            }
            // 品牌名称
            if(searchMap.get("brandName")!=null && !"".equals(searchMap.get("brandName"))){
                criteria.andLike("brandName","%"+searchMap.get("brandName")+"%");
            }
            // 规格
            if(searchMap.get("spec")!=null && !"".equals(searchMap.get("spec"))){
                criteria.andLike("spec","%"+searchMap.get("spec")+"%");
            }
            // 商品状态 1-正常，2-下架，3-删除
            if(searchMap.get("status")!=null && !"".equals(searchMap.get("status"))){
                criteria.andEqualTo("status", searchMap.get("status"));
            }

            // 价格（分）
            if(searchMap.get("price")!=null ){
                criteria.andEqualTo("price",searchMap.get("price"));
            }
            // 库存数量
            if(searchMap.get("num")!=null ){
                criteria.andEqualTo("num",searchMap.get("num"));
            }
            // 库存预警数量
            if(searchMap.get("alertNum")!=null ){
                criteria.andEqualTo("alertNum",searchMap.get("alertNum"));
            }
            // 重量（克）
            if(searchMap.get("weight")!=null ){
                criteria.andEqualTo("weight",searchMap.get("weight"));
            }
            // 类目ID
            if(searchMap.get("categoryId")!=null ){
                criteria.andEqualTo("categoryId",searchMap.get("categoryId"));
            }
            // 销量
            if(searchMap.get("saleNum")!=null ){
                criteria.andEqualTo("saleNum",searchMap.get("saleNum"));
            }
            // 评论数
            if(searchMap.get("commentNum")!=null ){
                criteria.andEqualTo("commentNum",searchMap.get("commentNum"));
            }
        }
        return example;
    }
}
