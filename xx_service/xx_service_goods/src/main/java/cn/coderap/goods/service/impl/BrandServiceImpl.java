package cn.coderap.goods.service.impl;

import cn.coderap.goods.pojo.Brand;
import cn.coderap.goods.dao.BrandMapper;
import cn.coderap.goods.service.BrandService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Autowired
    private BrandMapper brandMapper;

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectAll();
    }

    @Override
    public Brand findById(Integer id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void add(Brand brand) {
        /**
         * insert和insertSelective:
         * insert：保存一个实体，null的属性也会保存，不会使用数据库默认值。比如name=null，对应的sql为：insert into brand values(id,name,images,letter,seq) ...
         * insertSelective：保存一个实体，null的属性不会保存，会使用数据库默认值。比如name=null，对应的sql为：insert into brand values(id,images,letter,seq) ...
         */
        brandMapper.insertSelective(brand);
    }

    @Override
    public void update(Brand brand) {
        brandMapper.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Integer id) {
        brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Brand> findList(Map searchMap) {
        Example example = createExample(searchMap);
        return brandMapper.selectByExample(example);
    }

    @Override
    public Page<Brand> findPage(Map searchMap, int page, int size) {
        PageHelper.startPage(page,size);
        Example example = createExample(searchMap);
        return (Page<Brand>) brandMapper.selectByExample(example);
    }

    @Override
    public List<Brand> findByCategoryName(String categoryName) {
        return  brandMapper.selectByCategoryName(categoryName);
    }

    private Example createExample(Map<String, Object> searchMap){
        Example example=new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        if(!CollectionUtils.isEmpty(searchMap)){
            // 品牌id
            if(searchMap.get("id")!=null ){
                criteria.andEqualTo("id",searchMap.get("id"));
            }
            // 品牌名称
            if(searchMap.get("name")!=null && !"".equals(searchMap.get("name"))){
                criteria.andLike("name","%"+searchMap.get("name")+"%");
            }
            // 品牌图片地址
            if(searchMap.get("image")!=null && !"".equals(searchMap.get("image"))){
                criteria.andLike("image","%"+searchMap.get("image")+"%");
            }
            // 品牌的首字母
            if (searchMap.get("letter") != null && !"".equals(searchMap.get("letter"))) {
                criteria.andEqualTo("letter", searchMap.get("letter"));
            }
            // 排序
            if(searchMap.get("seq")!=null ){
                criteria.andEqualTo("seq",searchMap.get("seq"));
            }
        }
        return example;
    }
}
