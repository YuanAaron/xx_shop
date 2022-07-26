package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * order实体类
 */
@Table(name="tb_order")
@Data
public class Order implements Serializable {

	@Id
	private String id;//订单id
	private Integer totalNum;//数量合计
	private Integer totalMoney;//金额合计
	private Integer preMoney;//优惠金额
	private Integer postFee;//邮费
	private Integer payMoney;//实付金额
	private String payType;//支付类型，1、在线支付、0 货到付款
	private java.util.Date createTime;//订单创建时间
	private java.util.Date updateTime;//订单更新时间
	private java.util.Date payTime;//付款时间
	private java.util.Date consignTime;//发货时间
	private java.util.Date endTime;//交易完成时间
	private java.util.Date closeTime;//交易关闭时间
	private String shippingName;//物流名称
	private String shippingCode;//物流单号
	private String username;//用户名称
	private String buyerMessage;//买家留言
	private String buyerRate;//是否评价
	private String receiverContact;//收货人
	private String receiverMobile;//收货人手机
	private String receiverAddress;//收货人地址
	private String sourceType;//订单来源：1:web，2：app，3：微信公众号，4：微信小程序  5 H5手机页面
	private String transactionId;//交易流水号
	private String orderStatus;//订单状态
	private String payStatus;//支付状态
	private String consignStatus;//发货状态
	private String isDelete;//是否删除
}
