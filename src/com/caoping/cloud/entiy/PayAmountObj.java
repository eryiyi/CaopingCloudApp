package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2017/2/15.
 */
public class PayAmountObj {
    private String ruzhuNumber;
    private String zhichuAmount;
    private String shouruAmount;
    private String cpNumber;//发布数量
    private String goodsCountOne;//买的商品数量
    private String goodsCountTwo;//卖的商品数量
    private String zhimingdu;//知名度

    public String getZhimingdu() {
        return zhimingdu;
    }

    public void setZhimingdu(String zhimingdu) {
        this.zhimingdu = zhimingdu;
    }

    public String getCpNumber() {
        return cpNumber;
    }

    public void setCpNumber(String cpNumber) {
        this.cpNumber = cpNumber;
    }

    public String getGoodsCountOne() {
        return goodsCountOne;
    }

    public void setGoodsCountOne(String goodsCountOne) {
        this.goodsCountOne = goodsCountOne;
    }

    public String getGoodsCountTwo() {
        return goodsCountTwo;
    }

    public void setGoodsCountTwo(String goodsCountTwo) {
        this.goodsCountTwo = goodsCountTwo;
    }

    public String getRuzhuNumber() {
        return ruzhuNumber;
    }

    public void setRuzhuNumber(String ruzhuNumber) {
        this.ruzhuNumber = ruzhuNumber;
    }

    public String getZhichuAmount() {
        return zhichuAmount;
    }

    public void setZhichuAmount(String zhichuAmount) {
        this.zhichuAmount = zhichuAmount;
    }

    public String getShouruAmount() {
        return shouruAmount;
    }

    public void setShouruAmount(String shouruAmount) {
        this.shouruAmount = shouruAmount;
    }
}
