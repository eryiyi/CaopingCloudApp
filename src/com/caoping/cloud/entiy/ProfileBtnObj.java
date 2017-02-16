package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/10/16.
 */
public class ProfileBtnObj {
    private String title;
    private int pic;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPic() {
        return pic;
    }

    public void setPic(int pic) {
        this.pic = pic;
    }

    public ProfileBtnObj(String title, int pic) {
        this.title = title;
        this.pic = pic;
    }

    public ProfileBtnObj() {

    }
}
