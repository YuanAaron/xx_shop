package cn.coderap.goods.dao;

import cn.coderap.goods.pojo.Spec;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SpecMapper extends Mapper<Spec> {
    @Select("SELECT * FROM tb_spec WHERE template_id IN (" +
            "SELECT template_id FROM tb_category WHERE `name` = #{name}) ORDER BY seq")
    List<Spec> selectByCategoryName(@Param("name") String categoryName);
}
