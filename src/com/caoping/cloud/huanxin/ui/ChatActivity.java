package com.caoping.cloud.huanxin.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.EmpsData;
import com.caoping.cloud.db.DBHelper;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.huanxin.runtimepermissions.PermissionsManager;
import com.caoping.cloud.util.StringUtil;
import com.hyphenate.easeui.ui.EaseChatFragment;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class ChatActivity extends BaseActivity {
    public static ChatActivity activityInstance;
    private EaseChatFragment chatFragment;
    public String toChatUsername;
    public static String userName;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.em_activity_chat);
        activityInstance = this;
        //get user id or group id
        toChatUsername = getIntent().getExtras().getString("userId");
        userName = getIntent().getExtras().getString("userName");
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

        //判断toChatUsername是否存在数据库了
        if(!StringUtil.isNullOrEmpty(toChatUsername)){
            Member member = DBHelper.getInstance(ChatActivity.this).getMemberId(toChatUsername);
            if(member != null){

            }else{
                //去取数据\
                getNickNamesByHxUserNames(toChatUsername);
            }
        }
    }

    //获得好友资料
    List<Member> emps = new ArrayList<Member>();
    private void getNickNamesByHxUserNames(final String hxUserNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpsData data = getGson().fromJson(s, EmpsData.class);
                                    //保存到数据库
                                    if (data != null && data.getData() != null) {
                                        for (Member recordMsg : data.getData()) {
                                            Member recordMsgLocal = DBHelper.getInstance(ChatActivity.this).getMemberId(recordMsg.getEmpId());
                                            if (recordMsgLocal != null) {
                                                //已经存在了 不需要插入了
                                            } else {
                                                DBHelper.getInstance(ChatActivity.this).saveMember(recordMsg);
                                            }
                                        }
                                    }
                                }else {
                                    Toast.makeText(ChatActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(ChatActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ChatActivity.this, "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", hxUserNames);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(activityInstance != null){
            activityInstance = null;
        }
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
    	// make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
    
    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
//        if (EasyUtils.isSingleActivity(this)) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//        }
    }
    
    public String getToChatUsername(){
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
        @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
