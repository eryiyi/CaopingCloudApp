package com.caoping.cloud.entiy;

import java.io.Serializable;

/**
 * Created by zhl on 2016/11/9.
 */
public class Cptype implements Serializable{
    private String cloud_caoping_type_id;//草坪属性ID
    private String cloud_caoping_type_cont;//草坪属性描述
    private String cloud_caoping_type_num;//排序 越大越靠前 默认0

    public String getCloud_caoping_type_id() {
        return cloud_caoping_type_id;
    }

    public void setCloud_caoping_type_id(String cloud_caoping_type_id) {
        this.cloud_caoping_type_id = cloud_caoping_type_id;
    }

    public String getCloud_caoping_type_cont() {
        return cloud_caoping_type_cont;
    }

    public void setCloud_caoping_type_cont(String cloud_caoping_type_cont) {
        this.cloud_caoping_type_cont = cloud_caoping_type_cont;
    }

    public String getCloud_caoping_type_num() {
        return cloud_caoping_type_num;
    }

    public void setCloud_caoping_type_num(String cloud_caoping_type_num) {
        this.cloud_caoping_type_num = cloud_caoping_type_num;
    }
}
