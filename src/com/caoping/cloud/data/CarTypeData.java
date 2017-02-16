package com.caoping.cloud.data;

import com.caoping.cloud.entiy.CarType;

import java.util.List;

/**
 * Created by zhl on 2016/11/13.
 */
public class CarTypeData extends Data {
    private List<CarType> data;

    public List<CarType> getData() {
        return data;
    }

    public void setData(List<CarType> data) {
        this.data = data;
    }
}
