package com.caoping.cloud.ui;

import android.content.Intent;
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
import com.caoping.cloud.adapter.ItemProfileToutiaoAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.NewsObjData;
import com.caoping.cloud.entiy.NewsObj;
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
 * 我的头条
 */
public class ListMineNewsActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {
    private TextView title;
    private TextView right_btn;
    private PullToRefreshListView lstv2;
    private ItemProfileToutiaoAdapter adapter2;
    List<NewsObj> lists2 = new ArrayList<NewsObj>();
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;

    boolean flagT = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_mine_news_activity);
        initView();
        initData();
    }

    private void initView() {

        this.findViewById(R.id.back).setOnClickListener(this);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setVisibility(View.VISIBLE);
        right_btn.setText("发布");
        right_btn.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("我的头条");

        search_null2 = (ImageView) this.findViewById(R.id.search_null);
        lstv2 = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter2 = new ItemProfileToutiaoAdapter(lists2, ListMineNewsActivity.this, flagT);
        lstv2.setMode(PullToRefreshBase.Mode.BOTH);
        lstv2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMineNewsActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ListMineNewsActivity.this, System.currentTimeMillis(),
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
                    NewsObj newsObj = lists2.get(position-1);
                    if(newsObj != null){
                        Intent intent = new Intent(ListMineNewsActivity.this, DetailNewsActivity.class);
                        intent.putExtra("mm_msg_id", newsObj.getMm_msg_id());
                        startActivity(intent);
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
            case R.id.right_btn:
            {
                //
                Intent intent = new Intent(ListMineNewsActivity.this, PublishRecordActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    //我的头条
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {

                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    NewsObjData data = getGson().fromJson(s, NewsObjData.class);
                                    if (IS_REFRESH2) {
                                        lists2.clear();
                                    }
                                    lists2.addAll(data.getData());
                                    lstv2.onRefreshComplete();
                                    adapter2.notifyDataSetChanged();
                                    //处理数据，需要的话保存到数据库
//                                    if (data != null && data.getData() != null) {
//                                        DBHelper dbHelper = DBHelper.getInstance(getActivity());
//                                        for (Videos videos : data.getData()) {
//                                            if (dbHelper.getVideosById(videos.getId()) == null) {
//                                                DBHelper.getInstance(getActivity()).saveVideos(videos);
//                                            }
//                                        }
//                                    }
                                    if(lists2.size() != 0){
                                        lstv2.setVisibility(View.VISIBLE);
                                        search_null2.setVisibility(View.GONE);
                                    }else {
                                        lstv2.setVisibility(View.GONE);
                                        search_null2.setVisibility(View.VISIBLE);
                                    }
                                }else{
                                    Toast.makeText(ListMineNewsActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(ListMineNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                params.put("page", String.valueOf(pageIndex2));
                params.put("is_del", "0");
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
    NewsObj newsObj;
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
        deleteWindow = new DeletePopWindow(ListMineNewsActivity.this, itemsOnClick);
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
                default:
                    break;
            }
        }
    };

    private void deleteById() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DELETE_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {

                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                   showMsg(ListMineNewsActivity.this, "删除成功！");
                                    lists2.remove(tmpSelect);
                                    adapter2.notifyDataSetChanged();
                                }else{
                                    Toast.makeText(ListMineNewsActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(ListMineNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                params.put("mm_msg_id", newsObj.getMm_msg_id());
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
