package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 通过手机号找回密码
 */
public class FindPwrMobileTwoActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private EditText pwr_two;
    private EditText pwr_three;

    private String mobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_pwr_two_activity);
        mobile = getIntent().getExtras().getString("mobile");

        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("找回密码");
        pwr_two = (EditText) this.findViewById(R.id.pwr_two);
        pwr_three = (EditText) this.findViewById(R.id.pwr_three);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }

    public void updateLoginPwrAction(View view){

        if(StringUtil.isNullOrEmpty(pwr_two.getText().toString())){
            showMsg(FindPwrMobileTwoActivity.this, "请输入新密码！");
            return;
        }
        if(pwr_two.getText().toString().length() >18 || pwr_two.getText().toString().length() < 6){
            showMsg(FindPwrMobileTwoActivity.this, "请输入6到18位密码！");
            return;
        }
        if(StringUtil.isNullOrEmpty(pwr_three.getText().toString())){
            showMsg(FindPwrMobileTwoActivity.this, "请输入确认密码！");
            return;
        }
        if(!pwr_three.getText().toString().equals(pwr_two.getText().toString())){
            showMsg(FindPwrMobileTwoActivity.this, "两次输入密码不一致！！");
            return;
        }

        progressDialog = new CustomProgressDialog(FindPwrMobileTwoActivity.this, "正在加载中",R.anim.custom_dialog_frame);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.show();
        updatePwr();
    }

    private void updatePwr() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_PWR_MOBILE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    showMsg(FindPwrMobileTwoActivity.this, "找回密码成功，请重新登录！");
                                    save("empPass", pwr_two.getText().toString());
                                    Intent intent = new Intent(FindPwrMobileTwoActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                }else {
                                    showMsg(FindPwrMobileTwoActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(FindPwrMobileTwoActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(FindPwrMobileTwoActivity.this, R.string.add_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_mobile", mobile);
                params.put("rePass",pwr_two.getText().toString());
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
