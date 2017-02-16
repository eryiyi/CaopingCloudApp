package com.caoping.cloud.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemProfileToutiaoAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.NewsObjData;
import com.caoping.cloud.entiy.NewsObj;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.ui.DetailNewsActivity;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.SnsPlatform;
import com.umeng.socialize.utils.ShareBoardlistener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NewsFragment extends BaseFragment implements OnClickContentItemListener {
    private String time_is = "1";
    private String favour_is = "0";

    public ProgressDialog progressDialog;
    Activity activity;
    ArrayList<NewsObj> newsList = new ArrayList<NewsObj>();
    PullToRefreshListView mListView;
    ItemProfileToutiaoAdapter mAdapter;
    String typeId;
    String typeName;

    ImageView detail_loading;
    public final static int SET_NEWSLIST = 0;
    private TextView item_textview;
    private static boolean IS_REFRESH = true;
    private int pageIndex = 1;

    NewsObj tmpVideos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle args = getArguments();
        registerBoradcastReceiver();
        typeId = args != null ? args.getString("id") : "";
        typeName = args != null ? args.getString("name") : "";
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.news_fragment_lstv, null);
        initView(view);
        item_textview.setText(typeName);
        initData();
        return view;
    }

    private void initView(View view) {
        mListView = (PullToRefreshListView) view.findViewById(R.id.mListView);
        item_textview = (TextView) view.findViewById(R.id.item_textview);
        detail_loading = (ImageView) view.findViewById(R.id.detail_loading);
        mAdapter = new ItemProfileToutiaoAdapter(newsList, activity, false);
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(getActivity().getApplicationContext(), System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(newsList.size()>(position-1)){
                    NewsObj newsObj = newsList.get(position-1);
                    if(newsObj != null){
                        Intent intent = new Intent(getActivity(), DetailNewsActivity.class);
                        intent.putExtra("mm_msg_id", newsObj.getMm_msg_id());
                        startActivity(intent);
                    }
                }

            }
        });
        mAdapter.setOnClickContentItemListener(this);

    }

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
                                    if (IS_REFRESH) {
                                        newsList.clear();
                                    }
                                    newsList.addAll(data.getData());
                                    mListView.onRefreshComplete();
                                    mAdapter.notifyDataSetChanged();
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
                params.put("page", String.valueOf(pageIndex));
                params.put("is_del", "0");
                switch (Integer.parseInt(typeId)){
                    case 0:
                        params.put("mm_msg_type", "1");
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                    {
                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                            params.put("lat", String.valueOf(CaopingCloudApplication.latStr));
                        }
                        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                            params.put("lng", String.valueOf(CaopingCloudApplication.lngStr));
                        }
                    }
                        break;
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

    int tmpId = 0;

    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        tmpVideos = newsList.get(position);
        tmpId = position;
        switch (flag){
            case 1:
                //评论
            {
//                Intent comment = new Intent(getActivity(), PublishVideoCommentAcitvity.class);
//                comment.putExtra(Constants.FATHER_PERSON_NAME, "");
//                comment.putExtra(Constants.FATHER_UUID, "0");
//                comment.putExtra(Constants.RECORD_UUID, tmpVideos.getId());
//                comment.putExtra("fplempid", "");
//                startActivity(comment);
            }
            break;
            case 2:
                //分享
            {
                share();
            }
            break;
            case 3:
                //赞
            {
//                progressDialog =  new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
//                progressDialog.setCancelable(true);
//                progressDialog.setIndeterminate(true);
//                progressDialog.show();
//                zan_click(tmpVideos);
            }
            break;
            case 4:
                //播放
//                String videoUrl = tmpVideos.getVideoUrl();
////                Intent intent = new Intent(VideosActivity.this, VideoPlayerActivity2.class);
////                VideoPlayer video = new VideoPlayer(videoUrl);
////                intent.putExtra(Constants.EXTRA_LAYOUT, "0");
////                intent.putExtra(VideoPlayer.class.getName(), video);
////                startActivity(intent);
//                final Uri uri = Uri.parse(videoUrl);
//                final Intent it = new Intent(Intent.ACTION_VIEW, uri);
//                startActivity(it);
                break;
        }
    }

    void share() {
        new ShareAction(getActivity()).setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .setShareboardclickCallback(shareBoardlistener)
                .open();
    }

    private ShareBoardlistener shareBoardlistener = new ShareBoardlistener() {

        @Override
        public void onclick(SnsPlatform snsPlatform, SHARE_MEDIA share_media) {
            UMImage image = new UMImage(getActivity(), "");
            String title =  getGson().fromJson(getSp().getString("mm_emp_nickname", ""), String.class)+"邀您免费看电影" ;
//            String content = tmpVideos.getTitle()+tmpVideos.getContent();
            String content = "";
            new ShareAction(getActivity()).setPlatform(share_media).setCallback(umShareListener)
                    .withText(content)
                    .withTitle(title)
                    .withTargetUrl((InternetURL.SHARE_VIDEOS + "?id=" ))
                    .withMedia(image)
                    .share();
        }
    };

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_success), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_error), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(getActivity(), platform + getResources().getString(R.string.share_cancel), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(getActivity()).onActivityResult(requestCode, resultCode, data);
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("list_news_success")) {
                progressDialog = new CustomProgressDialog(getActivity(), "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.show();
                initData();
            }
        }

    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("list_news_success");//设置下拉按钮的广播事件
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }


}
