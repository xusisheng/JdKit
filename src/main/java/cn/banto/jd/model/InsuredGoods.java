package cn.banto.jd.model;

import java.util.Date;

/**
 * 保价商品信息
 * @author BANTO
 */
public class InsuredGoods extends Goods {

    private Date buyTime;
    private long orderId;
    private int count;
    private float price;

    private long skuid;
    private int sequence;


    public Date getBuyTime() {
        return buyTime;
    }

    public void setBuyTime(Date buyTime) {
        this.buyTime = buyTime;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public long getSkuid() {
        return skuid;
    }

    public void setSkuid(long skuid) {
        this.skuid = skuid;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return "InsuredGoods{" +
                "buyTime=" + buyTime +
                ", orderId=" + orderId +
                ", count=" + count +
                ", price=" + price +
                ", skuid=" + skuid +
                ", sequence=" + sequence +
                '}';
    }
}
