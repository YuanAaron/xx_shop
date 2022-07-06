package cn.coderap.system.pojo;

import lombok.Data;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * loginLog实体类
 */
@Data
@Table(name="tb_login_log")
public class LoginLog implements Serializable {

    @Id
    private Integer id;//id
    private String loginName;//login_name
    private String ip;//ip
    private String browserName;//browser_name
    private String location;//地区
    private java.util.Date loginTime;//登录时间

}
