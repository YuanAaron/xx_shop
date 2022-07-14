package cn.coderap.goods.service.impl;

import cn.coderap.goods.dao.*;
import cn.coderap.goods.pojo.*;
import cn.coderap.goods.service.GoodsService;
import cn.coderap.util.IdWorker;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private CategoryBrandMapper categoryBrandMapper;

    /**
     * 一个商品包含一个spu和多个sku
     * @param goods
     */
    @Transactional
    @Override
    public void add(Goods goods) {
        // 保存spu
        Spu spu = goods.getSpu();
        spu.setId(String.valueOf(idWorker.nextId()));
        spuMapper.insertSelective(spu);
        // 保存sku集合
        insertSkuList(goods);
    }

    @Override
    public Goods findById(String id) {
        // 查询spu对象
        Spu spu = spuMapper.selectByPrimaryKey(id);
        // 查询sku列表
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", id);
        List<Sku> skuList = skuMapper.selectByExample(example);

        Goods goods = new Goods();
        goods.setSpu(spu);
        goods.setSkuList(skuList);
        return goods;
    }

    @Transactional
    @Override
    public void update(Goods goods) {
        Spu spu = goods.getSpu();
        spuMapper.updateByPrimaryKeySelective(spu);

        //删除原来的sku列表
        Example example = new Example(Sku.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("spuId", spu.getId());
        skuMapper.deleteByExample(example);
        //保存sku列表
        insertSkuList(goods);
    }

    private void insertSkuList(Goods goods) {
        Spu spu = goods.getSpu();
        Date date = new Date();
        //获得品牌对象
        Brand brand = brandMapper.selectByPrimaryKey(spu.getBrandId());
        //获得分类对象
        Category category = categoryMapper.selectByPrimaryKey(spu.getCategory3Id());

        /**
         * 添加分类和品牌之间的关联
         * tb_category_brand
         */
        CategoryBrand categoryBrand = new CategoryBrand();
        categoryBrand.setBrandId(spu.getBrandId());
        categoryBrand.setCategoryId(spu.getCategory3Id());
        int count = categoryBrandMapper.selectCount(categoryBrand);
        if (count == 0) {
            categoryBrandMapper.insert(categoryBrand);
        }

        //获得SkuList
        List<Sku> skuList = goods.getSkuList();
        if (skuList != null && skuList.size() > 0) {
            for (Sku sku : skuList) {
                sku.setId(String.valueOf(idWorker.nextId()));
                sku.setName(spu.getName()); // ???
                sku.setCreateTime(date);
                sku.setUpdateTime(date);
                sku.setSpuId(spu.getId());
                sku.setCategoryId(category.getId());
                sku.setCategoryName(category.getName());
                sku.setBrandName(brand.getName());
                if (StringUtils.isEmpty(sku.getSpec())) {
                    sku.setSpec("{}");
                }
                skuMapper.insertSelective(sku);
            }
        }
    }
}
