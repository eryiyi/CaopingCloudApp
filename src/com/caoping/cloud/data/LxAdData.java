package com.caoping.cloud.data;

import com.caoping.cloud.entiy.LxAd;

import java.util.List;

/**
 * Created by zhl on 2016/11/13.
 */
public class LxAdData extends Data {
    private List<LxAd> data;

    public List<LxAd> getData() {
        return data;
    }

    public void setData(List<LxAd> data) {
        this.data = data;
    }
}
