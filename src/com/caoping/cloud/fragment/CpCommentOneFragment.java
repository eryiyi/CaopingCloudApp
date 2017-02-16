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
import com.caoping.cloud.adapter.ItemCommentAdapter;
import com.caoping.cloud.adapter.ItemWuliuHuoAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.GoodsCommentData;
import com.caoping.cloud.data.TransportData;
import com.caoping.cloud.entiy.GoodsComment;
import com.caoping.cloud.entiy.Transport;
import com.caoping.cloud.huanxin.ui.ChatActivity;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.ui.DetailGoodsActivity;
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
  产品评论
 */
public class CpCommentOneFragment extends BaseFragment  implements View.OnClickListener,OnClickContentItemListener {
    private View view;
    private Resources res;

    private PullToRefreshListView lstv1;
    private ItemCommentAdapter adapter1;
    List<GoodsComment> lists1 = new ArrayList<GoodsComment>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    boolean isMobileNet, isWifiNet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.comment_one_fragment, null);
        res = getActivity().getResources();
        initView();
        //判断是否有网
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
        return view;
    }

    private void initView() {
        search_null1 = (ImageView) view.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) view.findViewById(R.id.lstv);

        adapter1 = new ItemCommentAdapter(lists1, getActivity());
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getData1();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = false;
                pageIndex1++;
                getData1();
            }
        });
        lstv1.setAdapter(adapter1);
        adapter1.setOnClickContentItemListener(this);
        lstv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists1.size()> (position-1)){
                    GoodsComment comment = lists1.get(position-1);
                    if(comment != null){
//                        Intent intent = new Intent(CpCommentOneFragment.this, DetailPaopaoGoodsActivity.class);
//                        intent.putExtra("emp_id_dianpu", (comment.getGoodsEmpId()==null?"":comment.getGoodsEmpId()));
//                        intent.putExtra("goods_id", comment.getGoodsId());
//                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void getData1() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_MINE_GOODS_COMMENT_LISTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            GoodsCommentData data = getGson().fromJson(s, GoodsCommentData.class);
                            if (data.getCode() == 200) {
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
                            } else {
                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.start_address:
            {
                Intent intent = new Intent(getActivity(), SelectProvinceActivity.class);
                intent.putExtra("isType", "1");
                startActivity(intent);
            }
                break;
            case R.id.end_address:
            {
                Intent intent = new Intent(getActivity(), SelectProvinceActivity.class);
                intent.putExtra("isType", "2");
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
                        getData1();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
                break;

        }
    }


    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 0:
            {
                if(lists1.size()> (position)){
                    GoodsComment comment = lists1.get(position);
                    if(comment != null){
                        Intent intent  = new Intent(getActivity(), DetailGoodsActivity.class);
                        intent.putExtra("id", comment.getGoodsId());
                        startActivity(intent);
                    }
                }
            }
                break;
        }

    }


}
