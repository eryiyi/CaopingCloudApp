package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemMineProductAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CpObjData;
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.DeletePopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 我的发布
 */
public class ListMineProductActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private TextView title;
    private PullToRefreshListView lstv2;
    private ItemMineProductAdapter adapter2;
    List<CpObj> lists2 = new ArrayList<CpObj>();
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;

    private TextView btn_all;
    private TextView btn_nearby;
    private TextView btn_paixu;
    private String is_type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mine_product_activity);
        is_type = getIntent().getExtras().getString("is_type");
        initView();
        switch (Integer.parseInt(is_type)){
            case 0:
            {
                btn_all.setTextColor(getResources().getColor(R.color.red));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;
            case 1:
            {
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.red));
            }
            break;
            case 2:
            {
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.red));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
            }
            break;

        }

        initData();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("发布记录");

        search_null2 = (ImageView) this.findViewById(R.id.search_null);
        lstv2 = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter2 = new ItemMineProductAdapter(lists2, ListMineProductActivity.this);
        lstv2.setMode(PullToRefreshBase.Mode.BOTH);
        lstv2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMineProductActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMineProductActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = false;
                pageIndex2++;
                initData();
            }
        });
        lstv2.setAdapter(adapter2);
        adapter2.setOnClickContentItemListener(this);
        lstv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists2.size()>(position-1)){
                    CpObj cpObj = lists2.get(position-1);
                    if(cpObj != null){
                        Intent intent  = new Intent(ListMineProductActivity.this, DetailGoodsActivity.class);
                        intent.putExtra("id", cpObj.getCloud_caoping_id());
                        startActivity(intent);
                    }
                }
            }
        });

        btn_all = (TextView) this.findViewById(R.id.btn_all);
        btn_nearby = (TextView) this.findViewById(R.id.btn_nearby);
        btn_paixu = (TextView) this.findViewById(R.id.btn_paixu);
        btn_all.setOnClickListener(this);
        btn_nearby.setOnClickListener(this);
        btn_paixu.setOnClickListener(this);
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

//    0草坪 1草种 2机械
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
                is_type = "0";
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }
            break;
            case R.id.btn_nearby:
            {
                //附近点击
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.text_color));
                btn_nearby.setTextColor(getResources().getColor(R.color.red));
                is_type = "1";
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }
            break;
            case R.id.btn_paixu:
            {
                //最新排序
                btn_all.setTextColor(getResources().getColor(R.color.text_color));
                btn_paixu.setTextColor(getResources().getColor(R.color.red));
                btn_nearby.setTextColor(getResources().getColor(R.color.text_color));
                is_type = "2";
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }
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
                                Toast.makeText(ListMineProductActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ListMineProductActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ListMineProductActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex2));
                params.put("cloud_is_use", "0");
                params.put("cloud_is_del", "0");
                params.put("is_type", is_type);
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

    private DeletePopWindow deleteWindow;
    CpObj newsObj;
    int tmpSelect = 0;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        if(lists2.size() >(position)){
            newsObj = lists2.get(position);
        }
        if(newsObj != null){
            switch (flag){
                case 0:
                {
                    //删除
                    tmpSelect = position;
                    showDeleteDialog();
                }
                break;
            }
        }
    }


    void showDeleteDialog(){
        deleteWindow = new DeletePopWindow(ListMineProductActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure: {
                    deleteById();
                }
                break;
                case R.id.btn_cancle:
                {
                    //
                }
                    break;
                default:
                    break;
            }
        }
    };


    private void deleteById() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.appDeleteProductById,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {

                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    showMsg(ListMineProductActivity.this, "删除成功！");
                                    lists2.remove(tmpSelect);
                                    adapter2.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(ListMineProductActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(ListMineProductActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cloud_caoping_id", newsObj.getCloud_caoping_id());
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
