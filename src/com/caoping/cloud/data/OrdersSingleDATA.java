package com.caoping.cloud.data;


import com.caoping.cloud.entiy.OrderVo;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class OrdersSingleDATA extends Data {
    private OrderVo data;

    public OrderVo getData() {
        return data;
    }

    public void setData(OrderVo data) {
        this.data = data;
    }
}
