package com.caoping.cloud.data;


import com.caoping.cloud.entiy.ShoppingAddress;

/**
 * 收货地址默认的
 */
public class ShoppingAddressSingleDATA extends Data {
    private ShoppingAddress data;

    public ShoppingAddress getData() {
        return data;
    }

    public void setData(ShoppingAddress data) {
        this.data = data;
    }
}
