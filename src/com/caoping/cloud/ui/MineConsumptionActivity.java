package com.caoping.cloud.ui;

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
import com.caoping.cloud.adapter.ItemConsumptionAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.LxConsumptionData;
import com.caoping.cloud.data.MinePackageData;
import com.caoping.cloud.entiy.LxConsumption;
import com.caoping.cloud.entiy.MinePackage;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 我的消费记录
 */
public class MineConsumptionActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private PullToRefreshListView lstv;
    private ItemConsumptionAdapter adapter;
    List<LxConsumption> lists = new ArrayList<LxConsumption>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private ImageView search_null;

    private TextView countJb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_consumption_activity);
        initView();
        progressDialog = new CustomProgressDialog(MineConsumptionActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        getData();
        getPackage();
    }

    private MinePackage minePackage;//我的钱包

    //获得钱包
    public void getPackage(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.APP_GET_PACKAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MinePackageData data = getGson().fromJson(s, MinePackageData.class);
                            if (data.getCode() == 200) {
                                minePackage = data.getData();
                                if(minePackage != null){
                                    initDataPackage();
                                }
                            } else {
                                Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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

    void initDataPackage(){
        countJb.setText("余额："+ (minePackage.getPackage_money()==null?"":minePackage.getPackage_money()));
    }

    private void initView() {
        countJb = (TextView) this.findViewById(R.id.countJb);

        search_null = (ImageView) this.findViewById(R.id.search_null);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("我的消费记录");
        lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemConsumptionAdapter(lists, MineConsumptionActivity.this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineConsumptionActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                getData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineConsumptionActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                getData();
            }
        });
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

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

    //获得会员消费记录
    private void getData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CONSUMPTION_RETURN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            LxConsumptionData data = getGson().fromJson(s, LxConsumptionData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists.clear();
                                }
                                lists.addAll(data.getData());
                                lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                                if(lists.size() == 0){
                                    search_null.setVisibility(View.VISIBLE);
                                    lstv.setVisibility(View.GONE);
                                }else {
                                    search_null.setVisibility(View.GONE);
                                    lstv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if(progressDialog != null){
                            progressDialog.dismiss();
                        }
                        Toast.makeText(MineConsumptionActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
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


}
