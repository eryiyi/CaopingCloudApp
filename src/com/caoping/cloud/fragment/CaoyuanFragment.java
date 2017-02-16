package com.caoping.cloud.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
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
import com.caoping.cloud.adapter.*;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CpJixieuseData;
import com.caoping.cloud.data.CpuseData;
import com.caoping.cloud.data.LxAdData;
import com.caoping.cloud.entiy.CpJixieuse;
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.entiy.Cpuse;
import com.caoping.cloud.entiy.LxAd;
import com.caoping.cloud.library.HeaderGridView;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshHeadGridView;
import com.caoping.cloud.ui.*;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/10/16.
 */
public class CaoyuanFragment extends BaseFragment  implements View.OnClickListener ,OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private View view;
    private Resources res;

    private PullToRefreshHeadGridView lstv;
    private ItemProfileCaoyuanAdapter adapter;
    List<CpObj> listMq = new ArrayList<CpObj>();
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private View headLiner;

    //轮播广告--顶部
    private ViewPager viewpager1;
    private IndexAdViewPagerAdapter adapterAd1;
    private LinearLayout viewGroup1;
    private ImageView dot1, dots1[];
    private Runnable runnable1;
    private int autoChangeTime1 = 5000;
    private List<LxAd> listsAd1 = new ArrayList<LxAd>();

    //草坪类型
    private GridView gridView1;
    private ItemCaoyuanTypeAdapter adapter1;
    private List<Cpuse> lists1 = new ArrayList<Cpuse>();

    //轮播广告--机械的
    private ViewPager viewpager2;
    private IndexAdViewPagerAdapter adapterAd2;
    private LinearLayout viewGroup2;
    private ImageView dot2, dots2[];
    private Runnable runnable2;
    private int autoChangeTime2 = 5000;
    private List<LxAd> listsAd2 = new ArrayList<LxAd>();

    //轮播广告--曹种的
    private ViewPager viewpager3;
    private IndexAdViewPagerAdapter adapterAd3;
    private LinearLayout viewGroup3;
    private ImageView dot3, dots3[];
    private Runnable runnable3;
    private int autoChangeTime3 = 5000;
    private List<LxAd> listsAd3 = new ArrayList<LxAd>();

    //机械类型
    private GridView gridView2;
    private ItemJixieTypeAdapter adapter2;
    private List<CpJixieuse> lists2 = new ArrayList<CpJixieuse>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.caoyuan_fragment, null);
        res = getActivity().getResources();
        initView();

        getAds("2");
        getAds("3");
        getAds("4");


        //获得草坪属性
        getUseType();
        //获得机械用途
        getJixieUse();

        return view;
    }
    private void initView() {
        lstv = (PullToRefreshHeadGridView) view.findViewById(R.id.lstv);
        headLiner = View.inflate(getActivity(), R.layout.caoyuan_header, null);
        headLiner.findViewById(R.id.btn_more_jixie).setOnClickListener(this);
        headLiner.findViewById(R.id.btn_more_caozhong).setOnClickListener(this);
        lstv.setMode(PullToRefreshBase.Mode.BOTH);
        lstv.setAdapter(null);
        HeaderGridView lv = lstv.getRefreshableView();
        lv.setNumColumns(1);

        lv.addHeaderView(headLiner);
        adapter = new ItemProfileCaoyuanAdapter(listMq, getActivity());
        lstv.setAdapter(adapter);
        lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<HeaderGridView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
                lstv.onRefreshComplete();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<HeaderGridView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
                lstv.onRefreshComplete();
            }
        });
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists2.size()>(position)){
                    CpObj cpObj = listMq.get(position);
                    if(cpObj != null){
                        Intent intent  = new Intent(getActivity(), DetailGoodsActivity.class);
                        intent.putExtra("id", cpObj.getCloud_caoping_id());
                        startActivity(intent);
                    }
                }
            }
        });


        gridView1 = (GridView) headLiner.findViewById(R.id.gridView1);
        gridView2 = (GridView) headLiner.findViewById(R.id.gridView2);
        adapter1 = new ItemCaoyuanTypeAdapter(lists1, getActivity());
        adapter2 = new ItemJixieTypeAdapter(lists2, getActivity());



        gridView1.setAdapter(adapter1);
        gridView2.setAdapter(adapter2);

        gridView1.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView2.setSelector(new ColorDrawable(Color.TRANSPARENT));

        gridView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ListCaoyuanActivity.class);
                intent.putExtra("tmpNearby", "1");
                startActivity(intent);
            }
        });
        gridView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(lists2.size()>i){
                    CpJixieuse cpJixieuse = lists2.get(i);
                    if(cpJixieuse != null){
                        Intent intent = new Intent(getActivity(), ListJixieActivity.class);
                        intent.putExtra("cloud_jixie_use_id", cpJixieuse.getCloud_jixie_use_id());
                        intent.putExtra("tmpNearby", "0");
                        startActivity(intent);
                    }
                }

            }
        });

        adapterAd1 = new IndexAdViewPagerAdapter(getActivity());

        adapterAd2 = new IndexAdViewPagerAdapter(getActivity());

        adapterAd3 = new IndexAdViewPagerAdapter(getActivity());


    }

    //获得属性
    private void getUseType() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CP_USE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    CpuseData data = getGson().fromJson(s, CpuseData.class);
                                    lists1.clear();
                                    lists1.addAll(data.getData());
                                    adapter1.notifyDataSetChanged();
                                }else {
                                    Toast.makeText(getActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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

    void getJixieUse(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_JIXIE_USE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    CpJixieuseData data = getGson().fromJson(s, CpJixieuseData.class);
                                    lists2.clear();
                                    lists2.addAll(data.getData());
                                    adapter2.notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
    void initData() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.GET_INDEX_TUIJIAN_LISTS,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            try {
//                                JSONObject jo = new JSONObject(s);
//                                String code = jo.getString("code");
//                                if (Integer.parseInt(code) == 200) {
//                                    PaihangObjData data = getGson().fromJson(s, PaihangObjData.class);
//                                    if (IS_REFRESH) {
//                                        listsgoods.clear();
//                                    }
//                                    listsgoods.addAll(data.getData());
////                                    if (data != null && data.getData() != null) {
////                                        for (RecordMsg recordMsg : data.getData()) {
////                                            RecordMsg recordMsgLocal = DBHelper.getInstance(getActivity()).getRecord(recordMsg.getMm_msg_id());
////                                            if (recordMsgLocal != null) {
////                                                //已经存在了 不需要插入了
////                                            } else {
////                                                DBHelper.getInstance(getActivity()).saveRecord(recordMsg);
////                                            }
////
////                                        }
////                                    }
//                                    lstv.onRefreshComplete();
//                                    adapter.notifyDataSetChanged();
//                                }else {
//                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//
////                        Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("index", String.valueOf(pageIndex));
//                params.put("is_type", "0");
//                params.put("is_del", "0");
//                return params;
//            }
//
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("Content-Type", "application/x-www-form-urlencoded");
//                return params;
//            }
//        };
//        getRequestQueue().add(request);
    }

//    -----------------------草坪轮播开始---------------------
    private void initViewPager1() {

        adapterAd1.change(listsAd1);
        adapterAd1.setOnClickContentItemListener(this);
        viewpager1 = (ViewPager) headLiner.findViewById(R.id.viewpager);
        viewpager1.setAdapter(adapterAd1);
        viewpager1.setOnPageChangeListener(myOnPageChangeListener1);
        initDot1();
        runnable1 = new Runnable() {
            @Override
            public void run() {
                int next = viewpager1.getCurrentItem() + 1;
                if (next >= adapterAd1.getCount()) {
                    next = 0;
                }
                viewHandler1.sendEmptyMessage(next);
            }
        };
        viewHandler1.postDelayed(runnable1, autoChangeTime1);
    }


    // 初始化dot视图
    private void initDot1() {
        viewGroup1 = (LinearLayout) headLiner.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots1 = new ImageView[adapterAd1.getCount()];
        for (int i = 0; i < adapterAd1.getCount(); i++) {
            dot1 = new ImageView(getActivity());
            dot1.setLayoutParams(layoutParams);
            dots1[i] = dot1;
            dots1[i].setTag(i);
            dots1[i].setOnClickListener(onClick1);

            if (i == 0) {
                dots1[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots1[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup1.addView(dots1[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener1 = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot1(arg0);
            viewHandler1.removeCallbacks(runnable1);
            viewHandler1.postDelayed(runnable1, autoChangeTime1);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick1 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView1(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView1(int position) {
        if (position < 0 || position > adapterAd1.getCount()) {
            return;
        }
        viewpager1.setCurrentItem(position);
//        if (!StringUtil.isNullOrEmpty(listsAd1.get(position).getNewsTitle())){
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
    private void setCurDot1(int position) {
        for (int i = 0; i < dots1.length; i++) {
            if (position == i) {
                dots1[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots1[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler1 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView1(msg.what);
        }

    };

//    ----------------------草坪轮播结束------------------------------

//    --------------------------机械轮播的开始----------------------------
private void initViewPager2() {
    adapterAd2.change(listsAd2);
    adapterAd2.setOnClickContentItemListener(this);
    viewpager2 = (ViewPager) headLiner.findViewById(R.id.viewpager_jx);
    viewpager2.setAdapter(adapterAd2);
    viewpager2.setOnPageChangeListener(myOnPageChangeListener2);
    initDot2();
    runnable2 = new Runnable() {
        @Override
        public void run() {
            int next = viewpager2.getCurrentItem() + 1;
            if (next >= adapterAd2.getCount()) {
                next = 0;
            }
            viewHandler2.sendEmptyMessage(next);
        }
    };
    viewHandler2.postDelayed(runnable2, autoChangeTime2);
}


    // 初始化dot视图
    private void initDot2() {
        viewGroup2 = (LinearLayout) headLiner.findViewById(R.id.viewGroup_jx);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots2 = new ImageView[adapterAd2.getCount()];
        for (int i = 0; i < adapterAd2.getCount(); i++) {
            dot2 = new ImageView(getActivity());
            dot2.setLayoutParams(layoutParams);
            dots2[i] = dot2;
            dots2[i].setTag(i);
            dots2[i].setOnClickListener(onClick2);

            if (i == 0) {
                dots2[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots2[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup2.addView(dots2[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener2 = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot2(arg0);
            viewHandler2.removeCallbacks(runnable2);
            viewHandler2.postDelayed(runnable2, autoChangeTime2);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView2(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView2(int position) {
        if (position < 0 || position > adapterAd2.getCount()) {
            return;
        }
        viewpager2.setCurrentItem(position);
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
    private void setCurDot2(int position) {
        for (int i = 0; i < dots2.length; i++) {
            if (position == i) {
                dots2[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots2[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView2(msg.what);
        }

    };
//    ----------------------机械结束轮播-----------------------------------------

//    草种开始轮播-------------------------------------
private void initViewPager3() {
    adapterAd3.change(listsAd3);
    adapterAd3.setOnClickContentItemListener(this);
    viewpager3 = (ViewPager) headLiner.findViewById(R.id.viewpager_cz);
    viewpager3.setAdapter(adapterAd3);
    viewpager3.setOnPageChangeListener(myOnPageChangeListener3);
    initDot3();
    runnable3 = new Runnable() {
        @Override
        public void run() {
            int next = viewpager3.getCurrentItem() + 1;
            if (next >= adapterAd3.getCount()) {
                next = 0;
            }
            viewHandler3.sendEmptyMessage(next);
        }
    };
    viewHandler3.postDelayed(runnable3, autoChangeTime3);
}


    // 初始化dot视图
    private void initDot3() {
        viewGroup3 = (LinearLayout) headLiner.findViewById(R.id.viewGroup_cz);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots3 = new ImageView[adapterAd3.getCount()];
        for (int i = 0; i < adapterAd3.getCount(); i++) {
            dot3 = new ImageView(getActivity());
            dot3.setLayoutParams(layoutParams);
            dots3[i] = dot3;
            dots3[i].setTag(i);
            dots3[i].setOnClickListener(onClick3);

            if (i == 0) {
                dots3[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots3[i].setBackgroundResource(R.drawable.dotn);
            }

            viewGroup3.addView(dots3[i]);
        }
    }

    ViewPager.OnPageChangeListener myOnPageChangeListener3 = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            setCurDot3(arg0);
            viewHandler3.removeCallbacks(runnable3);
            viewHandler3.postDelayed(runnable3, autoChangeTime3);
        }

    };
    // 实现dot点击响应功能,通过点击事件更换页面
    View.OnClickListener onClick3 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int position = (Integer) v.getTag();
            setCurView3(position);
        }

    };

    /**
     * 设置当前的引导页
     */
    private void setCurView3(int position) {
        if (position < 0 || position > adapterAd3.getCount()) {
            return;
        }
        viewpager3.setCurrentItem(position);
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
    private void setCurDot3(int position) {
        for (int i = 0; i < dots3.length; i++) {
            if (position == i) {
                dots3[i].setBackgroundResource(R.drawable.dotc);
            } else {
                dots3[i].setBackgroundResource(R.drawable.dotn);
            }
        }
    }

    /**
     * 每隔固定时间切换广告栏图片
     */
    @SuppressLint("HandlerLeak")
    private final Handler viewHandler3 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            setCurView3(msg.what);
        }

    };

//    ---------------------------草种轮播结束------------------------------

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        switch (flag){
            case 0:
                LxAd lxAd= (LxAd) object;
                if(lxAd != null){
                    if("1".equals(lxAd.getAd_url_type())){
                        if(!StringUtil.isNullOrEmpty(lxAd.getAd_msg_id())){
                            Intent intent  = new Intent(getActivity(), DetailGoodsActivity.class);
                            intent.putExtra("id", lxAd.getAd_msg_id());
                            startActivity(intent);
                        }
                    }else {
                        if(!StringUtil.isNullOrEmpty(lxAd.getAd_emp_id())){
                            Intent intent  = new Intent(getActivity(), ProfileActivity.class);
                            intent.putExtra("emp_id", lxAd.getAd_emp_id());
                            startActivity(intent);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_more_jixie:
            {
                //点击机械更多
                Intent intent = new Intent(getActivity(), ListJixieActivity.class);
                intent.putExtra("cloud_jixie_use_id", "");
                intent.putExtra("tmpNearby", "0");
                startActivity(intent);
            }
                break;
            case R.id.btn_more_caozhong:
            {
                Intent intent = new Intent(getActivity(), ListCaozhongActivity.class);
                intent.putExtra("tmpNearby", "0");
                startActivity(intent);
            }
                break;
        }
    }

    void getAds(final String ad_type) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_AD_LIST_TYPE_LISTS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    LxAdData data = getGson().fromJson(s, LxAdData.class);
                                    switch (Integer.parseInt(ad_type)){
                                        case 2:
                                        {
                                            listsAd1.clear();
                                            listsAd1.addAll(data.getData());
                                            adapterAd1.notifyDataSetChanged();
                                            initViewPager1();
                                        }
                                            break;
                                        case 3:
                                        {
                                            listsAd2.clear();
                                            listsAd2.addAll(data.getData());
                                            adapterAd2.notifyDataSetChanged();
                                            initViewPager2();
                                        }
                                        break;
                                        case 4:
                                        {
                                            listsAd3.clear();
                                            listsAd3.addAll(data.getData());
                                            adapterAd3.notifyDataSetChanged();
                                            initViewPager3();
                                        }
                                        break;
                                    }

                                }else {
                                    Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ad_type", ad_type);
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
