package com.caoping.cloud.base;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.upload.MultiPartStringRequest;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.google.gson.Gson;
import com.hyphenate.easeui.ui.EaseBaseActivity;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ExecutorService;

/**
 * Created by zhanghl on 2014/11/11.
 */
public class BaseActivity extends EaseBaseActivity {
    public CustomProgressDialog progressDialog;
    private static final int notifiId = 11;
    protected NotificationManager notificationManager;
    /**
     * 屏幕的宽度和高度
     */
    protected int mScreenWidth;
    protected int mScreenHeight;

    private Context mContext;
    private final String mPageName = "AnalyticsHome";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.green));

        }
        mContext = this;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        /**
         * 获取屏幕宽度和高度
         */
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;

        ActivityTack.getInstanse().addActivity(this);
        MobclickAgent.setDebugMode(true);
        // SDK在统计Fragment时，需要关闭Activity自带的页面统计，
        // 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
        MobclickAgent.openActivityDurationTrack(false);
        // MobclickAgent.setAutoLocation(true);
        // MobclickAgent.setSessionContinueMillis(1000);
        // MobclickAgent.startWithConfigure(
        // new UMAnalyticsConfig(mContext, "4f83c5d852701564c0000011", "Umeng",
        // EScenarioType.E_UM_NORMAL));
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);


    }

    /**
     * Toast的封装
     *
     * @param mContext 上下文，来区别哪一个activity调用的
     * @param msg      你希望显示的值。
     */
    public static void showMsg(Context mContext, String msg) {
        Toast toast = new Toast(mContext);
        toast = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);//设置居中
        toast.show();//显示,(缺了这句不显示)
    }

    /**
     * 获取当前Application
     *
     * @return
     */
    public CaopingCloudApplication getMyApp() {
        return (CaopingCloudApplication) getApplication();
    }

    //存储sharepreference
    public void save(String key, Object value) {
        getMyApp().getSp().edit()
                .putString(key, getMyApp().getGson().toJson(value))
                .commit();
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
        MobclickAgent.onResume(mContext);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
        MobclickAgent.onPause(mContext);
    }


    public void onStop() {
        super.onStop();
    }

    /**
     * 返回
     *
     * @param view
     */
    public void back(View view) {
        finish();
    }

    /**
     * 获取Volley请求队列
     *
     * @return
     */
    public RequestQueue getRequestQueue() {
        return getMyApp().getRequestQueue();
    }

    /**
     * 获取Gson
     *
     * @return
     */
    public Gson getGson() {
        return getMyApp().getGson();
    }

    /**
     * 获取SharedPreferences
     *
     * @return
     */
    public SharedPreferences getSp() {
        return getMyApp().getSp();
    }

    /**
     * 获取自定义线程池
     *
     * @return
     */
    public ExecutorService getLxThread() {
        return getMyApp().getLxThread();
    }

    protected void onDestroy() {
        ActivityTack.getInstanse().removeActivity(this);
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        super.onDestroy();
    }

    public void addPutUploadFileRequest(final String url,
                                        final Map<String, File> files, final Map<String, String> params,
                                        final Response.Listener<String> responseListener, final Response.ErrorListener errorListener,
                                        final Object tag) {
        if (null == url || null == responseListener) {
            return;
        }

        MultiPartStringRequest multiPartRequest = new MultiPartStringRequest(
                Request.Method.POST, url, responseListener, errorListener) {

            @Override
            public Map<String, File> getFileUploads() {
                return files;
            }

            @Override
            public Map<String, String> getStringUploads() {
                return params;
            }

        };

        getRequestQueue().add(multiPartRequest);
    }

}
