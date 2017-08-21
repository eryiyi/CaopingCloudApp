package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
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
import com.caoping.cloud.adapter.ItemWuliuHuoAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.TransportData;
import com.caoping.cloud.entiy.Transport;
import com.caoping.cloud.huanxin.ui.ChatActivity;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 */
public class MineWuliuActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener {
    private TextView title;
    private TextView right_btn;
    private String titleStr = "";
    private String is_type = "1";
    private Resources res;

    boolean isMobileNet, isWifiNet;
    private PullToRefreshListView lstv1;
    private ItemWuliuHuoAdapter adapter1;
    List<Transport> lists1 = new ArrayList<Transport>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_wuliu_activity);
        res =getResources();
        titleStr = getIntent().getExtras().getString("titleStr");
        is_type = getIntent().getExtras().getString("is_type");
        initView();
        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(MineWuliuActivity.this);
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(MineWuliuActivity.this);
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(MineWuliuActivity.this, "请检查您网络链接", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new CustomProgressDialog(MineWuliuActivity.this, "正在加载中",R.anim.custom_dialog_frame);
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
        title = (TextView) this.findViewById(R.id.title);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setOnClickListener(this);
        right_btn.setText("发布");
        if(!StringUtil.isNullOrEmpty(titleStr)){
            title.setText(titleStr);
        }
        search_null1 = (ImageView) this.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) this.findViewById(R.id.lstv);

        adapter1 = new ItemWuliuHuoAdapter(lists1, MineWuliuActivity.this);
        adapter1.setOnClickContentItemListener(this);
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineWuliuActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineWuliuActivity.this, System.currentTimeMillis(),
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
                InternetURL.GET_TRANSPORT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    TransportData data = getGson().fromJson(s, TransportData.class);
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
                                    Toast.makeText(MineWuliuActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineWuliuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineWuliuActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex1));
                params.put("is_type", is_type);//0车辆信息 1货源信息
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                params.put("is_use", "");
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
    public void onClickContentItem(int position, int flag, Object object) {
        String str = (String) object;
        if("1000".equals(str)){
            if(lists1.size()>position){
                Transport transport = lists1.get(position);
                if(transport != null){
                    switch (flag){
                        case 0:
                        {
                            //电话
                            if(!StringUtil.isNullOrEmpty( transport.getTel())){
                                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + transport.getTel()));
                                startActivity(intent);
                            }else{
                                Toast.makeText(MineWuliuActivity.this, "暂无联系方式！",Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                        case 1:
                        {
                            //IM
                            if(!StringUtil.isNullOrEmpty(transport.getEmp_id()) && !StringUtil.isNullOrEmpty(transport.getTel())){
                                Intent intent = new Intent(Intent.ACTION_DIAL);
                                Uri data = Uri.parse("tel:" + transport.getTel());
                                intent.setData(data);
                                startActivity(intent);
                                //IM
//                                if(!transport.getEmp_id().equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
//                                    Intent chatV = new Intent(MineWuliuActivity.this, ChatActivity.class);
//                                    chatV.putExtra("userId", transport.getEmp_id());
//                                    chatV.putExtra("userName", transport.getEmp_name());
//                                    startActivity(chatV);
//                                }else{
//                                    Toast.makeText(MineWuliuActivity.this, "不能和自己聊天！",Toast.LENGTH_SHORT).show();
//                                }

                            }else{
                                Toast.makeText(MineWuliuActivity.this, "暂无联系方式！",Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.right_btn:
            {
                if(is_type.equals("0")){
                    //车主
                    Intent intent = new Intent(MineWuliuActivity.this, PublishCarActivity.class);
                    startActivity(intent);
                }else{
                    //货主
                    Intent intent = new Intent(MineWuliuActivity.this, PublishCarGoodsActivity.class);
                    startActivity(intent);
                }
            }
                break;
        }
    }
}
