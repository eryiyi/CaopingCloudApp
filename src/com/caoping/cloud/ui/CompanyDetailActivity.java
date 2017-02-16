package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.AreaData;
import com.caoping.cloud.data.CityDATA;
import com.caoping.cloud.data.CompanyDataSingle;
import com.caoping.cloud.data.ProvinceData;
import com.caoping.cloud.entiy.Area;
import com.caoping.cloud.entiy.City;
import com.caoping.cloud.entiy.Company;
import com.caoping.cloud.entiy.Province;
import com.caoping.cloud.upload.CommonUtil;
import com.caoping.cloud.util.CompressPhotoUtil;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.CustomerSpinner;
import com.caoping.cloud.widget.SelectPhoPopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 公司详情
 */
public class CompanyDetailActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private TextView right_btn;

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
    private String cityName = "";
    private String countryName = "";
    private String provinceCode = "";
    private String cityCode = "";
    private String countryCode = "";

    private Resources res;
    boolean isMobileNet, isWifiNet;

    private EditText company_name;
    private EditText company_detail;
    private EditText company_person;
    private EditText company_tel;
    private EditText company_address;

    private TextView company_address_one;

    private LinearLayout liner_area;//区域

    private ImageView md_add_pic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = this.getResources();
        setContentView(R.layout.company_detail_activity);
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(CompanyDetailActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(CompanyDetailActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(CompanyDetailActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(CompanyDetailActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getProvince();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        getDetail();
    }

    private void initView() {
        liner_area = (LinearLayout) this.findViewById(R.id.liner_area);
        company_name = (EditText) this.findViewById(R.id.company_name);
        company_detail = (EditText) this.findViewById(R.id.company_detail);
        company_person = (EditText) this.findViewById(R.id.company_person);
        company_tel = (EditText) this.findViewById(R.id.company_tel);
        company_address = (EditText) this.findViewById(R.id.company_address);
        company_address_one = (TextView) this.findViewById(R.id.company_address_one);
        company_address_one.setVisibility(View.GONE);
        md_add_pic = (ImageView) this.findViewById(R.id.md_add_pic);
        md_add_pic.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("公司详情");
        right_btn.setText("保存");

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
                            Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(CompanyDetailActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.right_btn:
            {
                //保存
                if(StringUtil.isNullOrEmpty(company_name.getText().toString())){
                    showMsg(CompanyDetailActivity.this, "请输入公司名称！");
                    return;
                }
                if(company_name.getText().toString().length()>100){
                    showMsg(CompanyDetailActivity.this, "公司名称太长，100字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_person.getText().toString())){
                    showMsg(CompanyDetailActivity.this, "请输入联系人！");
                    return;
                }
                if(company_person.getText().toString().length()>50){
                    showMsg(CompanyDetailActivity.this, "联系人太长，50字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_tel.getText().toString())){
                    showMsg(CompanyDetailActivity.this, "请输入联系电话！");
                    return;
                }
                if(company_tel.getText().toString().length()>20){
                    showMsg(CompanyDetailActivity.this, "联系电话太长，20字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_detail.getText().toString())){
                    showMsg(CompanyDetailActivity.this, "请输入公司详情！");
                    return;
                }
                if(company_detail.getText().toString().length()>1000){
                    showMsg(CompanyDetailActivity.this, "公司详情太长，1000字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_address.getText().toString())){
                    showMsg(CompanyDetailActivity.this, "请输入公司地址！");
                    return;
                }
                if(company_address.getText().toString().length()>100){
                    showMsg(CompanyDetailActivity.this, "公司地址太长，100字以内！");
                    return;
                }
                if(company == null){
                    //说明是初次
                    if(StringUtil.isNullOrEmpty(provinceCode)){
                        showMsg(CompanyDetailActivity.this, "请选择公司所在省份！");
                        return;
                    }
                    if(StringUtil.isNullOrEmpty(cityCode)){
                        showMsg(CompanyDetailActivity.this, "请选择公司所在城市！");
                        return;
                    }
                    if(StringUtil.isNullOrEmpty(countryCode)){
                        showMsg(CompanyDetailActivity.this, "请选择公司所在县区！");
                        return;
                    }
                    if(StringUtil.isNullOrEmpty(pics)){
                        showMsg(CompanyDetailActivity.this, "请选择公司图片！");
                        return;
                    }
                }
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(CompanyDetailActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(CompanyDetailActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        showMsg(CompanyDetailActivity.this ,"请检查您网络链接");
                    }else {
                        progressDialog = new CustomProgressDialog(CompanyDetailActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        save();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
                break;
            case R.id.md_add_pic:
            {
                //添加图片
                ShowPickDialog();
            }
                break;
        }
    }

    void save(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_COMPANY_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(CompanyDetailActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                                    CompanyDataSingle  data = getGson().fromJson(s, CompanyDataSingle.class);
                                    //上传图片
                                    if(!StringUtil.isNullOrEmpty(pics)){
                                        company = data.getData() ;
                                        if(company != null){
                                            sendFile(pics);
                                        }
                                    }

                                }else{
                                    showMsg(CompanyDetailActivity.this, jo.getString("message"));
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
                        Toast.makeText(CompanyDetailActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company_name", company_name.getText().toString());
                params.put("company_detail", company_detail.getText().toString());
                if(!StringUtil.isNullOrEmpty( company_person.getText().toString())){
                    params.put("company_person", company_person.getText().toString());
                }else {
                    params.put("company_person", "");
                }
                if(!StringUtil.isNullOrEmpty(company_tel.getText().toString())){
                    params.put("company_tel", company_tel.getText().toString());
                }else{
                    params.put("company_tel", "");
                }
                if(!StringUtil.isNullOrEmpty(company_address.getText().toString())){
                    params.put("company_address", company_address.getText().toString());
                }else{
                    params.put("company_address", "");
                }
                if(!StringUtil.isNullOrEmpty(provinceCode)){
                    params.put("provinceid", provinceCode);
                }else{
                    if(company != null){
                        params.put("provinceid", company.getProvinceid());
                    }
                }
                if(!StringUtil.isNullOrEmpty(cityCode)){
                    params.put("cityid", cityCode);
                }else{
                    if(company != null){
                        params.put("cityid", company.getCityid());
                    }
                }
                if(!StringUtil.isNullOrEmpty(countryCode)){
                    params.put("areaid", countryCode);
                }else{
                    if(company != null){
                        params.put("areaid", company.getAreaid());
                    }
                }
                if(company != null){
                    if(!StringUtil.isNullOrEmpty(company.getCompany_id())){
                        params.put("company_id", company.getCompany_id());
                    }else{
                        params.put("company_id", "");
                    }
                }
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
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

    private Company company;

    //查询公司详情
    void getDetail(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COMPANY_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    CompanyDataSingle data = getGson().fromJson(s, CompanyDataSingle.class);
                                    company = data.getData();
                                    if(company != null ){
                                        initData();
                                        //说明存在了
                                        liner_area.setVisibility(View.GONE);
                                        company_address_one.setVisibility(View.VISIBLE);
                                    }else {
                                        company_address_one.setVisibility(View.GONE);
                                        liner_area.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    company_address_one.setVisibility(View.GONE);
                                    liner_area.setVisibility(View.VISIBLE);
                                    showMsg(CompanyDetailActivity.this, jo.getString("message"));
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
                        company_address_one.setVisibility(View.GONE);
                        liner_area.setVisibility(View.VISIBLE);
                        Toast.makeText(CompanyDetailActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
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

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    void initData(){
        if(company != null){
            company_name.setText(company.getCompany_name()==null?"":company.getCompany_name());
            company_person.setText(company.getCompany_person()==null?"":company.getCompany_person());
            company_address.setText(company.getCompany_address()==null?"":company.getCompany_address());
            company_tel.setText(company.getCompany_tel()==null?"":company.getCompany_tel());
            company_detail.setText(company.getCompany_detail()==null?"":company.getCompany_detail());
            company_address_one.setText("地址："+ (company.getPname()+company.getCityName()+company.getAreaName()));
            imageLoader.displayImage(company.getCompany_pic(),md_add_pic, CaopingCloudApplication.options, animateFirstListener);
        }
    }

    private SelectPhoPopWindow deleteWindow;
    private String pics = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(CompanyDetailActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 0);
        intent.putExtra("aspectY", 0);
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                md_add_pic.setImageBitmap(photo);
            }
        }
    }

    public void sendFile(final String pic) {
        File f = new File(pic);
        final Map<String, File> files = new HashMap<String, File>();
        files.put("file", f);
        Map<String, String> params = new HashMap<String, String>();
        CommonUtil.addPutUploadFileRequest(
                CompanyDetailActivity.this,
                InternetURL.UPLOAD_FILE,
                files,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    String coverStr = jo.getString("data");
                                    sendCover(coverStr);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                null);
    }

    void sendCover(final String coverStr){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_COMPANY_DETAIL_PIC_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    finish();
                                }else{
                                    showMsg(CompanyDetailActivity.this, jo.getString("message"));
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
                        Toast.makeText(CompanyDetailActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if(company !=null){
                    params.put("company_id", company.getCompany_id());
                }
                params.put("company_pic", coverStr);
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
