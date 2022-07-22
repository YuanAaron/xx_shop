package cn.coderap.cart.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 购物车详情表和订单详情表一样
 */
@Data
public class OrderItem implements Serializable {

    private String id;//ID
    private Integer categoryId1;//1级分类
    private Integer categoryId2;//2级分类
    private Integer categoryId3;//3级分类
    private String spuId;//SPU_ID
    private String skuId;//SKU_ID
//    private String orderId;//订单ID
    private String name;//商品名称
    private Integer price;//单价
    private Integer num;//数量
    private Integer money;//总金额
    private Integer payMoney;//实付金额
    private String image;//图片地址
    private Integer weight;//重量
//    private Integer postFee;//运费
//    private String isReturn;//是否退货

    //用来标识是否被选中
    private boolean checked;
}
