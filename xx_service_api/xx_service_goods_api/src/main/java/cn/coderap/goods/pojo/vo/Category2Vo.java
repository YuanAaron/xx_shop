package cn.coderap.goods.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class Category2Vo {
    private Integer id;
    private String name;
    private Integer parentId;
    //三级分类Vo对应的List
    private List<Category3Vo> category3VoList;
}
