package com.caoping.cloud.data;



import com.caoping.cloud.entiy.CommentContent;

import java.util.List;

/**
 * Created by zhanghl on 2015/1/17.
 */
public class CommentContentDATA extends Data {
    private List<CommentContent> data;

    public List<CommentContent> getData() {
        return data;
    }

    public void setData(List<CommentContent> data) {
        this.data = data;
    }
}