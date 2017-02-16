package com.caoping.cloud.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.DetailCommentAdapter;
import com.caoping.cloud.adapter.DetailNewsViewPagerAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CommentData;
import com.caoping.cloud.data.NewsObjSingleData;
import com.caoping.cloud.entiy.Comment;
import com.caoping.cloud.entiy.NewsObj;
import com.caoping.cloud.util.Constants;
import com.caoping.cloud.util.HttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.ContentListView;
import com.caoping.cloud.widget.DeletePopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 头条详情
 */
public class DetailNewsActivity extends BaseActivity implements View.OnClickListener ,OnClickContentItemListener ,ContentListView.OnRefreshListener,
        ContentListView.OnLoadListener{
    private String mm_msg_id;//信息ID
    private TextView title;

    private TextView newsTitle;
    private TextView company;
    private TextView name;
    private TextView btn_favour;
    private TextView btn_comment;
    private TextView content;
    private TextView dateline;
    private TextView location;

    LinearLayout headLiner;
    private ViewPager viewpager;
    private DetailNewsViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<String> listsAd = new ArrayList<String>();
    private RelativeLayout ad;

    private ContentListView detail_lstv;
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;
    private DetailCommentAdapter adapterComment;
    List<Comment> commentContents = new ArrayList<Comment>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
        setContentView(R.layout.detail_news_activity);
        mm_msg_id = getIntent().getExtras().getString("mm_msg_id");
        initView();

        //获取信息详情
        getDetailById();
        //信息评论
        loadData(ContentListView.LOAD);
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("头条详情");

        headLiner = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.detail_news_header, null);
        this.findViewById(R.id.detail_like_liner).setOnClickListener(this);
        this.findViewById(R.id.detail_comment_liner).setOnClickListener(this);

        ad = (RelativeLayout) headLiner.findViewById(R.id.ad);
        dateline = (TextView) headLiner.findViewById(R.id.dateline);
        newsTitle = (TextView) headLiner.findViewById(R.id.newsTitle);
        company = (TextView) headLiner.findViewById(R.id.company);
        name = (TextView) headLiner.findViewById(R.id.name);
        btn_favour = (TextView) headLiner.findViewById(R.id.btn_favour);
        btn_comment = (TextView) headLiner.findViewById(R.id.btn_comment);
        location = (TextView) headLiner.findViewById(R.id.location);
        content = (TextView) headLiner.findViewById(R.id.content);
        detail_lstv = (ContentListView) this.findViewById(R.id.lstv);
        adapterComment = new DetailCommentAdapter(this, commentContents);
        adapterComment.setOnClickContentItemListener(this);
        detail_lstv.setAdapter(adapterComment);
        detail_lstv.addHeaderView(headLiner);
        detail_lstv.setOnRefreshListener(this);
        detail_lstv.setOnLoadListener(this);
        adapterComment.setOnClickContentItemListener(this);

        detail_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(commentContents.size() > (position-2)){
                    Comment comment = commentContents.get(position-2);
                    if (comment != null) {
                        Intent intent = new Intent(DetailNewsActivity.this, AddNewsCommentActivity.class);
                        intent.putExtra("mm_msg_id", mm_msg_id);
                        intent.putExtra("comment_fplid", comment.getComment_id());
                        intent.putExtra("comment_fplid_name", comment.getEmp_name());
                        startActivity(intent);
                    }
                }
            }
        });

    }

    private void initViewPager() {
        adapterAd = new DetailNewsViewPagerAdapter(DetailNewsActivity.this);
        adapterAd.change(listsAd);
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) this.findViewById(R.id.viewpager);
        viewpager.setAdapter(adapterAd);
        viewpager.setOnPageChangeListener(myOnPageChangeListener);
        initDot();
        runnable = new Runnable() {
            @Override
            public void run() {
                int next = viewpager.getCurrentItem() + 1;
                if (next >= adapterAd.getCount()) {
                    next = 0;
                }
                viewHandler.sendEmptyMessage(next);
            }
        };
        viewHandler.postDelayed(runnable, autoChangeTime);
    }


    // 初始化dot视图
    private void initDot() {
        viewGroup = (LinearLayout) headLiner.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {
            dot = new ImageView(DetailNewsActivity.this);
            dot.setLayoutParams(layoutParams);
            dots[i] = dot;
            dots[i].setTag(i);
            dots[i].setOnClickListener(onClick);

            if (i == 0) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }
            viewGroup.addView(dots[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot(arg0);
            viewHandler.removeCallbacks(runnable);
            viewHandler.postDelayed(runnable, autoChangeTime);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView(int position) {
        if (position < 0 || position > adapterAd.getCount()) {
            return;
        }
        viewpager.setCurrentItem(position);
//        if (!StringUtil.isNullOrEmpty(lists.get(position).getNewsTitle())){
//            titleSlide = lists.get(position).getNewsTitle();
//            if(titleSlide.length() > 13){
//                titleSlide = titleSlide.substring(0,12);
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }else{
//                article_title.setText(titleSlide);//当前新闻标题显示
//            }
//        }

    }

    /**
     * 选中当前引导小点
     */
    private void setCurDot(int position) {
        for (int i = 0; i < dots.length; i++) {
            if (position == i) {
                dots[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView(msg.what);
        }

    };

    @Override
    public void onClickContentItem(int position, int flag, Object object) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.detail_like_liner:
            {
                //赞
                favourAdd();
            }
                break;
            case R.id.detail_comment_liner:
            {
                //评论
                Intent intent = new Intent(DetailNewsActivity.this, AddNewsCommentActivity.class);
                intent.putExtra("mm_msg_id", mm_msg_id);
                intent.putExtra("comment_fplid", "");
                intent.putExtra("comment_fplid_name", "");
                startActivity(intent);
            }
            break;
        }
    }

    private void loadData(final int currentid) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_NEWS_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        detail_lstv.onRefreshComplete();
                        detail_lstv.onLoadComplete();
                        if (StringUtil.isJson(s)) {
                            CommentData data = getGson().fromJson(s, CommentData.class);
                            if (data.getCode() == 200) {
                                if (ContentListView.REFRESH == currentid) {
                                    commentContents.clear();
                                    commentContents.addAll(data.getData());
                                    detail_lstv.setResultSize(data.getData().size());
                                    adapterComment.notifyDataSetChanged();
                                }
                                if (ContentListView.LOAD == currentid) {
                                    commentContents.addAll(data.getData());
                                    detail_lstv.setResultSize(data.getData().size());
                                    adapterComment.notifyDataSetChanged();
                                }
                                detail_lstv.setVisibility(View.VISIBLE);
                                adapterComment.notifyDataSetChanged();
                            } else {
                                Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        detail_lstv.onRefreshComplete();
                        detail_lstv.onLoadComplete();
                        Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("mm_msg_id", mm_msg_id);
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

    private NewsObj newsObj;

    private void getDetailById() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.DETAIL_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    NewsObjSingleData data = getGson().fromJson(s, NewsObjSingleData.class);
                                    newsObj = data.getData();
                                    initHeader();
                                }else{
                                    showMsg(DetailNewsActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_id", mm_msg_id);
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



    /**
     * 加载数据监听实现
     */
    @Override
    public void onLoad() {
        pageIndex++;
        loadData(ContentListView.LOAD);
    }

    /**
     * 刷新数据监听实现
     */
    @Override
    public void onRefresh() {
        pageIndex = 1;
        loadData(ContentListView.REFRESH);
    }


    void initHeader(){
        //头部实现
        if(newsObj != null){
            newsTitle.setText(newsObj.getMm_msg_title()==null?"":newsObj.getMm_msg_title());
            company.setText(newsObj.getCompanyName()==null?"":newsObj.getCompanyName());
            name.setText((newsObj.getEmpName()==null?"":newsObj.getEmpName()));
            dateline.setText("  "+newsObj.getDateline()==null?"":newsObj.getDateline());
            btn_favour.setText(newsObj.getFavourNum()==null?"0":newsObj.getFavourNum());
            btn_comment.setText(newsObj.getCommentNum()==null?"0":newsObj.getCommentNum());
            content.setText(newsObj.getMm_msg_content()==null?"":newsObj.getMm_msg_content());
            if(!StringUtil.isNullOrEmpty(newsObj.getMm_msg_picurl())){
                String[] arras = newsObj.getMm_msg_picurl().split(",");
                if(arras != null){
                    for(String st:arras){
                        listsAd.add(st);
                    }
                }
                initViewPager();
                ad.setVisibility(View.VISIBLE);
            }else {
                ad.setVisibility(View.GONE);
            }
            if(!StringUtil.isNullOrEmpty(newsObj.getLocation())){
                location.setText(newsObj.getLocation());
                location.setVisibility(View.VISIBLE);
            }else {
                location.setText("");
                location.setVisibility(View.GONE);
            }
        }
    }


    private void favourAdd() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_NEWS_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    showMsg(DetailNewsActivity.this, "点赞成功！");
                                    btn_favour.setText(String.valueOf(Integer.parseInt(newsObj.getFavourNum())+1));
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent("list_news_success");
                                    sendBroadcast(intent1);
                                }else{
                                    showMsg(DetailNewsActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailNewsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_id", mm_msg_id);
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

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(Constants.SEND_COMMENT_SUCCESS);//评论成功，刷新评论列表
        myIntentFilter.addAction(Constants.SEND_FAVOUR_SUCCESS);//点赞成功，刷新赞列表
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    private DeletePopWindow deleteWindow;
    boolean isMobileNet, isWifiNet;

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Constants.SEND_COMMENT_SUCCESS)) {
                //刷新内容
                pageIndex = 1;
                //判断是否有网
                try {
                    isMobileNet = HttpUtils.isMobileDataEnable(DetailNewsActivity.this);
                    isWifiNet = HttpUtils.isWifiDataEnable(DetailNewsActivity.this);
                    if (!isMobileNet && !isWifiNet) {
                    }else {
                        loadData(ContentListView.REFRESH);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    };


}
