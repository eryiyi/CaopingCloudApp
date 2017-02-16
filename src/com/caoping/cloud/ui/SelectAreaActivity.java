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
import com.caoping.cloud.adapter.ItemAreaAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.AreaData;
import com.caoping.cloud.entiy.Area;
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
 * 选择县区
 */
public class SelectAreaActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private List<Area> provinces = new ArrayList<Area>();//省
    private ItemAreaAdapter adapter;
    private PullToRefreshListView lstv;
    private ImageView search_null;
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;

    private Resources res;
    boolean isMobileNet, isWifiNet;
    private String cityid;
    private String isType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_province_activity);
        cityid = getIntent().getExtras().getString("cityid");
        isType = getIntent().getExtras().getString("isType");
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(SelectAreaActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(SelectAreaActivity.this);
            if (!isMobileNet && !isWifiNet) {
                showMsg(SelectAreaActivity.this ,"请检查您网络链接");
            }else {
                progressDialog = new CustomProgressDialog(SelectAreaActivity.this, "正在加载中",R.anim.custom_dialog_frame);
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

        adapter = new ItemAreaAdapter(SelectAreaActivity.this ,provinces);
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        lstv.setAdapter(adapter);
        search_null = (ImageView) this.findViewById(R.id.search_null);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectAreaActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                getProvince();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SelectAreaActivity.this, System.currentTimeMillis(),
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
                    Area pro = provinces.get(position-1);
                    if(pro != null){
                        Intent intent1 = new Intent("select_area_success");
                        intent1.putExtra("areaid",  pro.getAreaid());
                        intent1.putExtra("areaName",  pro.getAreaName());
                        intent1.putExtra("isType",  isType);
                        sendBroadcast(intent1);
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
                            Toast.makeText(SelectAreaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SelectAreaActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cityid", cityid);
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
