package com.caoping.cloud.entiy;

import java.io.Serializable;

/**
 * Created by zhl on 2016/11/9.
 */
public class NewsObj implements Serializable{
    private String mm_msg_id;
    private String emp_id;
    private String mm_msg_type;
    private String mm_msg_title;
    private String mm_msg_content;
    private String dateline;
    private String is_del;
    private String is_top;
    private String top_num;
    private String mm_msg_picurl;
    private String lat;//经纬度
    private String lng;//经纬度
    private String location;//附近位置

    //vo
    private String empName;
    private String empCover;
    private String companyName;

    private String commentNum;//评论数量
    private String favourNum;//赞数量

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public String getFavourNum() {
        return favourNum;
    }

    public void setFavourNum(String favourNum) {
        this.favourNum = favourNum;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public String getEmpCover() {
        return empCover;
    }

    public void setEmpCover(String empCover) {
        this.empCover = empCover;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getMm_msg_id() {
        return mm_msg_id;
    }

    public void setMm_msg_id(String mm_msg_id) {
        this.mm_msg_id = mm_msg_id;
    }


    public String getMm_msg_type() {
        return mm_msg_type;
    }

    public void setMm_msg_type(String mm_msg_type) {
        this.mm_msg_type = mm_msg_type;
    }

    public String getMm_msg_title() {
        return mm_msg_title;
    }

    public void setMm_msg_title(String mm_msg_title) {
        this.mm_msg_title = mm_msg_title;
    }

    public String getMm_msg_content() {
        return mm_msg_content;
    }

    public void setMm_msg_content(String mm_msg_content) {
        this.mm_msg_content = mm_msg_content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getIs_top() {
        return is_top;
    }

    public void setIs_top(String is_top) {
        this.is_top = is_top;
    }

    public String getTop_num() {
        return top_num;
    }

    public void setTop_num(String top_num) {
        this.top_num = top_num;
    }

    public String getMm_msg_picurl() {
        return mm_msg_picurl;
    }

    public void setMm_msg_picurl(String mm_msg_picurl) {
        this.mm_msg_picurl = mm_msg_picurl;
    }
}
