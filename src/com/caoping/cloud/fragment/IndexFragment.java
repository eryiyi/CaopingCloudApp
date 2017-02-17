package com.caoping.cloud.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.adapter.IndexAdViewPagerAdapter;
import com.caoping.cloud.adapter.ItemIndexGoodsAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CompanyData;
import com.caoping.cloud.data.LxAdData;
import com.caoping.cloud.data.NewsObjData;
import com.caoping.cloud.entiy.Company;
import com.caoping.cloud.entiy.LxAd;
import com.caoping.cloud.entiy.NewsObj;
import com.caoping.cloud.library.HeaderGridView;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshHeadGridView;
import com.caoping.cloud.ui.*;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.AutoTextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/7/1.
 * 推荐
 */
public class IndexFragment extends BaseFragment implements View.OnClickListener,OnClickContentItemListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private View view;
    private Resources res;

    private EditText keywords;
//    private ImageView msg;

//    private PullToRefreshHeadGridView lstv;
//    private ItemIndexGoodsAdapter adapter;
//    List<Company> listMq = new ArrayList<Company>();
//    private int pageIndex = 1;
//    private static boolean IS_REFRESH = true;

//    private View headLiner;
    //轮播广告
    private ViewPager viewpager;
    private IndexAdViewPagerAdapter adapterAd;
    private LinearLayout viewGroup;
    private ImageView dot, dots[];
    private Runnable runnable;
    private int autoChangeTime = 5000;
    private List<LxAd> listsAd = new ArrayList<LxAd>();

    private AutoTextView mTextView02;

    private TextView txt_news_one;
    private TextView txt_news_two;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.index_fragment, null);
        res = getActivity().getResources();
        initView();
        getAds();
//        getNewsCompanys();
        getNews();
        return view;
    }


    private void initView() {
        keywords = (EditText) view.findViewById(R.id.keywords);
        keywords.addTextChangedListener(watcher);
        txt_news_one = (TextView) view.findViewById(R.id.txt_news_one);
        txt_news_two = (TextView) view.findViewById(R.id.txt_news_two);
        view.findViewById(R.id.btn_one).setOnClickListener(this);
        view.findViewById(R.id.btn_two).setOnClickListener(this);
        view.findViewById(R.id.btn_three).setOnClickListener(this);
        view.findViewById(R.id.btn_five).setOnClickListener(this);
        view.findViewById(R.id.relate_news).setOnClickListener(this);
        view.findViewById(R.id.btn_xstj).setOnClickListener(this);
        view.findViewById(R.id.btn_mqfc).setOnClickListener(this);
        view.findViewById(R.id.btn_czgy).setOnClickListener(this);
        view.findViewById(R.id.btn_wldt).setOnClickListener(this);
        mTextView02 = (AutoTextView) view.findViewById(R.id.switcher02);
        adapterAd = new IndexAdViewPagerAdapter(getActivity());
    }

//    int selectC = 0;
//    class MyTimer extends CountDownTimer {
//
//        public MyTimer(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onFinish() {
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            if(listNews!= null && listNews.size() > 0){
//                if(listNews.size() > selectC){
//                    mTextView02.setText(listNews.get(selectC).getCompany_name());
//                    selectC ++;
//                }else{
//                    selectC = 0;
//                    mTextView02.setText(listNews.get(selectC).getCompany_name());
//                }
//            }
//
//        }
//    }


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
            if(!StringUtil.isNullOrEmpty(keywords.getText().toString())){
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                intent.putExtra("keywords", keywords.getText().toString());
                startActivity(intent);
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.relate_news:
            {
                //草原头条
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.right_img:
            case R.id.btn_one:
            {
                //热销产品--就是推荐
                Intent intent = new Intent(getActivity(), ListTuijianProduceActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_two:
            {
                //订单数据
//                Intent intent = new Intent(getActivity(), StatisticalActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(getActivity(), ListCaoyuanActivity.class);
                intent.putExtra("tmpNearby", "0");
                startActivity(intent);
            }
            break;
            case R.id.btn_three:
            {
                //附近草坪
                Intent intent = new Intent(getActivity(), ListCaoyuanActivity.class);
                intent.putExtra("tmpNearby", "1");
                startActivity(intent);
            }
            break;
            case R.id.btn_five:
            case R.id.btn_mqfc:
            {
                //名企排行
                Intent intent = new Intent(getActivity(), ListMingqiActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_wldt:
            {
                //物流信息
                Intent intent = new Intent(getActivity(), ListWuliuActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_xstj:
            {
                //草原机械
                Intent intent = new Intent(getActivity(), ListJixieActivity.class);
                intent.putExtra("cloud_jixie_use_id", "");
                intent.putExtra("tmpNearby", "0");
                startActivity(intent);
            }
            break;
            case R.id.btn_czgy:
            {
                //草种供应
                Intent intent = new Intent(getActivity(), ListCaozhongActivity.class);
                intent.putExtra("tmpNearby", "0");
                startActivity(intent);
            }
            break;
        }
    }

    private void initViewPager() {
        adapterAd.change(listsAd);
        adapterAd.setOnClickContentItemListener(this);
        viewpager = (ViewPager) view.findViewById(R.id.viewpager);
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
        viewGroup = (LinearLayout) view.findViewById(R.id.viewGroup);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                20, 20);
        layoutParams.setMargins(4, 3, 4, 3);

        dots = new ImageView[adapterAd.getCount()];
        for (int i = 0; i < adapterAd.getCount(); i++) {
            dot = new ImageView(getActivity());
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

    void getAds() {
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
                                    listsAd.clear();
                                    listsAd.addAll(data.getData());
//                                    if (data != null && data.getData() != null) {
//                                        for (RecordMsg recordMsg : data.getData()) {
//                                            RecordMsg recordMsgLocal = DBHelper.getInstance(getActivity()).getRecord(recordMsg.getMm_msg_id());
//                                            if (recordMsgLocal != null) {
//                                                //已经存在了 不需要插入了
//                                            } else {
//                                                DBHelper.getInstance(getActivity()).saveRecord(recordMsg);
//                                            }
//
//                                        }
//                                    }
                                    adapterAd.notifyDataSetChanged();
                                    initViewPager();
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
                params.put("ad_type", "1");
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

    private List<Company> listNews = new ArrayList<Company>();
    //获取最新的二十条入驻企业信息学
//    private void getNewsCompanys() {
//        StringRequest request = new StringRequest(
//                Request.Method.POST,
//                InternetURL.appGetCompanyNews,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String s) {
//                        if (StringUtil.isJson(s)) {
//                            CompanyData data = getGson().fromJson(s, CompanyData.class);
//                            if (data.getCode() == 200) {
//                                listNews.clear();
//                                listNews.addAll(data.getData());
//                                if(listNews != null && listNews.size()>0){
//                                    new Thread(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            MyTimer myTimer = new MyTimer(6000000, 3000);
//                                            myTimer.start();
//                                        }
//                                    }).run();
//                                }
//                            } else {
//                                Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                            }
//                        } else {
//                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                        }
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) {
//                        if(progressDialog != null){
//                            progressDialog.dismiss();
//                        }
//                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("page", "1");
//                params.put("size", "20");
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
//    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("update_location_success")) {
                //定位地址
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("update_location_success");
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    ArrayList<NewsObj> newsList = new ArrayList<NewsObj>();
    private void getNews() {
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

                                    newsList.addAll(data.getData());
                                    if(newsList != null ){
                                        if(newsList.size() > 0){
                                            txt_news_one.setText(newsList.get(0).getMm_msg_title());
                                        }
                                        if(newsList.size() > 1){
                                            txt_news_two.setText(newsList.get(1).getMm_msg_title());
                                        }
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
                params.put("page", String.valueOf(1));
                params.put("is_del", "0");
//                switch (Integer.parseInt(typeId)){
//                    case 0:
                        params.put("mm_msg_type", "1");
//                        break;
//                    case 1:
//                        break;
//                    case 2:
//                        break;
//                    case 3:
//                    {
//                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
//                            params.put("lat", String.valueOf(CaopingCloudApplication.latStr));
//                        }
//                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
//                            params.put("lng", String.valueOf(CaopingCloudApplication.lngStr));
//                        }
//                    }
//                    break;
//                }

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
