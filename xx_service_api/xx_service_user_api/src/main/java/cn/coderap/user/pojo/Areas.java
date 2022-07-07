package cn.coderap.user.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * areas实体类
 */
@Data
@Table(name="tb_areas")
public class Areas implements Serializable {

	@Id
	private String areaid;//区域ID
	private String area;//区域名称
	private String cityid;//城市ID

}
