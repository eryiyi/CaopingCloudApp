package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemCityAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CityDATA;
import com.caoping.cloud.entiy.City;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 选择城市
 */
public class SelectCityActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private List<City> provinces = new ArrayList<City>();//省
    private ItemCityAdapter adapter;
    private PullToRefreshListView lstv;
    private ImageView search_null;
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;
    
    private Resources res;
    boolean isMobileNet, isWifiNet;
    private String provinceid;
    private String isType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_province_activity);
        isType = getIntent().getExtras().getString("isType");
        provinceid = getIntent().getExtras().getString("provinceid");
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(SelectCityActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(SelectCityActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(SelectCityActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(SelectCityActivity.this, "正在加载中",R.anim.custom_dialog_frame);
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
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("选择城市");

        adapter = new ItemCityAdapter(SelectCityActivity.this ,provinces);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        lstv.setAdapter(adapter);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectCityActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                getProvince();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectCityActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = false;
                pageIndex2++;
                getProvince();
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(provinces.size()>(position-1)){
                    City pro = provinces.get(position-1);
                    if(pro != null){
                        Intent intent  = new Intent(SelectCityActivity.this, SelectAreaActivity.class);
                        intent.putExtra("cityid", pro.getCityid());
                        intent.putExtra("isType", isType);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    //获得省份
    public void getProvince() {
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
                                    if (IS_REFRESH2) {
                                        provinces.clear();
                                    }
                                    provinces.addAll(data.getData());
                                    lstv.onRefreshComplete();
                                    adapter.notifyDataSetChanged();
                                    if(provinces.size() == 0){
                                        search_null2.setVisibility(View.VISIBLE);
                                        lstv.setVisibility(View.GONE);
                                    }else {
                                        search_null2.setVisibility(View.GONE);
                                        lstv.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(SelectCityActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectCityActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("provinceid", provinceid);
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
    
}
