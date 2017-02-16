package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/11/10.
 * 公司对象
 */
public class Company {
    private String company_id;
    private String company_name;//公司标题
    private String company_detail;//公司简介
    private String company_tel;//联系人
    private String company_person;//电话
    private String company_address;//公司地址
    private String lat_company;//公司经纬度
    private String lng_company;
    private String company_pic;//公司图片
    private String is_check;//默认0 未审核 1审核
    private String emp_id;//公司会员ID
    private String dateline;

    private String is_paihang;//是否排行榜  0否 1是
    private String end_time;//到期时间
    private String paihang_num;//排行榜排序

    private String provinceid;
    private String cityid;
    private String areaid;

    private String emp_name;
    private String emp_cover;
    private String emp_mobile;

    private String pname;//省
    private String cityName;//市
    private String areaName;//县


    public String getIs_paihang() {
        return is_paihang;
    }

    public void setIs_paihang(String is_paihang) {
        this.is_paihang = is_paihang;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getPaihang_num() {
        return paihang_num;
    }

    public void setPaihang_num(String paihang_num) {
        this.paihang_num = paihang_num;
    }

    public String getDateline() {
        return dateline;
    }

    public void setDateline(String dateline) {
        this.dateline = dateline;
    }

    public String getPname() {
        return pname;
    }

    public void setPname(String pname) {
        this.pname = pname;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getProvinceid() {
        return provinceid;
    }

    public void setProvinceid(String provinceid) {
        this.provinceid = provinceid;
    }

    public String getCityid() {
        return cityid;
    }

    public void setCityid(String cityid) {
        this.cityid = cityid;
    }

    public String getAreaid() {
        return areaid;
    }

    public void setAreaid(String areaid) {
        this.areaid = areaid;
    }

    public String getCompany_pic() {
        return company_pic;
    }

    public void setCompany_pic(String company_pic) {
        this.company_pic = company_pic;
    }

    public String getCompany_tel() {
        return company_tel;
    }

    public void setCompany_tel(String company_tel) {
        this.company_tel = company_tel;
    }

    public String getCompany_person() {
        return company_person;
    }

    public void setCompany_person(String company_person) {
        this.company_person = company_person;
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

    public String getEmp_mobile() {
        return emp_mobile;
    }

    public void setEmp_mobile(String emp_mobile) {
        this.emp_mobile = emp_mobile;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_detail() {
        return company_detail;
    }

    public void setCompany_detail(String company_detail) {
        this.company_detail = company_detail;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getLat_company() {
        return lat_company;
    }

    public void setLat_company(String lat_company) {
        this.lat_company = lat_company;
    }

    public String getLng_company() {
        return lng_company;
    }

    public void setLng_company(String lng_company) {
        this.lng_company = lng_company;
    }

    public String getIs_check() {
        return is_check;
    }

    public void setIs_check(String is_check) {
        this.is_check = is_check;
    }
}
