package com.caoping.cloud.entiy;

/**
 * Created by Administrator on 2015/8/14.
 */
public class OrderVo extends Order {

    private String emp_mobile;
    private String emp_name;
    private String emp_cover;

    private String emp_mobile_seller;
    private String emp_name_seller;
    private String emp_cover_seller;

    private String provinceName;//省
    private String cityName;//市
    private String areaName;//区
    private String cloud_caoping_title;//商品名称
    private String cloud_caoping_pic;//商品图片
    private String cloud_caoping_address;//商品地址
    private String cloud_caoping_prices;//商品价格

    public String getEmp_mobile() {
        return emp_mobile;
    }

    public void setEmp_mobile(String emp_mobile) {
        this.emp_mobile = emp_mobile;
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

    public String getEmp_mobile_seller() {
        return emp_mobile_seller;
    }

    public void setEmp_mobile_seller(String emp_mobile_seller) {
        this.emp_mobile_seller = emp_mobile_seller;
    }

    public String getEmp_name_seller() {
        return emp_name_seller;
    }

    public void setEmp_name_seller(String emp_name_seller) {
        this.emp_name_seller = emp_name_seller;
    }

    public String getEmp_cover_seller() {
        return emp_cover_seller;
    }

    public void setEmp_cover_seller(String emp_cover_seller) {
        this.emp_cover_seller = emp_cover_seller;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
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

    public String getCloud_caoping_title() {
        return cloud_caoping_title;
    }

    public void setCloud_caoping_title(String cloud_caoping_title) {
        this.cloud_caoping_title = cloud_caoping_title;
    }

    public String getCloud_caoping_pic() {
        return cloud_caoping_pic;
    }

    public void setCloud_caoping_pic(String cloud_caoping_pic) {
        this.cloud_caoping_pic = cloud_caoping_pic;
    }

    public String getCloud_caoping_address() {
        return cloud_caoping_address;
    }

    public void setCloud_caoping_address(String cloud_caoping_address) {
        this.cloud_caoping_address = cloud_caoping_address;
    }

    public String getCloud_caoping_prices() {
        return cloud_caoping_prices;
    }

    public void setCloud_caoping_prices(String cloud_caoping_prices) {
        this.cloud_caoping_prices = cloud_caoping_prices;
    }

    public OrderVo(String cloud_caoping_id, String emp_id, String seller_emp_id, String address_id, String goods_count, String payable_amount, String distribution_type, String distribution_status, String postscript, String invoice, String invoice_title, String taxes, String provinceId, String cityId, String areaId, String trade_type, String emp_mobile, String emp_name, String emp_cover, String emp_mobile_seller, String emp_name_seller, String emp_cover_seller, String provinceName, String cityName, String areaName, String cloud_caoping_title, String cloud_caoping_pic, String cloud_caoping_address, String cloud_caoping_prices ,String payable_amount_all,String pv_amount, String is_dxk_order, String order_cont) {
        super(cloud_caoping_id, emp_id, seller_emp_id, address_id, goods_count, payable_amount, distribution_type, distribution_status, postscript, invoice, invoice_title, taxes, provinceId, cityId, areaId, trade_type, payable_amount_all, pv_amount,is_dxk_order,order_cont);
        this.emp_mobile = emp_mobile;
        this.emp_name = emp_name;
        this.emp_cover = emp_cover;
        this.emp_mobile_seller = emp_mobile_seller;
        this.emp_name_seller = emp_name_seller;
        this.emp_cover_seller = emp_cover_seller;
        this.provinceName = provinceName;
        this.cityName = cityName;
        this.areaName = areaName;
        this.cloud_caoping_title = cloud_caoping_title;
        this.cloud_caoping_pic = cloud_caoping_pic;
        this.cloud_caoping_address = cloud_caoping_address;
        this.cloud_caoping_prices = cloud_caoping_prices;
    }
}
