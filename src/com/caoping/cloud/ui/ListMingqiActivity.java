package com.caoping.cloud.ui;

import android.app.Dialog;
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
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemMingqiAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CompanyData;
import com.caoping.cloud.entiy.Company;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 名企排行
 */
public class ListMingqiActivity extends BaseActivity implements View.OnClickListener {
    private EditText keywords;

    private PullToRefreshListView lstv2;
    private ItemMingqiAdapter adapter2;
    List<Company> lists2 = new ArrayList<Company>();
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;

    private TextView btn_all;
    private TextView btn_nearby;
    private TextView btn_paixu;
    private TextView btn_val;
    private String tmpNearby = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mingqi_activity);
        initView();
        initData();
    }

    private void initView() {
        keywords = (EditText) this.findViewById(R.id.keywords);
        keywords.addTextChangedListener(watcher);

        this.findViewById(R.id.back).setOnClickListener(this);


        search_null2 = (ImageView) this.findViewById(R.id.search_null);
        lstv2 = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter2 = new ItemMingqiAdapter(lists2, ListMingqiActivity.this);
        lstv2.setMode(PullToRefreshBase.Mode.BOTH);
        lstv2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMingqiActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMingqiActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = false;
                pageIndex2++;
                initData();
            }
        });
        lstv2.setAdapter(adapter2);
        lstv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists2.size() > (position-1)){
                    Company company= lists2.get(position-1);
                    if(company != null){
                        Intent intent  = new Intent(ListMingqiActivity.this, ProfileActivity.class);
                        intent.putExtra("emp_id", company.getEmp_id());
                        startActivity(intent);
                    }
                }

            }
        });

        btn_all = (TextView) this.findViewById(R.id.btn_all);
        btn_nearby = (TextView) this.findViewById(R.id.btn_nearby);
        btn_paixu = (TextView) this.findViewById(R.id.btn_paixu);
        btn_val = (TextView) this.findViewById(R.id.btn_val);
        btn_all.setOnClickListener(this);
        btn_nearby.setOnClickListener(this);
        btn_paixu.setOnClickListener(this);
        btn_val.setOnClickListener(this);
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
            case R.id.btn_all:
            {
                //全部点击
                btn_all.setTextColor(getResources().getColor(R.color.red));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
                btn_val.setTextColor(getResources().getColor(R.color.text_color));
                tmpNearby = "0";
//                showGoodsType();
            }
            break;
            case R.id.btn_nearby:
            {
                //附近点击
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.red));
                btn_val.setTextColor(getResources().getColor(R.color.text_color));
                tmpNearby = "1";
                initData();
            }
            break;
            case R.id.btn_paixu:
            {
                //最新排序
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.red));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
                btn_val.setTextColor(getResources().getColor(R.color.text_color));
                tmpNearby = "2";
                initData();
            }
            break;
            case R.id.btn_val:
            {
                //销量
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
                btn_val.setTextColor(getResources().getColor(R.color.red));
                tmpNearby = "3";
                initData();
            }
            break;
        }
    }

    private void showGoodsType() {
        final Dialog picAddDialog = new Dialog(ListMingqiActivity.this, R.style.dialog);
        View picAddInflate = View.inflate(this, R.layout.select_type_dialog, null);
        ListView listView = (ListView) picAddInflate.findViewById(R.id.lstv);
        TextView title_msg = (TextView) picAddInflate.findViewById(R.id.title_msg);
        title_msg.setText("请选择分类");
//        GoodsTypeAdapter adapter = new GoodsTypeAdapter(TuijianFragment.listGoodsType, SearchGoodsByTypeActivity.this);
//        listView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                GoodsType goodsType = TuijianFragment.listGoodsType.get(i);
//                btn_all.setText(goodsType.getTypeName());
//                typeId = goodsType.getTypeId();
//                IS_REFRESH = true;
//                pageIndex = 1;
//                initData();
//                picAddDialog.dismiss();
//            }
//        });
        picAddDialog.setContentView(picAddInflate);
        picAddDialog.show();
    }

    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COMPANY_MQ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            CompanyData data = getGson().fromJson(s, CompanyData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH2) {
                                    lists2.clear();
                                }
                                lists2.addAll(data.getData());
                                lstv2.onRefreshComplete();
                                adapter2.notifyDataSetChanged();
                                if(lists2.size() == 0){
                                    search_null2.setVisibility(View.VISIBLE);
                                    lstv2.setVisibility(View.GONE);
                                }else {
                                    search_null2.setVisibility(View.GONE);
                                    lstv2.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(ListMingqiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ListMingqiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ListMingqiActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex2));
                params.put("provinceid", "");
                params.put("cityid", "");
                params.put("areaid", "");
                if(!StringUtil.isNullOrEmpty(keywords.getText().toString())){
                    params.put("keyWords", keywords.getText().toString());
                }
                switch (Integer.parseInt(tmpNearby)){
                    case 0:
                    {
                        //全部点击

                    }
                    break;
                    case 1:
                    {
                        //附近
                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                            params.put("lat", String.valueOf(CaopingCloudApplication.latStr));
                        }
                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                            params.put("lng", String.valueOf(CaopingCloudApplication.lngStr));
                        }
                    }
                    break;
                    case 2:
                    {
                        //最新
                        params.put("is_time", "1");
                    }
                    break;
                    case 3:
                    {
                        //销量
                        params.put("is_count", "1");
                    }
                    break;
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
