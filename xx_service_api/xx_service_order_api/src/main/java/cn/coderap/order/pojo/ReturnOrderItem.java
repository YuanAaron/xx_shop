package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * returnOrderItem实体类
 */
@Data
@Table(name="tb_return_order_item")
public class ReturnOrderItem implements Serializable {
	@Id
	private String id;//ID
	private Integer categoryId;//分类ID
	private String spuId;//SPU_ID
	private String skuId;//SKU_ID
	private String orderId;//订单ID
	private String orderItemId;//订单明细ID
	private String returnOrderId;//退货订单ID
	private String title;//标题
	private Integer price;//单价
	private Integer num;//数量
	private Integer money;//总金额
	private Integer payMoney;//支付金额
	private String image;//图片地址
	private Integer weight;//重量
}
