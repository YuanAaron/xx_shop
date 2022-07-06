package cn.coderap.system.pojo;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * role实体类
 */
@Data
@Table(name="tb_role")
public class Role implements Serializable {

    @Id
    private Integer id;//ID
    private String name;//角色名称

}
