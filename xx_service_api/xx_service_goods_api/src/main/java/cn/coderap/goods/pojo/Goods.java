package cn.coderap.goods.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class Goods implements Serializable {
    private Spu spu;
    private List<Sku> skuList;
}
