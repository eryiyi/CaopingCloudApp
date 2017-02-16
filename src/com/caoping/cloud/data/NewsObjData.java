package com.caoping.cloud.data;

import com.caoping.cloud.entiy.NewsObj;

import java.util.List;

/**
 * Created by zhl on 2016/11/9.
 */
public class NewsObjData extends Data {
    private List<NewsObj> data;

    public List<NewsObj> getData() {
        return data;
    }

    public void setData(List<NewsObj> data) {
        this.data = data;
    }
}
