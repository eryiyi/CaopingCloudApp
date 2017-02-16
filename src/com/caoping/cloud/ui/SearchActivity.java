package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemProfileCaoyuanAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CpObjData;
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * chaxun
 */
public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private EditText keywords;

    private PullToRefreshListView lstv2;
    private ItemProfileCaoyuanAdapter adapter2;
    List<CpObj> lists2 = new ArrayList<CpObj>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private ImageView search_null;

    private String cont = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        cont = getIntent().getExtras().getString("keywords");
        initView();
        if(!StringUtil.isNullOrEmpty(cont)){
            keywords.setText(cont);
        }
        initData();
    }

    private void initView() {
        keywords = (EditText) this.findViewById(R.id.keywords);
        keywords.addTextChangedListener(watcher);

        this.findViewById(R.id.back).setOnClickListener(this);

        search_null = (ImageView) this.findViewById(R.id.search_null);
        lstv2 = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter2 = new ItemProfileCaoyuanAdapter(lists2, SearchActivity.this);
        lstv2.setMode(PullToRefreshBase.Mode.BOTH);
        lstv2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SearchActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(SearchActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        lstv2.setAdapter(adapter2);
        lstv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists2.size()>(position-1)){
                    CpObj cpObj = lists2.get(position-1);
                    if(cpObj != null){
                        Intent intent  = new Intent(SearchActivity.this, DetailGoodsActivity.class);
                        intent.putExtra("id", cpObj.getCloud_caoping_id());
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            initData();
        }
    };


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }


    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CP_LIST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            CpObjData data = getGson().fromJson(s, CpObjData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    lists2.clear();
                                }
                                lists2.addAll(data.getData());
                                lstv2.onRefreshComplete();
                                adapter2.notifyDataSetChanged();
                                if(lists2.size() == 0){
                                    search_null.setVisibility(View.VISIBLE);
                                    lstv2.setVisibility(View.GONE);
                                }else {
                                    search_null.setVisibility(View.GONE);
                                    lstv2.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SearchActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("cloud_is_use", "0");
                params.put("cloud_is_del", "0");
                params.put("cloud_caoping_type_id", "");
                params.put("cloud_caoping_guige_id", "");
                params.put("cloud_caoping_use_id", "");
                params.put("is_type", "");
                if(!StringUtil.isNullOrEmpty(keywords.getText().toString())){
                    params.put("keyWords", keywords.getText().toString());
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
