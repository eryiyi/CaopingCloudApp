package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
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
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.adapter.ItemProfileCaoyuanAdapter;
import com.caoping.cloud.adapter.ItemProfileToutiaoAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CompanyDataSingle;
import com.caoping.cloud.data.CpObjData;
import com.caoping.cloud.data.NewsObjData;
import com.caoping.cloud.data.SuccessData;
import com.caoping.cloud.entiy.Company;
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.entiy.NewsObj;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.HttpUtils;
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
 * Created by zhl on 2016/8/30.
 * 他的主页
 */
public class ProfileActivity extends BaseActivity implements View.OnClickListener {
    private ImageView cover;
    private ImageView btn_gz;
    private TextView company;
    private TextView profile_btn_count_one;
    private TextView profile_btn_count_two;
    private TextView profile_btn_count_three;
    private ImageView profile_btn_count_one_liner;
    private ImageView profile_btn_count_two_liner;
    private ImageView profile_btn_count_three_liner;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    //viewPager
    private ViewPager vPager;
    private List<View> views;
    private View view1, view2, view3;
    private int currentSelect = 0;//当前选中的viewpage

    private PullToRefreshListView lstv1;
    private ItemProfileToutiaoAdapter adapter1;
    List<NewsObj> lists1 = new ArrayList<NewsObj>();
    private int pageIndex1 = 1;
    private static boolean IS_REFRESH1 = true;
    private ImageView search_null1;

    private PullToRefreshListView lstv2;
    private ItemProfileCaoyuanAdapter adapter2;
    List<CpObj> lists2 = new ArrayList<CpObj>();
    private int pageIndex2 = 1;
    private static boolean IS_REFRESH2 = true;
    private ImageView search_null2;

    private String emp_id;//用户ID
    private boolean flagT;//如果是自己  就是true

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        emp_id = getIntent().getExtras().getString("emp_id");
        if(emp_id.equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
            flagT = true;
        }else {
            flagT = false;
        }
        initView();
        //获得他的头条
        getNews();
        //获得他的草原信息
        getCp();
        //获得他的公司详情
        getDetail();

        if(flagT){
            btn_gz.setVisibility(View.GONE);
            profile_btn_count_one.setText("我的头条");
            profile_btn_count_two.setText("我的草原");
        }
    }

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
                                    //处理数据，需要的话保存到数据库
//                                    if (data != null && data.getData() != null) {
//                                        DBHelper dbHelper = DBHelper.getInstance(getActivity());
//                                        for (Videos videos : data.getData()) {
//                                            if (dbHelper.getVideosById(videos.getId()) == null) {
//                                                DBHelper.getInstance(getActivity()).saveVideos(videos);
//                                            }
//                                        }
//                                    }
                                }else{
                                    Toast.makeText(ProfileActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                params.put("page", String.valueOf(pageIndex1));
                params.put("is_del", "0");
                params.put("emp_id", emp_id);
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

    private void getCp() {
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
                                Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ProfileActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex2));
                params.put("cloud_caoping_is_use", "0");
                params.put("cloud_caoping_is_del", "0");
                params.put("emp_id", emp_id);
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


    private Company companyObj;
    //查询公司详情
    void getDetail(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_COMPANY_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    CompanyDataSingle data = getGson().fromJson(s, CompanyDataSingle.class);
                                    companyObj = data.getData();
                                    if(companyObj != null ){
                                        company_person.setText("联系人："+(companyObj.getCompany_person()==null?"":companyObj.getCompany_person()));
                                        company_tel.setText("电话："+(companyObj.getCompany_tel()==null?"":companyObj.getCompany_tel()));
                                        company_address.setText("地址："+(companyObj.getCompany_address()==null?"":companyObj.getCompany_address()));
                                        company_detail.setText(companyObj.getCompany_detail()==null?"":companyObj.getCompany_detail());
                                        company.setText(companyObj.getEmp_name()+" "+(companyObj.getCompany_name()==null?"":companyObj.getCompany_name()));
                                        imageLoader.displayImage(companyObj.getCompany_pic(), company_pic, CaopingCloudApplication.options, animateFirstListener);
                                        imageLoader.displayImage(companyObj.getEmp_cover(), cover, CaopingCloudApplication.txOptions, animateFirstListener);
                                    }else {
                                        showMsg(ProfileActivity.this, "该用户尚未开通草原！");
                                        finish();
                                    }
                                }else{
                                    showMsg(ProfileActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                        Toast.makeText(ProfileActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
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

    private TextView company_person;
    private TextView company_tel;
    private TextView company_address;
    private TextView company_detail;
    private ImageView company_pic;

    private void initView() {
        this.findViewById(R.id.profile_liner_one).setOnClickListener(this);
        this.findViewById(R.id.profile_liner_two).setOnClickListener(this);
        this.findViewById(R.id.profile_liner_three).setOnClickListener(this);
        this.findViewById(R.id.close).setOnClickListener(this);
        cover = (ImageView) this.findViewById(R.id.cover);
        cover.setOnClickListener(this);
        company = (TextView) this.findViewById(R.id.company);
        profile_btn_count_one = (TextView) this.findViewById(R.id.profile_btn_count_one);
        profile_btn_count_two = (TextView) this.findViewById(R.id.profile_btn_count_two);
        profile_btn_count_three = (TextView) this.findViewById(R.id.profile_btn_count_three);
        btn_gz = (ImageView) this.findViewById(R.id.btn_gz);
        profile_btn_count_one_liner = (ImageView) this.findViewById(R.id.profile_btn_count_one_liner);
        profile_btn_count_two_liner = (ImageView) this.findViewById(R.id.profile_btn_count_two_liner);
        profile_btn_count_three_liner = (ImageView) this.findViewById(R.id.profile_btn_count_three_liner);

        btn_gz.setOnClickListener(this);
        profile_btn_count_one_liner.setOnClickListener(this);
        profile_btn_count_two_liner.setOnClickListener(this);
        profile_btn_count_three_liner.setOnClickListener(this);

        //viewPage
        vPager = (ViewPager) this.findViewById(R.id.vPager);
        views = new ArrayList<View>();
        final LayoutInflater inflater = getLayoutInflater();
        view1 = inflater.inflate(R.layout.profile_one_fragment, null);
        view2 = inflater.inflate(R.layout.profile_two_fragment, null);
        view3 = inflater.inflate(R.layout.profile_three_fragment, null);
        views.add(view1);
        views.add(view2);
        views.add(view3);

        company_person = (TextView) view3.findViewById(R.id.company_person);
        company_tel = (TextView) view3.findViewById(R.id.company_tel);
        company_address = (TextView) view3.findViewById(R.id.company_address);
        company_detail = (TextView) view3.findViewById(R.id.company_detail);
        company_pic = (ImageView) view3.findViewById(R.id.company_pic);


        vPager.setAdapter(new MyViewPagerAdapter(views));
        currentSelect = 0;
        vPager.setCurrentItem(currentSelect);
        vPager.setOnPageChangeListener(new MyOnPageChangeListener());


        search_null1 = (ImageView) view1.findViewById(R.id.search_null);
        lstv1 = (PullToRefreshListView) view1.findViewById(R.id.lstv);
        adapter1 = new ItemProfileToutiaoAdapter(lists1, ProfileActivity.this, false);
        lstv1.setMode(PullToRefreshBase.Mode.BOTH);
        lstv1.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ProfileActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = true;
                pageIndex1 = 1;
                getNews();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ProfileActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH1 = false;
                pageIndex1++;
                getNews();
            }
        });
        lstv1.setAdapter(adapter1);
        lstv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists1.size() > ((position-1))){
                    NewsObj newsObj = lists1.get(position-1);
                    if(newsObj != null){
                        Intent intent = new Intent(ProfileActivity.this, DetailNewsActivity.class);
                        intent.putExtra("mm_msg_id", newsObj.getMm_msg_id());
                        startActivity(intent);
                    }
                }

            }
        });


        search_null2 = (ImageView) view2.findViewById(R.id.search_null);
        lstv2 = (PullToRefreshListView) view2.findViewById(R.id.lstv);
        adapter2 = new ItemProfileCaoyuanAdapter(lists2, ProfileActivity.this);
        lstv2.setMode(PullToRefreshBase.Mode.BOTH);
        lstv2.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ProfileActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = true;
                pageIndex2 = 1;
                getCp();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(ProfileActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH2 = false;
                pageIndex2++;
                getCp();
            }
        });
        lstv2.setAdapter(adapter2);
        lstv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists2.size()>(position-1)){
                    CpObj cpObj = lists2.get(position-1);
                    if(cpObj != null){
                        Intent intent  = new Intent(ProfileActivity.this, DetailGoodsActivity.class);
                        intent.putExtra("id", cpObj.getCloud_caoping_id());
                        startActivity(intent);
                    }
                }
            }
        });

    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> mListViews;

        public MyViewPagerAdapter(List<View> mListViews) {
            this.mListViews = mListViews;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mListViews.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mListViews.get(position), 0);
            return mListViews.get(position);
        }

        @Override
        public int getCount() {
            return mListViews.size();
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
//        int one = offset * 1 + bmpW;
//        int two = one * 1;

        public void onPageScrollStateChanged(int arg0) {
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageSelected(int arg0) {
            if (arg0 == 0) {
                currentSelect = 0;
                profile_btn_count_one.setTextColor(getResources().getColor(R.color.green));
                profile_btn_count_two.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_three.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_one_liner.setVisibility(View.VISIBLE);
                profile_btn_count_two_liner.setVisibility(View.GONE);
                profile_btn_count_three_liner.setVisibility(View.GONE);
            }
            if (arg0 == 1) {
                currentSelect = 1;
                profile_btn_count_one.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_two.setTextColor(getResources().getColor(R.color.green));
                profile_btn_count_three.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_one_liner.setVisibility(View.GONE);
                profile_btn_count_two_liner.setVisibility(View.VISIBLE);
                profile_btn_count_three_liner.setVisibility(View.GONE);
            }
            if (arg0 == 2) {
                currentSelect = 2;
                profile_btn_count_one.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_two.setTextColor(getResources().getColor(R.color.text_color));
                profile_btn_count_three.setTextColor(getResources().getColor(R.color.green));
                profile_btn_count_one_liner.setVisibility(View.GONE);
                profile_btn_count_two_liner.setVisibility(View.GONE);
                profile_btn_count_three_liner.setVisibility(View.VISIBLE);
            }
        }
    }

        boolean isMobileNet, isWifiNet;


        @Override
        public void onClick(View v) {
            try {
                isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
                isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
                if (!isMobileNet && !isWifiNet) {
                    Toast.makeText(ProfileActivity.this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            switch (v.getId()) {
                case R.id.profile_liner_one: {
                    currentSelect = 0;
                    vPager.setCurrentItem(currentSelect);
                    profile_btn_count_one.setTextColor(getResources().getColor(R.color.green));
                    profile_btn_count_two.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_three.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_one_liner.setVisibility(View.VISIBLE);
                    profile_btn_count_two_liner.setVisibility(View.GONE);
                    profile_btn_count_three_liner.setVisibility(View.GONE);
                }
                break;
                case R.id.profile_liner_two: {
                    currentSelect = 1;
                    vPager.setCurrentItem(currentSelect);
                    profile_btn_count_one.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_two.setTextColor(getResources().getColor(R.color.green));
                    profile_btn_count_three.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_one_liner.setVisibility(View.GONE);
                    profile_btn_count_two_liner.setVisibility(View.VISIBLE);
                    profile_btn_count_three_liner.setVisibility(View.GONE);
                }
                break;
                case R.id.profile_liner_three: {
                    currentSelect = 2;
                    vPager.setCurrentItem(currentSelect);
                    profile_btn_count_one.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_two.setTextColor(getResources().getColor(R.color.text_color));
                    profile_btn_count_three.setTextColor(getResources().getColor(R.color.green));
                    profile_btn_count_one_liner.setVisibility(View.GONE);
                    profile_btn_count_two_liner.setVisibility(View.GONE);
                    profile_btn_count_three_liner.setVisibility(View.VISIBLE);
                }
                break;
                case R.id.btn_gz:
                {
                    //关注
                    if(emp_id.equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                        showMsg(ProfileActivity.this, "不能关注自己");
                    }else {
                        saveFavour();
                    }

                }
                break;
                case R.id.close:
                {
                    finish();
                }
                    break;
            }

        }

    void saveFavour(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_FAVOUR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    SuccessData data = getGson().fromJson(s, SuccessData.class);
                                    showMsg(ProfileActivity.this, "关注成功！");
                                }else{
                                    showMsg(ProfileActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ProfileActivity.this, "关注失败", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", emp_id);
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




}
