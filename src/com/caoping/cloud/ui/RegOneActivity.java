package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.caoping.cloud.MainActivity;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.MemberData;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.receiver.SMSBroadcastReceiver;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.util.Utils;
import com.caoping.cloud.widget.CustomProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 注册
 */
public class RegOneActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private Button btn_card;
    private EditText mobile;
    private EditText card;
    private EditText emp_up_mobile;//邀请人手机号
    private Resources res;
    //mob短信
    // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = InternetURL.APP_MOB_KEY;//"69d6705af33d";0d786a4efe92bfab3d5717b9bc30a10d
    // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = InternetURL.APP_MOB_SCRECT;
    public String phString;//手机号码

    //短信读取
    private SMSBroadcastReceiver mSMSBroadcastReceiver;
    private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private EditText pwr_one;
    private EditText pwr_two;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reg_one_activity);
        res = getResources();
        //mob短信无GUI
        SMSSDK.initSDK(this, APPKEY, APPSECRET, true);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }

        };
        SMSSDK.registerEventHandler(eh);

        initView();

        //生成广播处理
        mSMSBroadcastReceiver = new SMSBroadcastReceiver();
        //实例化过滤器并设置要过滤的广播
        IntentFilter intentFilter = new IntentFilter(ACTION);
        intentFilter.setPriority(Integer.MAX_VALUE);
        //注册广播
        this.registerReceiver(mSMSBroadcastReceiver, intentFilter);
        mSMSBroadcastReceiver.setOnReceivedMessageListener(new SMSBroadcastReceiver.MessageListener() {
            @Override
            public void onReceived(String message) {
                //花木通的验证码：8469【掌淘科技】
                if (!StringUtil.isNullOrEmpty(message)) {
                    String codestr = StringUtil.valuteNumber(message);
                    if (!StringUtil.isNullOrEmpty(codestr)) {
                        card.setText(codestr);
                    }
                }
            }
        });
    }

    private void initView() {
        btn_card = (Button) this.findViewById(R.id.btn_card);
        btn_card.setOnClickListener(this);
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("手机快速注册");
        mobile = (EditText) this.findViewById(R.id.mobile);
        emp_up_mobile = (EditText) this.findViewById(R.id.emp_up_mobile);
        card = (EditText) this.findViewById(R.id.card);
        this.findViewById(R.id.liner_one).setOnClickListener(this);
        pwr_one = (EditText) this.findViewById(R.id.pwr_one);
        pwr_two = (EditText) this.findViewById(R.id.pwr_two);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.btn_card:
                //点此获得验证码
                //验证码
               if(StringUtil.isNullOrEmpty(mobile.getText().toString())){
                   showMsg(RegOneActivity.this, "请输入手机号！");
                   return;
               }
                checkMObile();
                break;
            case R.id.liner_one:
            {
                //注册协议
                Intent intent = new Intent(RegOneActivity.this, RegistMsgActivity.class);
                startActivity(intent);
            }
                break;

        }
    }

    public void loginAction(View view){
        //登录
        if(StringUtil.isNullOrEmpty(mobile.getText().toString())){
            showMsg(RegOneActivity.this, "请输入手机号！");
            return;
        }
        if(StringUtil.isNullOrEmpty(card.getText().toString())){
            showMsg(RegOneActivity.this, "请输入验证码！");
            return;
        }
        if(StringUtil.isNullOrEmpty(pwr_one.getText().toString())){
            showMsg(RegOneActivity.this, "请输入6到18位密码");
            return;
        }
        if(pwr_one.getText().toString().length() > 18 || pwr_one.getText().toString().length() < 6){
            showMsg(RegOneActivity.this, "请输入6到18位密码");
            return;
        }
        if(StringUtil.isNullOrEmpty(pwr_two.getText().toString())){
            showMsg(RegOneActivity.this, "请输入确认密码");
            return;
        }
        if(!pwr_one.getText().toString().equals(pwr_two.getText().toString())){
            showMsg(RegOneActivity.this, "两次输入密码不一致");
            return;
        }
        progressDialog = new CustomProgressDialog(RegOneActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
//        SMSSDK.submitVerificationCode("86", phString, card.getText().toString());
        reg();
    }

    private void reg() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.REG_ONE__URL,
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
                                    if(member != null){
                                        showMsg(RegOneActivity.this, "注册成功");
                                        saveMember(member);
                                    }
                                }else {
                                    switch (Integer.parseInt(code)){
                                        case 1:
                                            showMsg(RegOneActivity.this, "手机号已经注册了，换个试试");
                                            break;
                                        case 2:
                                            showMsg(RegOneActivity.this, "注册失败，请稍后重试");
                                            break;
                                        case 3:
                                            showMsg(RegOneActivity.this, "注册失败，请稍后重试");
                                            break;
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(RegOneActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(RegOneActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empMobile", mobile.getText().toString());
                params.put("empPass",pwr_one.getText().toString());
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
                Utils.getMetaValue(RegOneActivity.this, "api_key"));
        save("empId", member.getEmpId());
        save("emp_number", member.getEmp_number());
        save("empMobile", member.getEmpMobile());
        save("empPass", pwr_one.getText().toString());
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

        save("isLogin", "1");//1已经登录了  0未登录

        Intent intent = new Intent(RegOneActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void findAction(View view){
        //找回账号密码
        Intent intent = new Intent(RegOneActivity.this, FindPwrMobileActivity.class);
        startActivity(intent);
    }

    void checkMObile() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_EMP_MOBILE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(RegOneActivity.this, getResources().getString(R.string.reg_error_is_use));
                                } else {
                                    SMSSDK.getVerificationCode("86", mobile.getText().toString());//发送请求验证码，手机10s之内会获得短信验证码
                                    phString = mobile.getText().toString();
                                    btn_card.setClickable(false);//不可点击
                                    MyTimer myTimer = new MyTimer(60000, 1000);
                                    myTimer.start();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            SMSSDK.getVerificationCode("86", mobile.getText().toString());//发送请求验证码，手机10s之内会获得短信验证码
                            phString = mobile.getText().toString();
                            btn_card.setClickable(false);//不可点击
                            MyTimer myTimer = new MyTimer(60000, 1000);
                            myTimer.start();
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
                        SMSSDK.getVerificationCode("86", mobile.getText().toString());//发送请求验证码，手机10s之内会获得短信验证码
                        phString = mobile.getText().toString();
                        btn_card.setClickable(false);//不可点击
                        MyTimer myTimer = new MyTimer(60000, 1000);
                        myTimer.start();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_emp_mobile", mobile.getText().toString());
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

    class MyTimer extends CountDownTimer {

        public MyTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btn_card.setText(res.getString(R.string.daojishi_three));
            btn_card.setClickable(true);//可点击
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btn_card.setText(res.getString(R.string.daojishi_one) + millisUntilFinished / 1000 + res.getString(R.string.daojishi_two));
        }
    }

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            Log.e("event", "event=" + event);
            if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result" + event);
                //短信注册成功后，返回MainActivity,然后提示新好友
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {//提交验证码成功
//                    Toast.makeText(getApplicationContext(), "提交验证码成功", Toast.LENGTH_SHORT).show();
                    reg();

                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    //已经验证
                    Toast.makeText(getApplicationContext(), R.string.code_msg_one, Toast.LENGTH_SHORT).show();
                }

            } else {
//				((Throwable) data).printStackTrace();
                Toast.makeText(RegOneActivity.this, R.string.code_msg_two, Toast.LENGTH_SHORT).show();
//					Toast.makeText(MainActivity.this, "123", Toast.LENGTH_SHORT).show();
                int status = 0;
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;

                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(RegOneActivity.this, des, Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    SMSLog.getInstance().w(e);
                }
            }
            if(progressDialog != null){
                progressDialog.dismiss();
            }
        }

        ;
    };

    public void onDestroy() {
        super.onPause();
        SMSSDK.unregisterAllEventHandler();
        //注销短信监听广播
        this.unregisterReceiver(mSMSBroadcastReceiver);
    }


}
