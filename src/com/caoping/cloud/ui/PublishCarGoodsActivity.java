package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.AreaData;
import com.caoping.cloud.data.CityDATA;
import com.caoping.cloud.data.ProvinceData;
import com.caoping.cloud.entiy.Area;
import com.caoping.cloud.entiy.City;
import com.caoping.cloud.entiy.Province;
import com.caoping.cloud.util.DateUtil;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.CustomerSpinner;
import com.caoping.cloud.widget.DateTimePickDialogUtil;
import com.caoping.cloud.widget.PublishPopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 发布货物信息
 */
public class PublishCarGoodsActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private TextView right_btn;
    //省市县
    private CustomerSpinner province1;
    private CustomerSpinner city1;
    private CustomerSpinner country1;
    private List<Province> provinces1 = new ArrayList<Province>();//省
    private ArrayList<String> provinceNames1 = new ArrayList<String>();//省份名称
    private List<City> citys1 = new ArrayList<City>();//市
    private ArrayList<String> cityNames1 = new ArrayList<String>();//市名称
    private List<Area> countrys1 = new ArrayList<Area>();//区
    private ArrayList<String> countrysNames1 = new ArrayList<String>();//区名称
    ArrayAdapter<String> ProvinceAdapter1;
    ArrayAdapter<String> cityAdapter1;
    ArrayAdapter<String> countryAdapter1;
    private String provinceName1 = "";
    private String cityName1 = "";
    private String countryName1 = "";
    private String provinceCode1 = "";
    private String cityCode1 = "";
    private String countryCode1 = "";

    private Resources res;
    boolean isMobileNet, isWifiNet;

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
    private String cityName = "";
    private String countryName = "";
    private String provinceCode = "";
    private String cityCode = "";
    private String countryCode = "";

    private EditText start_time;

    private EditText detail;
    private EditText person;
    private EditText tel;


    private String initStartDateTime = "2013年9月3日 14:44"; // 初始化开始时间
    private String initEndDateTime = "2014年8月23日 17:44"; // 初始化结束时间

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_car_goods_activity);
        res = getResources();
        initStartDateTime = DateUtil.getNoteDateline();
        initEndDateTime = DateUtil.getNoteDateline();
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(PublishCarGoodsActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(PublishCarGoodsActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(PublishCarGoodsActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(PublishCarGoodsActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getProvince();
                getProvince1();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        start_time = (EditText) this.findViewById(R.id.start_time);

        detail = (EditText) this.findViewById(R.id.detail);
        person = (EditText) this.findViewById(R.id.person);
        tel = (EditText) this.findViewById(R.id.tel);

        start_time.setText(initStartDateTime);

        start_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(
                        PublishCarGoodsActivity.this, initEndDateTime);
                dateTimePicKDialog.dateTimePicKDialog(start_time);

            }
        });

        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empName", ""), String.class))){
            person.setText(getGson().fromJson(getSp().getString("empName", ""), String.class));
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empMobile", ""), String.class))){
            tel.setText(getGson().fromJson(getSp().getString("empMobile", ""), String.class));
        }

        this.findViewById(R.id.back).setOnClickListener(this);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("发布货物信息");
        right_btn.setText("保存");
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setOnClickListener(this);
        ProvinceAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, provinceNames1);
        ProvinceAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province1 = (CustomerSpinner) findViewById(R.id.mm_emp_provinceId1);
        province1.setAdapter(ProvinceAdapter1);
        province1.setList(provinceNames1);
        province1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                citys1.clear();
                cityNames1.clear();
                cityNames1.add(getResources().getString(R.string.select_city));
                cityAdapter1.notifyDataSetChanged();
                Province province = null;
                if (provinces1 != null && position > 0) {
                    province = provinces1.get(position - 1);
                    provinceName1 = province.getPname();
                    provinceCode1 = province.getProvinceId();
                } else if (provinces1 != null) {
                    province = provinces1.get(position);
                    provinceName1 = province.getPname();
                    provinceCode1 = province.getProvinceId();
                }
                try {
                    getCitys1();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        cityAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, cityNames1);
        cityAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        city1 = (CustomerSpinner) findViewById(R.id.mm_emp_cityId1);
        city1.setAdapter(cityAdapter1);
        city1.setList(cityNames1);
        city1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    countrys1.clear();
                    countrysNames1.clear();
                    countrysNames1.add(getResources().getString(R.string.select_area));
                    City city = citys1.get(position - 1);
                    cityName1 = city.getCityName();
                    cityCode1 = city.getCityid();
                    try {
                        getArea1();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    country1.setEnabled(true);
                    countrysNames1.clear();
                    countrysNames1.add(res.getString(R.string.select_area));
                    countrys1.clear();
                    countryAdapter1.notifyDataSetChanged();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        countryAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, countrysNames1);
        countryAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        country1 = (CustomerSpinner) findViewById(R.id.mm_emp_countryId1);
        country1.setAdapter(countryAdapter1);
        country1.setList(countrysNames1);
        country1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Area country = countrys1.get(position - 1);
                    countryCode1 = country.getAreaid();
                    countryName1 = country.getAreaName();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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




    //获得省份
    public void getProvince1() {
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
                                    provinceNames1.add(res.getString(R.string.select_province));
                                    ProvinceData data = getGson().fromJson(s, ProvinceData.class);
                                    provinces1 = data.getData();
                                    if (provinces1 != null) {
                                        for (int i = 0; i < provinces1.size(); i++) {
                                            provinceNames1.add(provinces1.get(i).getPname());
                                        }
                                    }
                                    ProvinceAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
    public void getCitys1() {
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
                                    citys1 = data.getData();
                                    if (citys1 != null) {
                                        for (int i = 0; i < citys1.size(); i++) {
                                            cityNames1.add(citys1.get(i).getCityName());
                                        }
                                    }
                                    cityAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provinceid", provinceCode1);
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
    public void getArea1() {
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
                                    countrys1 = data.getData();
                                    if (countrys1 != null) {
                                        for (int i = 0; i < countrys1.size(); i++) {
                                            countrysNames1.add(countrys1.get(i).getAreaName());
                                        }
                                    }
                                    countryAdapter1.notifyDataSetChanged();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityid", cityCode1);
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
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.right_btn:
            {
                //发布
                if(StringUtil.isNullOrEmpty(provinceCode) || StringUtil.isNullOrEmpty(cityCode) || StringUtil.isNullOrEmpty(countryCode)){
                    showMsg(PublishCarGoodsActivity.this, "请选择发货地！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(provinceCode1) || StringUtil.isNullOrEmpty(cityCode1) || StringUtil.isNullOrEmpty(countryCode1)){
                    showMsg(PublishCarGoodsActivity.this, "请选择收货地！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(start_time.getText().toString())){
                    showMsg(PublishCarGoodsActivity.this, "请选择开始时间！");
                    return;
                }

                if(detail.getText().toString().length() > 1000){
                    showMsg(PublishCarGoodsActivity.this, "内容超出字段限制，1000字以内！");
                    return;
                }

                if(StringUtil.isNullOrEmpty(person.getText().toString())){
                    showMsg(PublishCarGoodsActivity.this, "请输入联系人姓名！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(tel.getText().toString())){
                    showMsg(PublishCarGoodsActivity.this, "请输入联系电话！");
                    return;
                }

//                if(dataList.size() == 1){
//                    showMsg(AddCaozhongActivity.this, "请至少选择一张图片！");
//                    return;
//                }
//
//                if(StringUtil.isNullOrEmpty(cloud_caozhong_type_id)){
//                    showMsg(AddCaozhongActivity.this, "请选择品种！");
//                    return;
//                }
//                if (uploadPaths.size() ==( dataList.size()-1)) {
                    publishAll();
//                }else {
//                    uploadPaths.clear();
//                    addPic();
//                }
            }
                break;
            case R.id.car_type_id:
            {
                //车型
                Intent intent = new Intent(PublishCarGoodsActivity.this, SelectCarTypeActivity.class);
                startActivityForResult(intent, 1000);
            }
                break;
            case R.id.car_length_id:
            {
                //车长
                Intent intent = new Intent(PublishCarGoodsActivity.this, SelectCarLengthActivity.class);
                startActivityForResult(intent, 1000);
            }
            break;
        }
    }


    private void publishAll() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_TRANSPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(PublishCarGoodsActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent("add_car_success");
                                    sendBroadcast(intent1);
                                    finish();
                                }else{
                                    showMsg(PublishCarGoodsActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                        Toast.makeText(PublishCarGoodsActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("start_provinceid", provinceCode);
                params.put("start_cityid", cityCode);
                params.put("start_areaid", countryCode);
                params.put("end_provinceid", provinceCode1);
                params.put("end_cityid", cityCode1);
                params.put("end_areaid", countryCode1);
                params.put("start_time", start_time.getText().toString());
                params.put("end_time", "");
                if(!StringUtil.isNullOrEmpty( detail.getText().toString())){
                    params.put("detail", detail.getText().toString());
                }

                if(!StringUtil.isNullOrEmpty( person.getText().toString())){
                    params.put("person", person.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty( tel.getText().toString())){
                    params.put("tel", tel.getText().toString());
                }

                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                        params.put("lat", CaopingCloudApplication.latStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                        params.put("lng", CaopingCloudApplication.lngStr);
                    }

                params.put("is_type", "1");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private PublishPopWindow publishPopWindow;

    // 选择是否退出发布
    private void showBackPop() {
        publishPopWindow = new PublishPopWindow(PublishCarGoodsActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(PublishCarGoodsActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            showBackPop();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
