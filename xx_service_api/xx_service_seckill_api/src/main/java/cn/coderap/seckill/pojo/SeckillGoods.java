package cn.coderap.seckill.pojo;

import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ToString
@Table(name = "tb_seckill_goods")
public class SeckillGoods implements Serializable {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "goods_id")
    private Long goodsId;
    @Column(name = "item_id")
    private Long itemId;
    @Column(name = "title")
    private String title; //标题
    @Column(name = "small_pic")
    private String smallPic; //商品图片
    @Column(name = "price")
    private BigDecimal price; //原价格
    @Column(name = "cost_price")
    private BigDecimal costPrice; //秒杀价格
    @Column(name = "seller_id")
    private String sellerId; //商家id
    @Column(name = "create_time")
    private Date createTime; //添加日期
    @Column(name = "check_time")
    private Date checkTime; // 审核日期
    @Column(name = "status")
    private String status; //审核状态
    @Column(name = "start_time")
    private Date startTime; //开始时间
    @Column(name = "end_time")
    private Date endTime; //结束时间
    @Column(name = "num")
    private Integer num; //秒杀商品数
    @Column(name = "stock_count")
    private Integer stockCount; //剩余库存数
    @Column(name = "introduction")
    private String introduction; // 描述

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getSmallPic() {
        return smallPic;
    }

    public void setSmallPic(String smallPic) {
        this.smallPic = smallPic == null ? null : smallPic.trim();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
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

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction == null ? null : introduction.trim();
    }

}
