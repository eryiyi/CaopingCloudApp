package com.caoping.cloud.data;

import com.caoping.cloud.entiy.NewsObj;

/**
 * Created by zhl on 2016/11/9.
 */
public class NewsObjSingleData extends Data {
    private NewsObj data;

    public NewsObj getData() {
        return data;
    }

    public void setData(NewsObj data) {
        this.data = data;
    }
}
