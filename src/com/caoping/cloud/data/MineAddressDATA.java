package com.caoping.cloud.data;


import com.caoping.cloud.entiy.ShoppingAddress;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class MineAddressDATA extends Data {
    private List<ShoppingAddress> data;

    public List<ShoppingAddress> getData() {
        return data;
    }

    public void setData(List<ShoppingAddress> data) {
        this.data = data;
    }
}
