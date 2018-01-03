package cn.banto.jd.model;

public class CartGoods extends  Goods{
    private int count;
    private boolean isSeleced;

    private String pid;
    private String ptype;
    private String packId;
    private String targetId;
    private String promoID;
    private String venderId;
    private String t;
    private String manFanZeng;
    private String outSkus;
    private String locationId;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isSeleced() {
        return isSeleced;
    }

    public void setSeleced(boolean seleced) {
        isSeleced = seleced;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype;
    }

    public String getPackId() {
        return packId;
    }

    public void setPackId(String packId) {
        this.packId = packId;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getPromoID() {
        return promoID;
    }

    public void setPromoID(String promoID) {
        this.promoID = promoID;
    }

    public String getVenderId() {
        return venderId;
    }

    public void setVenderId(String venderId) {
        this.venderId = venderId;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getManFanZeng() {
        return manFanZeng;
    }

    public void setManFanZeng(String manFanZeng) {
        this.manFanZeng = manFanZeng;
    }

    public String getOutSkus() {
        return outSkus;
    }

    public void setOutSkus(String outSkus) {
        this.outSkus = outSkus;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
