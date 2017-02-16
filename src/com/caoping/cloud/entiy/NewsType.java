package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/10/17.
 */
public class NewsType {
    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NewsType() {
    }

    public NewsType(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
