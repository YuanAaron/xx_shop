package cn.coderap.user.pojo;

import lombok.Data;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="tb_point_log")
@Data
public class PointLog {

    @Id
    private String orderId;
    private String username;
    private Integer point;
}
