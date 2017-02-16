package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemCarLengthAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CarLengthData;
import com.caoping.cloud.entiy.CarLength;
import com.caoping.cloud.util.StringUtil;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 选择车长
 */
public class SelectCarLengthActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private ListView lstv;
    private ItemCarLengthAdapter adapter;
    private List<CarLength> listGuiges = new ArrayList<CarLength>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_cp_type_activity);
        initView();
        getGuige();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("选择车长");
        lstv = (ListView) this.findViewById(R.id.lstv);
        adapter = new ItemCarLengthAdapter(SelectCarLengthActivity.this, listGuiges);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(listGuiges.size() > i){
                    CarLength cptype = listGuiges.get(i);
                    if(cptype != null){
                        Intent mIntent = new Intent();
                        mIntent.putExtra("car_length_id", cptype.getCar_length_id());
                        mIntent.putExtra("car_length_name", cptype.getCar_length_name());
                        // 设置结果，并进行传送
                        setResult(1001, mIntent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }


    private void getGuige() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_CAR_LENGTH_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    CarLengthData data = getGson().fromJson(s, CarLengthData.class);
                                    listGuiges.clear();
                                    listGuiges.addAll(data.getData());
                                    adapter.notifyDataSetChanged();
                                }else{
                                    showMsg(SelectCarLengthActivity.this, jo.getString("message"));
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
                        Toast.makeText(SelectCarLengthActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
