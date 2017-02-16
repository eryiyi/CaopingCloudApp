package com.caoping.cloud.data;

import com.caoping.cloud.entiy.CarLength;

import java.util.List;

/**
 * Created by zhl on 2016/11/13.
 */
public class CarLengthData extends Data {
    private List<CarLength> data;

    public List<CarLength> getData() {
        return data;
    }

    public void setData(List<CarLength> data) {
        this.data = data;
    }
}
