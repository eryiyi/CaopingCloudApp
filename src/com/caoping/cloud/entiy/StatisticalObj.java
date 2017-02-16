package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/11/28.
 */
public class StatisticalObj {
    //发了多少草坪数据 机械数据 草种数据
    //发了多少物流数据
    //发了多少新闻
    //有多少订单
    //有多少人关注你
    //你的公司在名企排行中排第几

    private String cpNumber;
    private String jxNumber;
    private String czNumber;
    private String wlNumber;
    private String newsNumber;
    private String orderNumber;
    private String favourNumber;
    private String mqNumber;

    public String getCpNumber() {
        return cpNumber;
    }

    public void setCpNumber(String cpNumber) {
        this.cpNumber = cpNumber;
    }

    public String getJxNumber() {
        return jxNumber;
    }

    public void setJxNumber(String jxNumber) {
        this.jxNumber = jxNumber;
    }

    public String getCzNumber() {
        return czNumber;
    }

    public void setCzNumber(String czNumber) {
        this.czNumber = czNumber;
    }

    public String getWlNumber() {
        return wlNumber;
    }

    public void setWlNumber(String wlNumber) {
        this.wlNumber = wlNumber;
    }

    public String getNewsNumber() {
        return newsNumber;
    }

    public void setNewsNumber(String newsNumber) {
        this.newsNumber = newsNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getFavourNumber() {
        return favourNumber;
    }

    public void setFavourNumber(String favourNumber) {
        this.favourNumber = favourNumber;
    }

    public String getMqNumber() {
        return mqNumber;
    }

    public void setMqNumber(String mqNumber) {
        this.mqNumber = mqNumber;
    }
}
