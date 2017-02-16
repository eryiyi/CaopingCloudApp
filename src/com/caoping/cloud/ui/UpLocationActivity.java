package com.caoping.cloud.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 上传坐标
 */
public class UpLocationActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private TextView right_btn;
    private TextView location;
    private TextView address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uplocation_activity);
        initView();
        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr) && !StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
            location.setText("经纬度："+(CaopingCloudApplication.latStr+CaopingCloudApplication.lngStr));
        }
        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress) ){
            address.setText("附近位置："+(CaopingCloudApplication.locationAddress));
        }
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        title = (TextView) this.findViewById(R.id.title);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        location = (TextView) this.findViewById(R.id.location);
        address = (TextView) this.findViewById(R.id.address);
        right_btn.setText("保存");
        right_btn.setOnClickListener(this);
        title.setText("上传公司坐标");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.right_btn:
            {
                //保存
                if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr) && !StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                    upLocation();
                }
            }
                break;
        }
    }

    private void upLocation() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_COMPANY_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(UpLocationActivity.this, "上传公司坐标成功！", Toast.LENGTH_SHORT).show();
                                    finish();
                                }else{
                                    showMsg(UpLocationActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(UpLocationActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("company_id", ""), String.class))){
                    params.put("company_id", getGson().fromJson(getSp().getString("company_id", ""), String.class));
                }
                params.put("lat_company", CaopingCloudApplication.latStr);
                params.put("lng_company", CaopingCloudApplication.lngStr);
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
