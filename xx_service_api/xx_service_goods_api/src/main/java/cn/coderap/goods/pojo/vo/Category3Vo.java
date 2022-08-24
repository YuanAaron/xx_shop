package cn.coderap.goods.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Category3Vo {
    private Integer subId;
    private String subName;
    private Integer subParentId;
}
