package com.caoping.cloud.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
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
import com.caoping.cloud.widget.CustomProgressDialog;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private EditText mobile;
    private EditText pwr;

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
        setContentView(R.layout.login_activity);
        DemoHelper.getInstance().logout(false,new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                    }
                });
            }
            @Override
            public void onProgress(int progress, String status) {
            }
            @Override
            public void onError(int code, String message) {
            }
        });

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("登录");
        mobile = (EditText) this.findViewById(R.id.mobile);
        pwr = (EditText) this.findViewById(R.id.pwr);

        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empMobile", ""), String.class)) &&
                !StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("empPass", ""), String.class))){
            mobile.setText(getGson().fromJson(getSp().getString("empMobile", ""), String.class));
            pwr.setText(getGson().fromJson(getSp().getString("empPass", ""), String.class));
        }
    }


    public void findAction(View view){
        //找回账号密码
        Intent intent = new Intent(LoginActivity.this, FindPwrMobileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;

        }
    }
    public void regAction(View view){
        Intent intent = new Intent(LoginActivity.this, RegOneActivity.class);
        startActivity(intent);
    }

    public void loginAction(View view){
        //登录
        if(StringUtil.isNullOrEmpty(mobile.getText().toString())){
            showMsg(LoginActivity.this ,"请输入手机号");
            return;
        }
        if(StringUtil.isNullOrEmpty(pwr.getText().toString())){
            showMsg(LoginActivity.this ,"请输入密码");
            return;
        }
        progressDialog = new CustomProgressDialog(LoginActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        login();
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
                                    showMsg(LoginActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(LoginActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(LoginActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", mobile.getText().toString());
                params.put("password", pwr.getText().toString());
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
                Utils.getMetaValue(LoginActivity.this, "api_key"));
        save("empId", member.getEmpId());
        save("emp_number", member.getEmp_number());
        save("empMobile", member.getEmpMobile());
        save("empPass", pwr.getText().toString());
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

        Member recordMsgLocal = DBHelper.getInstance(LoginActivity.this).getMemberId(member.getEmpId());
        if (recordMsgLocal != null) {
            //已经存在了 不需要插入了
        } else {
            DBHelper.getInstance(LoginActivity.this).saveMember(member);
        }

        DemoDBManager.getInstance().closeDB();

        // 将自己服务器返回的环信账号、昵称和头像URL设置到帮助类中。
//        DemoHelper.getInstance().getUserProfileManager().updateCurrentUserNickName(member.getEmpName());
//        DemoHelper.getInstance().getUserProfileManager().setCurrentUserAvatar(member.getEmpCover());
        DemoHelper.getInstance().setCurrentUserName(member.getEmpId());// 环信Id

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

                Intent intent = new Intent(LoginActivity.this,
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
                        Toast.makeText(LoginActivity.this, getString(R.string.Login_failed) + message,
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);

                        finish();
                    }
                });
            }
        });
    }

}
