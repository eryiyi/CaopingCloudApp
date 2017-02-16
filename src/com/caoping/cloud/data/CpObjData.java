package com.caoping.cloud.data;

import com.caoping.cloud.entiy.CpObj;

import java.util.List;

/**
 * Created by zhl on 2016/11/10.
 */
public class CpObjData extends Data {
    private List<CpObj> data;

    public List<CpObj> getData() {
        return data;
    }

    public void setData(List<CpObj> data) {
        this.data = data;
    }
}
