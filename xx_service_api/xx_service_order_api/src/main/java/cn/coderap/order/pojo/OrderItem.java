package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * orderItem实体类
 */
@Data
@Table(name="tb_order_item")
public class OrderItem implements Serializable {

	@Id
	private String id;//ID
	private Integer categoryId1;//1级分类
	private Integer categoryId2;//2级分类
	private Integer categoryId3;//3级分类
	private String spuId;//SPU_ID
	private String skuId;//SKU_ID
	private String orderId;//订单ID
	private String name;//商品名称
	private Integer price;//单价
	private Integer num;//数量
	private Integer money;//总金额
	private Integer payMoney;//实付金额
	private String image;//图片地址
	private Integer weight;//重量
	private Integer postFee;//运费
	private String isReturn;//是否退货

	//用来标识是否被选中
	private boolean checked;
}
