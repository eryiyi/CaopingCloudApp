package com.caoping.cloud.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.MainActivity;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.MemberData;
import com.caoping.cloud.db.DBHelper;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.huanxin.DemoHelper;
import com.caoping.cloud.huanxin.db.DemoDBManager;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.util.Utils;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 缓冲页
 */
public class WelcomeActivity extends BaseActivity implements Runnable,AMapLocationListener {
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //定位
    private AMapLocationClient mlocationClient = null;

    @TargetApi(23)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                Intent intent = new Intent();
                intent.setAction(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
        setContentView(R.layout.welcome_activity);

        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(20000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();


        save("isLogin", "0");//1已经登录了  0未登录
        // 启动一个线程
        new Thread(WelcomeActivity.this).start();
    }

    @Override
    public void run() {
        try {
            // 3秒后跳转到登录界面
            Thread.sleep(1500);
            SharedPreferences.Editor editor = getSp().edit();
            boolean isFirstRun = getSp().getBoolean("isFirstRun", true);
            if (isFirstRun) {
                editor.putBoolean("isFirstRun", false);
                editor.commit();
                Intent loadIntent = new Intent(WelcomeActivity.this, AboutActivity.class);
                startActivity(loadIntent);
                finish();
            } else {
                if (!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empMobile", ""), String.class)) &&
                        !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empPass", ""), String.class))) {
                    login();
                } else {
                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void login() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.LOGIN__URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    MemberData data = getGson().fromJson(s, MemberData.class);
                                    Member member = data.getData();
                                    saveMember(member);
                                }else {
                                    startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                            finish();
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
                        startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
                        finish();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", getGson().fromJson(getSp().getString("empMobile", ""), String.class));
                params.put("password", getGson().fromJson(getSp().getString("empPass", ""), String.class));
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

    public void saveMember(Member member){
        PushManager.startWork(getApplicationContext(),
                PushConstants.LOGIN_TYPE_API_KEY,
                Utils.getMetaValue(WelcomeActivity.this, "api_key"));
        save("empId", member.getEmpId());
        save("emp_number", member.getEmp_number());
        save("empMobile", member.getEmpMobile());
        save("empName", member.getEmpName());
        save("empCover", member.getEmpCover());
        save("empSex", member.getEmpSex());
        save("isUse", member.getIsUse());
        save("dateline", member.getDateline());
        save("emp_birthday", member.getEmp_birthday());
        save("pushId", member.getPushId());
        save("hxUsername", member.getHxUsername());
        save("isInGroup", member.getIsInGroup());
        save("lat", member.getLat());
        save("lng", member.getLng());
        save("level_id", member.getLevel_id());
        save("emp_erweima", member.getEmp_erweima());
        save("emp_up", member.getEmp_up());
        save("emp_up_mobile", member.getEmp_up_mobile());
        save("levelName", member.getLevelName());
        save("jfcount", member.getJfcount());
        save("emp_pay_pass", member.getEmp_pay_pass());
        save("package_money", member.getPackage_money());
        save("empType", member.getEmpType());
        save("is_card_emp", member.getIs_card_emp());
        save("lx_attribute_id", member.getLx_attribute_id());

        save("level_zhe", member.getLevel_zhe());

        save("is_vip_one", member.getIs_vip_one());
        save("is_vip_two", member.getIs_vip_two());
        save("is_vip_three", member.getIs_vip_three());
        save("is_vip_four", member.getIs_vip_four());
        save("is_vip_five", member.getIs_vip_five());
        save("is_shiming_rz", member.getIs_shiming_rz());
        save("is_qiye_rz", member.getIs_qiye_rz());


        save("company_id", member.getCompany_id());
        save("company_name", member.getCompany_name());
        save("company_person", member.getCompany_person());
        save("company_tel", member.getCompany_tel());
        save("company_address", member.getCompany_address());
        save("lat_company", member.getLat_company());
        save("lng_company", member.getLng_company());
        save("company_pic", member.getCompany_pic());
        save("is_check", member.getIs_check());
        save("provinceid", member.getProvinceid());
        save("cityid", member.getCityid());
        save("areaid", member.getAreaid());

        save("is_gys", member.getIs_gys());
        save("is_fws", member.getIs_fws());

        save("isLogin", "1");//1已经登录了  0未登录

        Member recordMsgLocal = DBHelper.getInstance(WelcomeActivity.this).getMemberId(member.getEmpId());
        if (recordMsgLocal != null) {
            //已经存在了 不需要插入了
        } else {
            DBHelper.getInstance(WelcomeActivity.this).saveMember(member);
        }

        DemoDBManager.getInstance().closeDB();
        // reset current user name before login
        DemoHelper.getInstance().setCurrentUserName(member.getEmpId());
        final long start = System.currentTimeMillis();

        EMClient.getInstance().login(member.getEmpId(), "123456", new EMCallBack() {

            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();

                boolean updatenick = EMClient.getInstance().updateCurrentUserNick(
                        CaopingCloudApplication.currentUserNick.trim());
                if (!updatenick) {
                    Log.e("LoginActivity", "update current user nick fail");
                }

                // get user's info (this should be get from App's server or 3rd party service)
                DemoHelper.getInstance().getUserProfileManager().asyncGetCurrentUserInfo();

                Intent intent = new Intent(WelcomeActivity.this,
                        MainActivity.class);
                startActivity(intent);

                finish();
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(final int code, final String message) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(WelcomeActivity.this, getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(WelcomeActivity.this,
                                LoginActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
            }
        });
    }


    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                CaopingCloudApplication.latStr = String.valueOf(amapLocation.getLatitude());
                CaopingCloudApplication.lngStr = String.valueOf(amapLocation.getLongitude());
                CaopingCloudApplication.locationAddress = amapLocation.getAddress();
                CaopingCloudApplication.locationProvinceName = String.valueOf(amapLocation.getProvince());
                CaopingCloudApplication.locationCityName = String.valueOf(amapLocation.getCity());
                CaopingCloudApplication.locationAreaName = String.valueOf(amapLocation.getDistrict());

                if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr) && !StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr) && !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                    sendLocation();
                }
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    }

    void sendLocation() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SEND_LOCATION_BYID_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {

                                } else {
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
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("lat", (CaopingCloudApplication.latStr == null ? "" : CaopingCloudApplication.latStr));
                params.put("lng", (CaopingCloudApplication.lngStr == null ? "" : CaopingCloudApplication.lngStr));
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                    params.put("empId", getGson().fromJson(getSp().getString("empId", ""), String.class));
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



}
