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
import com.caoping.cloud.adapter.ItemProvinceAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.ProvinceData;
import com.caoping.cloud.entiy.Province;
import com.caoping.cloud.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/9.
 * 新增我的收货地址--选择省份
 */
public class MineAddressAddProvinceActivity extends BaseActivity implements View.OnClickListener {

    private TextView title;

    private ListView lstv;
    private ItemProvinceAdapter adapter;
    List<Province> provinces = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_address_add_activity);
        initView();
        getData();
    }
    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("新增收获地址");

        adapter = new ItemProvinceAdapter( MineAddressAddProvinceActivity.this, provinces);
        lstv = (ListView) this.findViewById(R.id.lstv);
        lstv.setAdapter(adapter);
        lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Province province = provinces.get(position);
                Intent intent = new Intent(MineAddressAddProvinceActivity.this, MineAddressAddCityActivity.class);
                intent.putExtra("provinceId", province.getProvinceId());
                intent.putExtra("provinceName", province.getPname());
                startActivity(intent);
                finish();
            }
        });
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
        }
    }

    //获取省份
    public void getData(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SELECT_PROVINCE_ADDRESS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ProvinceData data = getGson().fromJson(s, ProvinceData.class);
                            if (data.getCode() == 200) {
                                provinces.addAll(data.getData()) ;
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineAddressAddProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineAddressAddProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(MineAddressAddProvinceActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
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
