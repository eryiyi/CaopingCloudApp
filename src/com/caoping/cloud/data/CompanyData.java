package com.caoping.cloud.data;

import com.caoping.cloud.entiy.Company;

import java.util.List;

/**
 * Created by zhl on 2016/11/10.
 */
public class CompanyData extends Data {
    private List<Company> data;

    public List<Company> getData() {
        return data;
    }

    public void setData(List<Company> data) {
        this.data = data;
    }
}
