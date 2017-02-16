package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.base.ActivityTack;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.VersionUpdateObjData;
import com.caoping.cloud.entiy.VersionUpdateObj;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.CustomProgressDialog;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 系统设置
 */
public class MineSettingActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_setting_activity);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("系统设置");

        this.findViewById(R.id.set_about_us).setOnClickListener(this);
        this.findViewById(R.id.btn_help).setOnClickListener(this);
        this.findViewById(R.id.btn_check_version).setOnClickListener(this);
        this.findViewById(R.id.btn_ziliao).setOnClickListener(this);
        this.findViewById(R.id.btn_pwr).setOnClickListener(this);
        this.findViewById(R.id.mine_address).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ziliao:
            {
                //个人资料
                Intent intent = new Intent(MineSettingActivity.this, SetProfileActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_pwr:
            {
                //登录密码
                Intent intent = new Intent(MineSettingActivity.this, UpdateLoginPwrActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.back:
                finish();
                break;
            case R.id.set_about_us:
            {
                Intent intent  = new Intent(MineSettingActivity.this, AboutUsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_help:
            {
                Intent intent  = new Intent(MineSettingActivity.this, RegistMsgActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_check_version:
            {
                //版本更新
                Resources res = getBaseContext().getResources();
                String message = res.getString(R.string.check_new_version).toString();
                progressDialog = new CustomProgressDialog(MineSettingActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                initData();
            }
                break;
            case R.id.mine_address:
            {
                //收货地址
                Intent intent  = new Intent(MineSettingActivity.this, MineAddressActivity.class);
                startActivity(intent);
            }
                break;
        }
    }

    public void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.CHECK_VERSION_CODE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code1 = jo.getString("code");
                                if (Integer.parseInt(code1) == 200) {
                                    VersionUpdateObjData data = getGson().fromJson(s, VersionUpdateObjData.class);
                                    VersionUpdateObj versionUpdateObj = data.getData();
                                    if("true".equals(versionUpdateObj.getFlag())){
                                        //更新
                                        final Uri uri = Uri.parse(versionUpdateObj.getDurl());
                                        final Intent it = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(it);
                                    }else{
                                        showMsg(MineSettingActivity.this, "已是最新版本");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MineSettingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineSettingActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_version_code", getV());
                params.put("mm_version_package", "com.caoping.cloud");
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

    String getV(){
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }


    public void quiteAction(View view){
        //退出
        save("empPass", "");
        ActivityTack.getInstanse().exit(MineSettingActivity.this);
    }
}
