package cn.coderap.user.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * cities实体类
 */
@Data
@Table(name="tb_cities")
public class Cities implements Serializable {

	@Id
	private String cityid;//城市ID
	private String city;//城市名称
	private String provinceid;//省份ID

}
