package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * task实体类
 */
@Table(name="tb_task")
@Data
public class Task implements Serializable {

	@Id
	private Long id;
	private Date createTime;
	private Date updateTime;
	private Date deleteTime;
	private String taskType;
	private String mqExchange;
	private String mqRoutingkey;
	private String requestBody;
	private String status;
	private String errormsg;
}
