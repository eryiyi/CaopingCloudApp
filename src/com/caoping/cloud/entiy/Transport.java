package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/11/13.
 */
public class Transport {
    private String transport_id;
    private String emp_id;
    private String is_type;//0车辆信息 1货源信息
    private String start_provinceid;//出发省份
    private String start_cityid;//出发城市
    private String start_areaid;//出发地区
    private String end_provinceid;
    private String end_cityid;
    private String end_areaid;
    private String start_time;//出发时间
    private String end_time;
    private String detail;//描述
    private String car_length_id;//车长ID
    private String car_type_id;//车型ID
    private String car_num;//车数量
    private String person;//联系人
    private String tel;//电话
    private String is_use;//默认0   1过期
    private String is_del;//默认0  1删除
    private String dateline;//发布时间
    private String lat;//信息发布时的经纬度
    private String lng;//信息发布时的经纬度
    private String car_pic;//车辆照片 最多9张

    private String emp_name;
    private String car_length_name;//车长
    private String car_type_name;//车型

    private String start_pname;
    private String start_cityName;
    private String start_areaName;
    private String end_pname;
    private String end_cityName;
    private String end_areaName;

    public String getCar_pic() {
        return car_pic;
    }

    public void setCar_pic(String car_pic) {
        this.car_pic = car_pic;
    }

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getCar_length_name() {
        return car_length_name;
    }

    public void setCar_length_name(String car_length_name) {
        this.car_length_name = car_length_name;
    }

    public String getCar_type_name() {
        return car_type_name;
    }

    public void setCar_type_name(String car_type_name) {
        this.car_type_name = car_type_name;
    }

    public String getStart_pname() {
        return start_pname;
    }

    public void setStart_pname(String start_pname) {
        this.start_pname = start_pname;
    }

    public String getStart_cityName() {
        return start_cityName;
    }

    public void setStart_cityName(String start_cityName) {
        this.start_cityName = start_cityName;
    }

    public String getStart_areaName() {
        return start_areaName;
    }

    public void setStart_areaName(String start_areaName) {
        this.start_areaName = start_areaName;
    }

    public String getEnd_pname() {
        return end_pname;
    }

    public void setEnd_pname(String end_pname) {
        this.end_pname = end_pname;
    }

    public String getEnd_cityName() {
        return end_cityName;
    }

    public void setEnd_cityName(String end_cityName) {
        this.end_cityName = end_cityName;
    }

    public String getEnd_areaName() {
        return end_areaName;
    }

    public void setEnd_areaName(String end_areaName) {
        this.end_areaName = end_areaName;
    }

    public String getTransport_id() {
        return transport_id;
    }

    public void setTransport_id(String transport_id) {
        this.transport_id = transport_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getIs_type() {
        return is_type;
    }

    public void setIs_type(String is_type) {
        this.is_type = is_type;
    }

    public String getStart_provinceid() {
        return start_provinceid;
    }

    public void setStart_provinceid(String start_provinceid) {
        this.start_provinceid = start_provinceid;
    }

    public String getStart_cityid() {
        return start_cityid;
    }

    public void setStart_cityid(String start_cityid) {
        this.start_cityid = start_cityid;
    }

    public String getStart_areaid() {
        return start_areaid;
    }

    public void setStart_areaid(String start_areaid) {
        this.start_areaid = start_areaid;
    }

    public String getEnd_provinceid() {
        return end_provinceid;
    }

    public void setEnd_provinceid(String end_provinceid) {
        this.end_provinceid = end_provinceid;
    }

    public String getEnd_cityid() {
        return end_cityid;
    }

    public void setEnd_cityid(String end_cityid) {
        this.end_cityid = end_cityid;
    }

    public String getEnd_areaid() {
        return end_areaid;
    }

    public void setEnd_areaid(String end_areaid) {
        this.end_areaid = end_areaid;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getCar_length_id() {
        return car_length_id;
    }

    public void setCar_length_id(String car_length_id) {
        this.car_length_id = car_length_id;
    }

    public String getCar_type_id() {
        return car_type_id;
    }

    public void setCar_type_id(String car_type_id) {
        this.car_type_id = car_type_id;
    }

    public String getCar_num() {
        return car_num;
    }

    public void setCar_num(String car_num) {
        this.car_num = car_num;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getIs_use() {
        return is_use;
    }

    public void setIs_use(String is_use) {
        this.is_use = is_use;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

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
}
