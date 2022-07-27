package cn.coderap.seckill.pojo;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Table(name = "tb_seckill_order")
public class SeckillOrder implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "seckill_id")
    private Long seckillId; //秒杀商品ID
    @Column(name = "money")
    private BigDecimal money; //支付金额
    @Column(name = "user_id")
    private String userId; //用户
    @Column(name = "seller_id")
    private String sellerId; //商家
    @Column(name = "create_time")
    private Date createTime; //创建时间
    @Column(name = "pay_time")
    private Date payTime; //支付时间
    @Column(name = "status")
    private String status; //状态
    @Column(name = "receiver_address")
    private String receiverAddress; //收货人地址
    @Column(name = "receiver_mobile")
    private String receiverMobile; //收货人电话
    @Column(name = "receiver")
    private String receiver; //收货人
    @Column(name = "transaction_id")
    private String transactionId; //交易流水

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(Long seckillId) {
        this.seckillId = seckillId;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId == null ? null : userId.trim();
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId == null ? null : sellerId.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public void setReceiverAddress(String receiverAddress) {
        this.receiverAddress = receiverAddress == null ? null : receiverAddress.trim();
    }

    public String getReceiverMobile() {
        return receiverMobile;
    }

    public void setReceiverMobile(String receiverMobile) {
        this.receiverMobile = receiverMobile == null ? null : receiverMobile.trim();
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver == null ? null : receiver.trim();
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId == null ? null : transactionId.trim();
    }
}