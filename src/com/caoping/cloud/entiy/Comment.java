package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/11/9.
 */
public class Comment {
    private String comment_id;//评论ID
    private String mm_msg_id;//被评论的信息ID
    private String comment_fplid;//父评论ID  如果不存在就是空
    private String comment_emp_id;//评论人ID
    private String comment_content;//评论内容 最多500字
    private String dateline;//评论时间 后台自动添加 毫秒值

    private String emp_name;//评论者姓名
    private String emp_cover;//评论者头像

    private String emp_name_f;//父评论者姓名
    private String emp_cover_f;//父评论者头像

    public String getEmp_name_f() {
        return emp_name_f;
    }

    public void setEmp_name_f(String emp_name_f) {
        this.emp_name_f = emp_name_f;
    }

    public String getEmp_cover_f() {
        return emp_cover_f;
    }

    public void setEmp_cover_f(String emp_cover_f) {
        this.emp_cover_f = emp_cover_f;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_cover() {
        return emp_cover;
    }

    public void setEmp_cover(String emp_cover) {
        this.emp_cover = emp_cover;
    }

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }

    public String getMm_msg_id() {
        return mm_msg_id;
    }

    public void setMm_msg_id(String mm_msg_id) {
        this.mm_msg_id = mm_msg_id;
    }

    public String getComment_fplid() {
        return comment_fplid;
    }

    public void setComment_fplid(String comment_fplid) {
        this.comment_fplid = comment_fplid;
    }

    public String getComment_emp_id() {
        return comment_emp_id;
    }

    public void setComment_emp_id(String comment_emp_id) {
        this.comment_emp_id = comment_emp_id;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }
}
