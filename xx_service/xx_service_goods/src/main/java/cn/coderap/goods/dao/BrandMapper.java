package cn.coderap.goods.dao;

import cn.coderap.goods.pojo.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper extends Mapper<Brand> {
    @Select("SELECT * FROM tb_brand WHERE id IN (" +
            "SELECT brand_id FROM tb_category_brand WHERE category_id IN (" +
            "SELECT id FROM tb_category WHERE `name` = #{name})) order by seq")
    List<Brand> selectByCategoryName(@Param("name") String categoryName);
}
