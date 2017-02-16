package com.caoping.cloud.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemWuliuCarAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.TransportData;
import com.caoping.cloud.entiy.Transport;
import com.caoping.cloud.huanxin.ui.ChatActivity;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.ui.SelectProvinceActivity;
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
 * Created by zhl on 2016/10/16.
 * 物流 找车
 */
public class WuliuTwoFragment extends BaseFragment  implements View.OnClickListener ,OnClickContentItemListener{
    private View view;
    private Resources res;

    private PullToRefreshListView lstv1;
    private ItemWuliuCarAdapter adapter1;
    List<Transport> lists1 = new ArrayList<Transport>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    private TextView start_address;
    private TextView end_address;
    private TextView searchbtn;


    boolean isMobileNet, isWifiNet;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.wuliu_two_fragment, null);
        res = getActivity().getResources();
        initView();
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
            }else {
                progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                getData1();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        return view;
    }

    private void initView() {
        search_null1 = (ImageView) view.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) view.findViewById(R.id.lstv);

        adapter1 = new ItemWuliuCarAdapter(lists1, getActivity());
        adapter1.setOnClickContentItemListener(this);
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime( getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime( getActivity(), System.currentTimeMillis(),
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
        start_address = (TextView) view.findViewById(R.id.start_address);
        end_address = (TextView) view.findViewById(R.id.end_address);
        searchbtn = (TextView) view.findViewById(R.id.searchbtn);
        start_address.setOnClickListener(this);
        end_address.setOnClickListener(this);
        searchbtn.setOnClickListener(this);
        view.findViewById(R.id.img_action).setOnClickListener(this);

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
                                Toast.makeText(getActivity(), "暂无联系方式！",Toast.LENGTH_SHORT).show();
                            }

                        }
                        break;
                        case 1:
                        {
                            //IM
                            if(!StringUtil.isNullOrEmpty(transport.getEmp_id())){
                                //IM
                                if(!transport.getEmp_id().equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                                    Intent chatV = new Intent(getActivity(), ChatActivity.class);
                                    chatV.putExtra("userId", transport.getEmp_id());
                                    chatV.putExtra("userName", transport.getEmp_name());
                                    startActivity(chatV);
                                }else{
                                    Toast.makeText(getActivity(), "不能和自己聊天！",Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(getActivity(), "暂无联系方式！",Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    }
                }
            }
        }
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
                                    Toast.makeText(getActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex1));
                params.put("is_type", "0");//0车辆信息 1货源信息
                params.put("is_use", "0");
//                if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
//                    params.put("lat", CaopingCloudApplication.latStr);
//                }
//                if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
//                    params.put("lng", CaopingCloudApplication.lngStr);
//                }
                if(!StringUtil.isNullOrEmpty(areaidStart)){
                    params.put("areaidStart", areaidStart);
                }if(!StringUtil.isNullOrEmpty(areaidEnd)){
                    params.put("areaidEnd", areaidEnd);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_address:
            {
                Intent intent = new Intent(getActivity(), SelectProvinceActivity.class);
                intent.putExtra("isType", "3");
                startActivity(intent);
            }
            break;
            case R.id.end_address:
            {
                Intent intent = new Intent(getActivity(), SelectProvinceActivity.class);
                intent.putExtra("isType", "4");
                startActivity(intent);
            }
            break;
            case R.id.searchbtn:
            {
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        getData1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case R.id.img_action:
            {
                try {
                    isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                    isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                    if (!isMobileNet && !isWifiNet) {
                        Toast.makeText(getActivity(), "请检查您网络链接", Toast.LENGTH_SHORT).show();
                    }else {
                        progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                        progressDialog.setCancelable(true);
                        progressDialog.setIndeterminate(true);
                        progressDialog.show();
                        start_address.setText("出发地");
                        end_address.setText("抵达地");
                        areaidStart = "";
                        areaidEnd = "";
                        getData1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
        }
    }
    private String areaidStart = "";
    private String areaidEnd = "";


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("select_area_success")) {
                String isType = intent.getExtras().getString("isType");
                switch (Integer.parseInt(isType)){
                    case 3:
                    {
                        areaidStart = intent.getExtras().getString("areaid");
                        String areaName = intent.getExtras().getString("areaName");
                        start_address.setText(areaName);
                    }
                    break;
                    case 4:
                    {
                        areaidEnd = intent.getExtras().getString("areaid");
                        String areaName = intent.getExtras().getString("areaName");
                        end_address.setText(areaName);
                    }
                    break;
                }

            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("select_area_success");
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


}
