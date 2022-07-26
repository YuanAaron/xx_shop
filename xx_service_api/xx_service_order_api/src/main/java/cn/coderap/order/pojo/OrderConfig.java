package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * orderConfig实体类
 */
@Data
@Table(name="tb_order_config")
public class OrderConfig implements Serializable {

	@Id
	private Integer id;//ID
	private Integer orderTimeout;//正常订单超时时间（分）
	private Integer seckillTimeout;//秒杀订单超时时间（分）
	private Integer takeTimeout;//自动收货（天）
	private Integer serviceTimeout;//售后期限
	private Integer commentTimeout;//自动五星好评
}
