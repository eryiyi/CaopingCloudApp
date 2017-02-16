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
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.AreaData;
import com.caoping.cloud.data.CityDATA;
import com.caoping.cloud.data.ProvinceData;
import com.caoping.cloud.entiy.Area;
import com.caoping.cloud.entiy.City;
import com.caoping.cloud.entiy.Province;
import com.caoping.cloud.upload.CommonUtil;
import com.caoping.cloud.util.CompressPhotoUtil;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.CustomerSpinner;
import com.caoping.cloud.widget.SelectPhoPopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 申请供应商
 */
public class ApplyGysActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private TextView right_btn;
    private EditText company_name;
    private EditText company_faren;
    private EditText company_yzzz_num;
    private EditText company_address;
    private EditText company_detail;
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

    private ImageView company_yzzz_pic;
    private ImageView company_faren_pic_z;
    private ImageView company_faren_pic_f;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apply_gys_activity);
        res = getResources();
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(ApplyGysActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(ApplyGysActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(ApplyGysActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(ApplyGysActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getProvince();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setText("提交");
        right_btn.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("申请供应商");

        this.findViewById(R.id.company_yzzz_pic).setOnClickListener(this);
        this.findViewById(R.id.company_faren_pic_z).setOnClickListener(this);
        this.findViewById(R.id.company_faren_pic_f).setOnClickListener(this);

        company_name = (EditText) this.findViewById(R.id.company_name);
        company_faren = (EditText) this.findViewById(R.id.company_faren);
        company_yzzz_num = (EditText) this.findViewById(R.id.company_yzzz_num);
        company_address = (EditText) this.findViewById(R.id.company_address);
        company_detail = (EditText) this.findViewById(R.id.company_detail);

        company_yzzz_pic = (ImageView) this.findViewById(R.id.company_yzzz_pic);
        company_faren_pic_z = (ImageView) this.findViewById(R.id.company_faren_pic_z);
        company_faren_pic_f = (ImageView) this.findViewById(R.id.company_faren_pic_f);

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
                            Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ApplyGysActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    int tmpSelectType = 0;
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
                    showMsg(ApplyGysActivity.this, "请输入公司名称！");
                    return;
                }
                if(company_name.getText().toString().length()>100){
                    showMsg(ApplyGysActivity.this, "公司名称太长，100字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_faren.getText().toString())){
                    showMsg(ApplyGysActivity.this, "请输入法人姓名！");
                    return;
                }
                if(company_faren.getText().toString().length()>50){
                    showMsg(ApplyGysActivity.this, "法人姓名太长，50字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_yzzz_num.getText().toString())){
                    showMsg(ApplyGysActivity.this, "请输入营业执照号！");
                    return;
                }
                if(company_yzzz_num.getText().toString().length()>50){
                    showMsg(ApplyGysActivity.this, "营业执照号太长，50字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(provinceCode)){
                    showMsg(ApplyGysActivity.this, "请选择公司所在省份！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(cityCode)){
                    showMsg(ApplyGysActivity.this, "请选择公司所在城市！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(countryCode)){
                    showMsg(ApplyGysActivity.this, "请选择公司所在县区！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_address.getText().toString())){
                    showMsg(ApplyGysActivity.this, "请输入公司地址！");
                    return;
                }
                if(company_address.getText().toString().length()>100){
                    showMsg(ApplyGysActivity.this, "公司地址太长，100字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(company_detail.getText().toString())){
                    showMsg(ApplyGysActivity.this, "请输入公司主营业务！");
                    return;
                }
                if(company_detail.getText().toString().length()>1000){
                    showMsg(ApplyGysActivity.this, "公司主营业务太长，1000字以内！");
                    return;
                }

                if(StringUtil.isNullOrEmpty(pic1)){
                    showMsg(ApplyGysActivity.this, "请上传公司营业执照！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(pic2)){
                    showMsg(ApplyGysActivity.this, "请上传公司法人身份证(正面)！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(pic3)){
                    showMsg(ApplyGysActivity.this, "请上传公司法人身份证(反面)！");
                    return;
                }
                //判断是否有网
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(ApplyGysActivity.this);
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(ApplyGysActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                        showMsg(ApplyGysActivity.this ,"请检查您网络链接");
                    }else {
                        progressDialog = new CustomProgressDialog(ApplyGysActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        sendFile(pic1);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            break;
            case R.id.company_yzzz_pic:
            {
                //添加图片
                tmpSelectType = 0;
                ShowPickDialog();
            }
            break;
            case R.id.company_faren_pic_z:
            {
                //添加图片
                tmpSelectType= 1;
                ShowPickDialog();
            }
            break;
            case R.id.company_faren_pic_f:
            {
                //添加图片
                tmpSelectType = 2;
                ShowPickDialog();
            }
            break;
        }
    }
    private SelectPhoPopWindow deleteWindow;
    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(ApplyGysActivity.this, itemsOnClick);
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
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    private String pic1 = "";
    private String pic2 = "";
    private String pic3 = "";

    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

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
                switch (tmpSelectType){
                    case 0:
                    {
                        pic1 = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                        company_yzzz_pic.setImageBitmap(photo);
                    }
                        break;
                    case 1:
                    {
                        pic2 = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                        company_faren_pic_z.setImageBitmap(photo);
                    }
                        break;
                    case 2:
                    {
                        pic3 = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                        company_faren_pic_f.setImageBitmap(photo);
                    }
                        break;
                }

            }
        }
    }

    private String picOne;
    private String picTwo;
    private String picThree;

    public void sendFile(final String pic) {
        File f = new File(pic);
        final Map<String, File> files = new HashMap<String, File>();
        files.put("file", f);
        Map<String, String> params = new HashMap<String, String>();
        CommonUtil.addPutUploadFileRequest(
                ApplyGysActivity.this,
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
                                    if(pic.equals(pic1)){
                                        picOne= jo.getString("data");
                                        sendFile(pic2);
                                    }
                                    if(pic.equals(pic2)){
                                        picTwo= jo.getString("data");
                                        sendFile(pic3);
                                    }
                                    if(pic.equals(pic3)){
                                        picThree= jo.getString("data");
                                        save();
                                    }
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


    void save(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_APPLY_GYS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(ApplyGysActivity.this, "申请成功！请等待管理员审核", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    showMsg(ApplyGysActivity.this, jo.getString("message"));
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
                        Toast.makeText(ApplyGysActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("company_name", company_name.getText().toString());
                params.put("company_faren", company_faren.getText().toString());
                params.put("company_detail", company_detail.getText().toString());
                params.put("company_yzzz_num", company_yzzz_num.getText().toString());
                if(!StringUtil.isNullOrEmpty(company_address.getText().toString())){
                    params.put("company_address", company_address.getText().toString());
                }else{
                    params.put("company_address", "");
                }
                if(!StringUtil.isNullOrEmpty(provinceCode)){
                    params.put("company_province_id", provinceCode);
                }
                if(!StringUtil.isNullOrEmpty(cityCode)){
                    params.put("company_city_id", cityCode);
                }
                if(!StringUtil.isNullOrEmpty(countryCode)){
                    params.put("company_area_id", countryCode);
                }
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                params.put("company_yzzz_pic", picOne);
                params.put("company_faren_pic_z", picTwo);
                params.put("company_faren_pic_f", picThree);
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
