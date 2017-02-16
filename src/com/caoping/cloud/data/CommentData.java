package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Comment;

import java.util.List;

/**
 * Created by zhl on 2016/11/9.
 */
public class CommentData extends Data {
    private List<Comment> data;

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }
}
