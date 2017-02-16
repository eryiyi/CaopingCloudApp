package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import com.caoping.cloud.adapter.ItemFavourDianpuAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.DianPuFavourData;
import com.caoping.cloud.entiy.DianPuFavour;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.DeletePopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 */
public class MineGuanzhuActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private TextView title;
    private Resources res;

    boolean isMobileNet, isWifiNet;
    private PullToRefreshListView lstv1;
    private ItemFavourDianpuAdapter adapter1;
    List<DianPuFavour> lists1 = new ArrayList<DianPuFavour>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    private int tmpSelected2;//暂时存UUID  删除用

    private DeletePopWindow deleteWindow2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_favour_activity);
        res =getResources();
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineGuanzhuActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineGuanzhuActivity.this);
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(MineGuanzhuActivity.this, "请检查您网络链接", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new CustomProgressDialog(MineGuanzhuActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData1();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("我的关注");
        search_null1 = (ImageView) this.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) this.findViewById(R.id.lstv);

        adapter1 = new ItemFavourDianpuAdapter(lists1, MineGuanzhuActivity.this);
        adapter1.setOnClickContentItemListener(this);
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineGuanzhuActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineGuanzhuActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = false;
                pageIndex1++;
                getData1();
            }
        });
        lstv1.setAdapter(adapter1);
        lstv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists1 != null){
                    if(lists1.size() > (position-1)){
                        DianPuFavour dianPuFavour = lists1.get(position-1);
                        if(dianPuFavour != null){
                            if(!StringUtil.isNullOrEmpty(dianPuFavour.getEmp_id())){
                                Intent intent  = new Intent(MineGuanzhuActivity.this, ProfileActivity.class);
                                intent.putExtra("emp_id", dianPuFavour.getEmp_id());
                                startActivity(intent);
                            }
                        }
                    }
                }
            }
        });
    }

    //获得我的店铺收藏列表
    private void getData1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.APP_GET_FAVOUR_DIANPU_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            DianPuFavourData data = getGson().fromJson(s, DianPuFavourData.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH1) {
                                    lists1.clear();
                                }
                                lists1.addAll(data.getData());
                                lstv1.onRefreshComplete();
                                if (lists1.size() == 0) {
                                    search_null1.setVisibility(View.VISIBLE);
                                } else {
                                    search_null1.setVisibility(View.GONE);
                                }
                                adapter1.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineGuanzhuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineGuanzhuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineGuanzhuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex1));
                params.put("emp_id_favour", getGson().fromJson(getSp().getString("empId", ""), String.class));
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

    DianPuFavour good2;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                good2 = lists1.get(position);
                //删除
                tmpSelected2 = position;
                showSelectImageDialog2();
                break;
        }
    }

    // 选择是否删除
    private void showSelectImageDialog2() {
        deleteWindow2 = new DeletePopWindow(MineGuanzhuActivity.this, itemsOnClick2);
        //显示窗口
        deleteWindow2.showAtLocation(MineGuanzhuActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void deleteDp() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_DIANPU_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(MineGuanzhuActivity.this, "取消关注成功", Toast.LENGTH_SHORT).show();
                                    lists1.remove(tmpSelected2);
                                    adapter1.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(MineGuanzhuActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineGuanzhuActivity.this, "取消收藏失败", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineGuanzhuActivity.this,"取消关注失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("favour_no", good2.getFavour_no());
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

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick2 = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow2.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    deleteDp();
                    break;
                default:
                    break;
            }
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
}
