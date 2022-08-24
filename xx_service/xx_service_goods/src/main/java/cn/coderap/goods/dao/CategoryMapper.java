package cn.coderap.goods.dao;

import cn.coderap.goods.pojo.Category;
import cn.coderap.goods.pojo.vo.Category2Vo;
import cn.coderap.goods.pojo.vo.Category3Vo;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface CategoryMapper extends Mapper<Category> {

    @Results(id = "category2VoMap", value = {
            @Result(column = "id",property = "id"),
            @Result(column = "name",property = "name"),
            @Result(column = "parent_id",property = "parentId"),
            @Result(column = "id",property = "category3VoList",
                    many = @Many(select = "cn.coderap.goods.dao.CategoryMapper.getSubCategory3List"))})
    @Select("select * from tb_category where parent_id= #{id}")
    List<Category2Vo> getSubCategory2List(Integer id);

    @Select("select id as subId,name as subName,parent_id as subParentId from tb_category where parent_id= #{id}")
    List<Category3Vo> getSubCategory3List(Integer id);
}
