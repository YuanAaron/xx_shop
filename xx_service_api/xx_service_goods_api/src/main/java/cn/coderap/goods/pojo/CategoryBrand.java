package cn.coderap.goods.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table(name="tb_category_brand")
public class CategoryBrand implements Serializable {

    @Id
    private Integer categoryId;
    @Id
    private Integer brandId;

}
