package cn.coderap.order.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * log实体类
 */
@Table(name="undo_log")
@Data
public class Log implements Serializable {

	@Id
	private Long id;//id
	private Long branchId;//branch_id
	private String xid;//xid
	private byte[] rollbackInfo;//rollback_info
	private Integer logStatus;//log_status
	private java.util.Date logCreated;//log_created
	private java.util.Date logModified;//log_modified
	private String ext;//ext
}
