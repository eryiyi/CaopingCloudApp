package com.caoping.cloud.data;



import com.caoping.cloud.entiy.OrderVo;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class OrdersVoDATA extends Data {
    private List<OrderVo> data;

    public List<OrderVo> getData() {
        return data;
    }

    public void setData(List<OrderVo> data) {
        this.data = data;
    }
}
