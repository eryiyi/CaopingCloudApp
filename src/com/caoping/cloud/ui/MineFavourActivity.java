package com.caoping.cloud.ui;

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
import com.caoping.cloud.adapter.ItemFavourAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.GoodsFavourVOData;
import com.caoping.cloud.entiy.GoodsFavourVO;
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
public class MineFavourActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private TextView title;
    private Resources res;

    boolean isMobileNet, isWifiNet;
    private PullToRefreshListView lstv1;
    private ItemFavourAdapter adapter1;
    List<GoodsFavourVO> lists1 = new ArrayList<GoodsFavourVO>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_favour_activity);
        res =getResources();
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineFavourActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineFavourActivity.this);
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(MineFavourActivity.this, "请检查您网络链接", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new CustomProgressDialog(MineFavourActivity.this, "正在加载中",R.anim.custom_dialog_frame);
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
        title.setText("我的收藏");
        search_null1 = (ImageView) this.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) this.findViewById(R.id.lstv);

        adapter1 = new ItemFavourAdapter(lists1, MineFavourActivity.this);
        adapter1.setOnClickContentItemListener(this);
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineFavourActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineFavourActivity.this, System.currentTimeMillis(),
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
            }
        });
    }
    private void getData1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.MINE_FAVOUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    GoodsFavourVOData data = getGson().fromJson(s, GoodsFavourVOData.class);
                                    if (IS_REFRESH1) {
                                        lists1.clear();
                                    }
                                    lists1.addAll(data.getData());
                                    lstv1.onRefreshComplete();
                                    adapter1.notifyDataSetChanged();
                                    if(lists1.size() == 0){
                                        search_null1.setVisibility(View.VISIBLE);
                                        lstv1.setVisibility(View.GONE);
                                    }else {
                                        search_null1.setVisibility(View.GONE);
                                        lstv1.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    Toast.makeText(MineFavourActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineFavourActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex1));
                params.put("empId", getGson().fromJson(getSp().getString("empId", ""), String.class));
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

    private int tmpSelected1;//暂时存UUID  删除用

    private DeletePopWindow deleteWindow1;

    // 选择是否删除
    private void showSelectImageDialog() {
        deleteWindow1 = new DeletePopWindow(MineFavourActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow1.showAtLocation(MineFavourActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //删除商品方法
    private void delete() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_FAVOUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(MineFavourActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                                    lists1.remove(tmpSelected1);
                                    adapter1.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(MineFavourActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineFavourActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineFavourActivity.this, R.string.delete_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("favourId", good.getFavour_id());
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
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow1.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    delete();
                    break;
                default:
                    break;
            }
        }

    };


    GoodsFavourVO good;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag) {
            case 1:
                    good = lists1.get(position);
                    //删除
                    tmpSelected1 = position;
                    showSelectImageDialog();
                break;
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
