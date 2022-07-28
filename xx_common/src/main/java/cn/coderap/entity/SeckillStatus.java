package cn.coderap.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 存储用户信息和订单信息
 */
@NoArgsConstructor
@Data
public class SeckillStatus implements Serializable {
    private String username; //用户ID
    private Date createTime; //创建时间
    private Integer status; //秒杀状态 1.正在排队  2.等待支付  3.支付超时   4. 秒杀失败   5.支付完成
    private Long goodsId; //秒杀商品的id
    private Float money; //应付金额
    private Long orderId; //订单号
    private String time; //时间菜单项

    public SeckillStatus(String username, Date createTime, Integer status, Long goodsId, String time) {
        this.username = username;
        this.createTime = createTime;
        this.status = status;
        this.goodsId = goodsId;
        this.time = time;
    }
}
