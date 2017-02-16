package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Area;

import java.util.List;

/**
 * Created by zhl on 2016/11/10.
 */
public class AreaData extends Data {
    private List<Area> data;

    public List<Area> getData() {
        return data;
    }

    public void setData(List<Area> data) {
        this.data = data;
    }
}
