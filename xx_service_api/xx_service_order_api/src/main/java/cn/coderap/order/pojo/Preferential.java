package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * preferential实体类
 */
@Data
@Table(name="tb_preferential")
public class Preferential implements Serializable {

	@Id
	private Integer id;//ID
	private Integer buyMoney;//消费金额
	private Integer preMoney;//优惠金额
	private Integer categoryId;//品类ID
	private java.util.Date startTime;//活动开始日期
	private java.util.Date endTime;//活动截至日期
	private String state;//状态
	private String type;//类型1不翻倍 2翻倍
}
