package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Province;

import java.util.List;

/**
 * Created by zhl on 2016/11/10.
 */
public class ProvinceData extends Data {
    private List<Province> data;

    public List<Province> getData() {
        return data;
    }

    public void setData(List<Province> data) {
        this.data = data;
    }
}
