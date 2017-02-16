package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Member;

import java.util.List;

/**
 * Created by zhl on 2016/11/16.
 */
public class EmpsData extends Data {
    private List<Member> data;

    public List<Member> getData() {
        return data;
    }

    public void setData(List<Member> data) {
        this.data = data;
    }
}
