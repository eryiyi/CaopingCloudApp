package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Transport;

import java.util.List;

/**
 * Created by zhl on 2016/11/13.
 */
public class TransportData extends Data {
    private List<Transport> data;

    public List<Transport> getData() {
        return data;
    }

    public void setData(List<Transport> data) {
        this.data = data;
    }
}
