package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.AreaData;
import com.caoping.cloud.data.CityDATA;
import com.caoping.cloud.data.ProvinceData;
import com.caoping.cloud.data.SuccessData;
import com.caoping.cloud.entiy.Area;
import com.caoping.cloud.entiy.City;
import com.caoping.cloud.entiy.Province;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.CustomerSpinner;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/9.
 * 新增我的收货地址
 */
public class MineAddressAddActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;

    private EditText add_nickname;
//    private TextView add_address_one;
    private EditText add_tel;
    private EditText add_address_two;
    private Button button_add_address;
    //省市县
    private CustomerSpinner province;
    private CustomerSpinner city;
    private CustomerSpinner country;
    private List<Province> provinces = new ArrayList<Province>();//省
    private ArrayList<String> provinceNames = new ArrayList<String>();//省份名称
    private List<City> citys = new ArrayList<City>();//市
    private ArrayList<String> cityNames = new ArrayList<String>();//市名称
    private List<Area> countrys = new ArrayList<Area>();//区
    private ArrayList<String> countrysNames = new ArrayList<String>();//区名称
    ArrayAdapter<String> ProvinceAdapter;
    ArrayAdapter<String> cityAdapter;
    ArrayAdapter<String> countryAdapter;
    private String provinceName = "";
    private String cityName= "";
    private String countryName = "";
    private String provinceCode = "";
    private String cityCode = "";
    private String countryCode = "";

    private Resources res;
    boolean isMobileNet, isWifiNet;

    private CheckBox checkbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_address_add_four_activity);
        res = getResources();

//        cityid = getIntent().getExtras().getString("cityid");
//        cityName = getIntent().getExtras().getString("cityName");
//        provinceId = getIntent().getExtras().getString("provinceId");
//        provinceName = getIntent().getExtras().getString("provinceName");
//        areaId = getIntent().getExtras().getString("areaId");
//        areaName = getIntent().getExtras().getString("areaName");
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineAddressAddActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineAddressAddActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(MineAddressAddActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(MineAddressAddActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getProvince();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        initView();
    }
    private void initView() {
        ProvinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames);
        ProvinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province = (CustomerSpinner) findViewById(R.id.mm_emp_provinceId);
        province.setAdapter(ProvinceAdapter);
        province.setList(provinceNames);
        province.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citys.clear();
                cityNames.clear();
                cityNames.add(getResources().getString(R.string.select_city));
                cityAdapter.notifyDataSetChanged();
                Province province = null;
                if (provinces != null && position > 0) {
                    province = provinces.get(position - 1);
                    provinceName = province.getPname();
                    provinceCode = province.getProvinceId();
                } else if (provinces != null) {
                    province = provinces.get(position);
                    provinceName = province.getPname();
                    provinceCode = province.getProvinceId();
                }
                try {
                    getCitys();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cityAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityNames);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city = (CustomerSpinner) findViewById(R.id.mm_emp_cityId);
        city.setAdapter(cityAdapter);
        city.setList(cityNames);
        city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    countrys.clear();
                    countrysNames.clear();
                    countrysNames.add(getResources().getString(R.string.select_area));
                    City city = citys.get(position - 1);
                    cityName = city.getCityName();
                    cityCode = city.getCityid();
                    try {
                        getArea();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    country.setEnabled(true);
                    countrysNames.clear();
                    countrysNames.add(res.getString(R.string.select_area));
                    countrys.clear();
                    countryAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrysNames);
        countryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country = (CustomerSpinner) findViewById(R.id.mm_emp_countryId);
        country.setAdapter(countryAdapter);
        country.setList(countrysNames);
        country.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Area country = countrys.get(position - 1);
                    countryCode = country.getAreaid();
                    countryName = country.getAreaName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("新增收获地址");

        add_nickname =  (EditText) this.findViewById(R.id.add_nickname);
        add_tel =  (EditText) this.findViewById(R.id.add_tel);
        add_address_two =  (EditText) this.findViewById(R.id.add_address_two);
        button_add_address =  (Button) this.findViewById(R.id.button_add_address);
        checkbox =  (CheckBox) this.findViewById(R.id.checkbox);

        button_add_address.setOnClickListener(this);
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empName", ""), String.class))){
            add_nickname.setText(getGson().fromJson(getSp().getString("empName", ""), String.class));
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empMobile", ""), String.class))){
            add_tel.setText(getGson().fromJson(getSp().getString("empMobile", ""), String.class));
        }
    }
    //获得省份
    public void getProvince() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_PROVINCE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    provinceNames.add(res.getString(R.string.select_province));
                                    ProvinceData data = getGson().fromJson(s, ProvinceData.class);
                                    provinces = data.getData();
                                    if (provinces != null) {
                                        for (int i = 0; i < provinces.size(); i++) {
                                            provinceNames.add(provinces.get(i).getPname());
                                        }
                                    }
                                    ProvinceAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("is_use", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    //获得城市
    public void getCitys() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CITY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    CityDATA data = getGson().fromJson(s, CityDATA.class);
                                    citys = data.getData();
                                    if (citys != null) {
                                        for (int i = 0; i < citys.size(); i++) {
                                            cityNames.add(citys.get(i).getCityName());
                                        }
                                    }
                                    cityAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provinceid", provinceCode);
                params.put("is_use", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }

    //获得地区
    public void getArea() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COUNTRY_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    AreaData data = getGson().fromJson(s, AreaData.class);
                                    countrys = data.getData();
                                    if (countrys != null) {
                                        for (int i = 0; i < countrys.size(); i++) {
                                            countrysNames.add(countrys.get(i).getAreaName());
                                        }
                                    }
                                    countryAdapter.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineAddressAddActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityid", cityCode);
                params.put("is_use", "1");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }






    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.button_add_address:
                //添加
                if (StringUtil.isNullOrEmpty(add_nickname.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_one, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(add_tel.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_two, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(StringUtil.isNullOrEmpty(provinceCode) || StringUtil.isNullOrEmpty(cityCode) || StringUtil.isNullOrEmpty(countryCode)){
                    showMsg(MineAddressAddActivity.this, "请选择省市县！");
                    return;
                }

                if (StringUtil.isNullOrEmpty(add_address_two.getText().toString())) {
                    Toast.makeText(MineAddressAddActivity.this, R.string.add_address_error_three, Toast.LENGTH_SHORT).show();
                    return;
                }

                progressDialog = new CustomProgressDialog(MineAddressAddActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                saveAddress();
                break;
        }
    }

    //保存收货地址
    public void saveAddress(){
        //获得收货地址列表
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.ADD_MINE_ADDRSS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                //成功
                                Intent intent = new Intent("add_address_success");
                                sendBroadcast(intent);
                                finish();
                            } else {
                                Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineAddressAddActivity.this, R.string.address_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("accept_name", add_nickname.getText().toString());
                params.put("address", add_address_two.getText().toString());
                params.put("phone", add_tel.getText().toString());
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                params.put("province", provinceCode);
                params.put("city", cityCode);
                params.put("area", countryCode);
                if(checkbox.isChecked()){
                    params.put("is_default", "1");
                }else {
                    params.put("is_default", "0");
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        getRequestQueue().add(request);
    }
}
