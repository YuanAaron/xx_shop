package cn.coderap.business.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * activity实体类
 */
@Data
@Table(name="tb_activity")
public class Activity implements Serializable {

    @Id
    private Integer id;//ID
    private String title;//活动标题
    private Date startTime;//开始时间
    private Date endTime;//结束时间
    private String status;//状态
    private String content;//活动内容

}
