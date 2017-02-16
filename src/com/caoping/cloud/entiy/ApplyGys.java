package com.caoping.cloud.entiy;

/**
 * Created by zhl on 2016/11/12.
 * 申请供应商
 */
public class ApplyGys {
    private String apply_gys_id;
    private String emp_id;//会员ID
    private String company_name;//公司名
    private String company_faren;//法人姓名
    private String company_detail;//公司从事的行业
    private String company_yzzz_num;//营业执照号码
    private String company_province_id;
    private String company_city_id;
    private String company_area_id;
    private String company_address;//详细地址
    private String company_yzzz_pic;//营业执照
    private String company_faren_pic_z;//法人身份证正面
    private String company_faren_pic_f;//法人身份证反面
    private String dateline_apply;//提交时申请时间
    private String dateline_check;//审核时间
    private String check_reason;//审核不通过的原因
    private String is_check;//是否审核 默认0否 1是 2不通过

    private String emp_name;
    private String emp_mobile;
    private String emp_cover;

    private String pname;
    private String cityName;
    private String areaName;

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

    public String getEmp_name() {
        return emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getEmp_mobile() {
        return emp_mobile;
    }

    public void setEmp_mobile(String emp_mobile) {
        this.emp_mobile = emp_mobile;
    }

    public String getEmp_cover() {
        return emp_cover;
    }

    public void setEmp_cover(String emp_cover) {
        this.emp_cover = emp_cover;
    }

    public String getApply_gys_id() {
        return apply_gys_id;
    }

    public void setApply_gys_id(String apply_gys_id) {
        this.apply_gys_id = apply_gys_id;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getCompany_faren() {
        return company_faren;
    }

    public void setCompany_faren(String company_faren) {
        this.company_faren = company_faren;
    }

    public String getCompany_detail() {
        return company_detail;
    }

    public void setCompany_detail(String company_detail) {
        this.company_detail = company_detail;
    }

    public String getCompany_yzzz_num() {
        return company_yzzz_num;
    }

    public void setCompany_yzzz_num(String company_yzzz_num) {
        this.company_yzzz_num = company_yzzz_num;
    }

    public String getCompany_province_id() {
        return company_province_id;
    }

    public void setCompany_province_id(String company_province_id) {
        this.company_province_id = company_province_id;
    }

    public String getCompany_city_id() {
        return company_city_id;
    }

    public void setCompany_city_id(String company_city_id) {
        this.company_city_id = company_city_id;
    }

    public String getCompany_area_id() {
        return company_area_id;
    }

    public void setCompany_area_id(String company_area_id) {
        this.company_area_id = company_area_id;
    }

    public String getCompany_address() {
        return company_address;
    }

    public void setCompany_address(String company_address) {
        this.company_address = company_address;
    }

    public String getCompany_yzzz_pic() {
        return company_yzzz_pic;
    }

    public void setCompany_yzzz_pic(String company_yzzz_pic) {
        this.company_yzzz_pic = company_yzzz_pic;
    }

    public String getCompany_faren_pic_z() {
        return company_faren_pic_z;
    }

    public void setCompany_faren_pic_z(String company_faren_pic_z) {
        this.company_faren_pic_z = company_faren_pic_z;
    }

    public String getCompany_faren_pic_f() {
        return company_faren_pic_f;
    }

    public void setCompany_faren_pic_f(String company_faren_pic_f) {
        this.company_faren_pic_f = company_faren_pic_f;
    }

    public String getDateline_apply() {
        return dateline_apply;
    }

    public void setDateline_apply(String dateline_apply) {
        this.dateline_apply = dateline_apply;
    }

    public String getDateline_check() {
        return dateline_check;
    }

    public void setDateline_check(String dateline_check) {
        this.dateline_check = dateline_check;
    }

    public String getCheck_reason() {
        return check_reason;
    }

    public void setCheck_reason(String check_reason) {
        this.check_reason = check_reason;
    }

    public String getIs_check() {
        return is_check;
    }

    public void setIs_check(String is_check) {
        this.is_check = is_check;
    }
}
