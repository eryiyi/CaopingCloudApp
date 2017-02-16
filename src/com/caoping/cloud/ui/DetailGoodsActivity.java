package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.*;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CpObjSingData;
import com.caoping.cloud.db.DBHelper;
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.entiy.ShoppingCart;
import com.caoping.cloud.huanxin.ui.ChatActivity;
import com.caoping.cloud.util.DateUtil;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.MenuPopMenu;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.shareboard.ShareBoardConfig;
import com.umeng.socialize.utils.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 */
public class DetailGoodsActivity extends BaseActivity implements MenuPopMenu.OnItemClickListener, View.OnClickListener {
    private WebView detail_webview;
    private String strurl;

    private static final String APP_CACAHE_DIRNAME = "/webcache";
    private String id = "";

    private UMShareListener mShareListener;
    private ShareAction mShareAction;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_goods_activity);
        mShareListener = new CustomShareListener(this);
        id = getIntent().getExtras().getString("id");
        strurl = InternetURL.TO_DETAIL_GOODS+"?id=" + id;
        initView();
        detail_webview.setInitialScale(35);
        detail_webview.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        detail_webview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        detail_webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //设置 缓存模式
        // 开启 DOM storage API 功能
        detail_webview.getSettings().setDomStorageEnabled(true);
        //开启 database storage API 功能
        detail_webview.getSettings().setDatabaseEnabled(true);
        String cacheDirPath = getFilesDir().getAbsolutePath()+APP_CACAHE_DIRNAME;
//      String cacheDirPath = getCacheDir()getCacheDir.getAbsolutePath()+Constant.APP_DB_DIRNAME;
        //设置数据库缓存路径
        //设置  Application Caches 缓存目录
        detail_webview.getSettings().setAppCachePath(cacheDirPath);
        //开启 Application Caches 功能
//        detail_webview.getSettings().setAppCacheEnabled(true);
        detail_webview.getSettings().setJavaScriptEnabled(true);
        detail_webview.requestFocus();
        detail_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                callback.invoke(origin, true, true);
                super.onGeolocationPermissionsShowPrompt(origin, callback);
            }
        });


        detail_webview.loadUrl(strurl);
        detail_webview.setWebViewClient(new HelloWebViewClient());
        //根据商品ID查询商品详情
        getDetail();
        arrayMenu.add("收藏");
        arrayMenu.add("分享");
        arrayMenu.add("更多评论");
    }

    CpObj cpObj = null;
    public void getDetail() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CP_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    CpObjSingData data = getGson().fromJson(s, CpObjSingData.class);
                                    if(data != null){
                                        cpObj = data.getData();
                                    }
                                }else{
                                    Toast.makeText(DetailGoodsActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(DetailGoodsActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cloud_caoping_id", id);
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

    private void initView() {
        this.findViewById(R.id.iv_back).setOnClickListener(this);
        detail_webview = (WebView) this.findViewById(R.id.webview);
        this.findViewById(R.id.btn_share).setOnClickListener(this);
        this.findViewById(R.id.liner_lxmj).setOnClickListener(this);
        this.findViewById(R.id.liner_tdcy).setOnClickListener(this);
        this.findViewById(R.id.bottom_btn_three).setOnClickListener(this);
        this.findViewById(R.id.bottom_btn_four).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.liner_lxmj:
            {
                //联系卖家
                if(cpObj != null){
                    //IM
                    if(!cpObj.getEmp_id().equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                        Intent chatV = new Intent(DetailGoodsActivity.this, ChatActivity.class);
                        chatV.putExtra("userId", cpObj.getEmp_id());
                        chatV.putExtra("userName", cpObj.getEmp_name());
                        startActivity(chatV);
                    }else{
                        Toast.makeText(DetailGoodsActivity.this, "不能和自己聊天！",Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showMsg(DetailGoodsActivity.this, "暂无卖家数据，请稍后重试！");
                }
            }
                break;
            case R.id.liner_tdcy:
            {
                //他的草原
                if(cpObj != null){
                    Intent intent = new Intent(DetailGoodsActivity.this, ProfileActivity.class);
                    intent.putExtra("emp_id", cpObj.getEmp_id());
                    startActivity(intent);
                }
            }
                break;
            case R.id.bottom_btn_three:
            {
                //加入购物车
                //先查询是否已经存在该商品了
                if(DBHelper.getInstance(DetailGoodsActivity.this).isSaved(cpObj.getCloud_caoping_id())){
                    //如果已经加入购物车了
                    Toast.makeText(DetailGoodsActivity.this, R.string.add_cart_is, Toast.LENGTH_SHORT).show();
                }else{
                    //处理图片
                    String[] arras = new String[1];
                    if(!StringUtil.isNullOrEmpty(cpObj.getCloud_caoping_pic())){
                        String[] arr = cpObj.getCloud_caoping_pic().split(",");
                        if(arr != null && arr.length>0){
                            arras[0] = arr[0];
                        }
                    }
                    ShoppingCart shoppingCart = new ShoppingCart();
                    shoppingCart.setCartid(StringUtil.getUUID());
                    shoppingCart.setGoods_id(cpObj.getCloud_caoping_id());
                    shoppingCart.setEmp_id(cpObj.getEmp_id() == null ? "" : cpObj.getEmp_id());
                    shoppingCart.setManager_id(cpObj.getEmp_id() == null ? "" : cpObj.getEmp_id());
                    shoppingCart.setGoods_name(cpObj.getCloud_caoping_title());
                    if(arras != null && arras.length>0){
                        shoppingCart.setGoods_cover(arras[0]);
                    }else{
                        shoppingCart.setGoods_cover(cpObj.getCloud_caoping_pic());
                    }
                    shoppingCart.setSell_price(cpObj.getCloud_caoping_prices());
                    shoppingCart.setMarketPrice(cpObj.getCloud_caoping_prices());
                    shoppingCart.setGoods_count("1");
                    shoppingCart.setDateline(DateUtil.getCurrentDateTime());
                    shoppingCart.setIs_select("0");//默认选中
                    shoppingCart.setIs_zhiying("0");
                    shoppingCart.setEmp_name(cpObj.getEmp_name());
                    shoppingCart.setEmp_cover(cpObj.getEmp_cover());
                    DBHelper.getInstance(DetailGoodsActivity.this).addShoppingToTable(shoppingCart);
                    Toast.makeText(DetailGoodsActivity.this, R.string.add_cart_success, Toast.LENGTH_SHORT).show();
                }
            }
                break;
            case R.id.bottom_btn_four:
            {
                if(cpObj == null){
                    showMsg(DetailGoodsActivity.this, "商品不存在，请检查商品信息！");
                    return;
                }
                //订单
                Intent orderMakeView = new Intent(DetailGoodsActivity.this, OrderMakeActivity.class);
                ArrayList<ShoppingCart> arrayList = new ArrayList<ShoppingCart>();

                ShoppingCart shoppingCart = new ShoppingCart();
                shoppingCart.setCartid(StringUtil.getUUID());
                shoppingCart.setGoods_id(cpObj.getCloud_caoping_id());
                shoppingCart.setEmp_id(cpObj.getEmp_id() == null ? "" : cpObj.getEmp_id());
                shoppingCart.setManager_id(cpObj.getEmp_id() == null ? "" : cpObj.getEmp_id());
                shoppingCart.setGoods_name(cpObj.getCloud_caoping_title());
                String[] arras = new String[1];
                if(!StringUtil.isNullOrEmpty(cpObj.getCloud_caoping_pic())){
                    String[] arr = cpObj.getCloud_caoping_pic().split(",");
                    if(arr != null && arr.length>0){
                        arras[0] = arr[0];
                    }
                }
                if(arras != null && arras.length>0){
                    shoppingCart.setGoods_cover(arras[0]);
                }else{
                    shoppingCart.setGoods_cover(cpObj.getCloud_caoping_pic());
                }
                shoppingCart.setSell_price(cpObj.getCloud_caoping_prices());
                shoppingCart.setMarketPrice(cpObj.getCloud_caoping_prices());
                shoppingCart.setGoods_count("1");
                shoppingCart.setDateline(DateUtil.getCurrentDateTime());
                shoppingCart.setIs_select("0");//默认选中
                shoppingCart.setIs_zhiying("0");
                shoppingCart.setEmp_name(cpObj.getEmp_name());
                shoppingCart.setEmp_cover(cpObj.getEmp_cover());
                arrayList.add(shoppingCart);

                if(arrayList !=null && arrayList.size() > 0){
                    orderMakeView.putExtra("listsgoods",arrayList);
                    startActivity(orderMakeView);
                }else{
                    Toast.makeText(DetailGoodsActivity.this,R.string.cart_error_one,Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.btn_share:
            {
                Intent mineCar = new Intent(DetailGoodsActivity.this, MineCartActivity.class);
                startActivity(mineCar);
            }
                break;

        }
    }

    private void favour() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_FAVOUR,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    showMsg(DetailGoodsActivity.this, "收藏成功！");
                                }else{
                                    showMsg(DetailGoodsActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailGoodsActivity.this, R.string.goods_favour_error_two, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("goods_id", id);
                params.put("emp_id_favour", getGson().fromJson(getSp().getString("empId", ""), String.class));
                params.put("emp_id_goods", cpObj.getEmp_id());
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



    private class HelloWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && detail_webview.canGoBack()) {
            detail_webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            detail_webview.loadData("", "text/html; charset=UTF-8", null);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onPause ()
    {
        detail_webview.reload ();
        super.onPause ();
    }



    private MenuPopMenu menu;
    List<String> arrayMenu = new ArrayList<>();


    //弹出顶部主菜单
    public void onTopMenuPopupButtonClick(View view) {
        //顶部右侧按钮
        menu = new MenuPopMenu(DetailGoodsActivity.this, arrayMenu);
        menu.setOnItemClickListener(this);
        menu.showAsDropDown(view);
    }
    @Override
    public void onItemClick(int index) {
        switch (index) {
            case 0:
            {
                favour();
            }
            break;
            case 1:
            {
                UMImage image = new UMImage(DetailGoodsActivity.this, cpObj.getEmp_cover());
                String title =  cpObj.getCloud_caoping_title()==null?"":cpObj.getCloud_caoping_title();
                String content = cpObj.getCloud_caoping_content()==null?"":cpObj.getCloud_caoping_content();

                 /*无自定按钮的分享面板*/
                mShareAction = new ShareAction(DetailGoodsActivity.this).setDisplayList(
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.WEIXIN_FAVORITE,
                        SHARE_MEDIA.SINA, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE,
                        SHARE_MEDIA.ALIPAY,
                        SHARE_MEDIA.SMS, SHARE_MEDIA.EMAIL,
                        SHARE_MEDIA.MORE)
                        .withText(content)
                        .withTitle(title)
                        .withTargetUrl(strurl)
                        .withMedia(image)
                        .setCallback(mShareListener);

                ShareBoardConfig config = new ShareBoardConfig();
                config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_CENTER);
                config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR); // 圆角背景
                mShareAction.open(config);
            }
            break;
            case 2:
            {
                //更多评论
                Intent intent = new Intent(DetailGoodsActivity.this, ListCpCommentActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
                break;
        }
    }


    private static class CustomShareListener implements UMShareListener {

        private WeakReference<DetailGoodsActivity> mActivity;

        private CustomShareListener(DetailGoodsActivity activity) {
            mActivity = new WeakReference(activity);
        }

        @Override
        public void onResult(SHARE_MEDIA platform) {

            if (platform.name().equals("WEIXIN_FAVORITE")) {
                Toast.makeText(mActivity.get(), platform + " 收藏成功啦", Toast.LENGTH_SHORT).show();
            } else {
                if (platform!= SHARE_MEDIA.MORE&&platform!=SHARE_MEDIA.SMS
                        &&platform!=SHARE_MEDIA.EMAIL
                        &&platform!=SHARE_MEDIA.FLICKR
                        &&platform!=SHARE_MEDIA.FOURSQUARE
                        &&platform!=SHARE_MEDIA.TUMBLR
                        &&platform!=SHARE_MEDIA.POCKET
                        &&platform!=SHARE_MEDIA.PINTEREST
                        &&platform!=SHARE_MEDIA.LINKEDIN
                        &&platform!=SHARE_MEDIA.INSTAGRAM
                        &&platform!=SHARE_MEDIA.GOOGLEPLUS
                        &&platform!=SHARE_MEDIA.YNOTE
                        &&platform!=SHARE_MEDIA.EVERNOTE){
                    Toast.makeText(mActivity.get(), platform + " 分享成功啦", Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            if (platform!= SHARE_MEDIA.MORE&&platform!=SHARE_MEDIA.SMS
                    &&platform!=SHARE_MEDIA.EMAIL
                    &&platform!=SHARE_MEDIA.FLICKR
                    &&platform!=SHARE_MEDIA.FOURSQUARE
                    &&platform!=SHARE_MEDIA.TUMBLR
                    &&platform!=SHARE_MEDIA.POCKET
                    &&platform!=SHARE_MEDIA.PINTEREST
                    &&platform!=SHARE_MEDIA.LINKEDIN
                    &&platform!=SHARE_MEDIA.INSTAGRAM
                    &&platform!=SHARE_MEDIA.GOOGLEPLUS
                    &&platform!=SHARE_MEDIA.YNOTE
                    &&platform!=SHARE_MEDIA.EVERNOTE){
                Toast.makeText(mActivity.get(), platform + " 分享失败啦", Toast.LENGTH_SHORT).show();
                if (t != null) {
                    Log.d("throw", "throw:" + t.getMessage());
                }
            }

        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 屏幕横竖屏切换时避免出现window leak的问题
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mShareAction.close();
    }

}
