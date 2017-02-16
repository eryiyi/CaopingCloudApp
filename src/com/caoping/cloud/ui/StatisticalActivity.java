package com.caoping.cloud.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemIndexAdapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.CpuseData;
import com.caoping.cloud.data.StatisticalObjData;
import com.caoping.cloud.entiy.StatisticalObj;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.PictureGridview;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 */
public class StatisticalActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    StatisticalObj statisticalObj ;

    private PictureGridview gridView;
    private ItemIndexAdapter adapter;
    private List<String> lists = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistical_activity);
        initView();
        getStatical();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("草坪数据");
        gridView = (PictureGridview) this.findViewById(R.id.gridView);
        adapter = new ItemIndexAdapter(lists, StatisticalActivity.this);
        gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(lists.size() > position){
                    String str = lists.get(position);
                    if(!StringUtil.isNullOrEmpty(str)){
                        //说明不是空
                        switch (position){
                            case 0:
                            {
                                //草坪
                                if("0".equals(str) || StringUtil.isNullOrEmpty(str)){
                                    showMsg(StatisticalActivity.this, "暂无数据");
                                }else{
                                    Intent intent = new Intent(StatisticalActivity.this, ListMineProductActivity.class);
                                    intent.putExtra("is_type", "0");
                                    startActivity(intent);
                                }
                            }
                            break;
                            case 1:
                            {
                                //机械
                                if("0".equals(str) || StringUtil.isNullOrEmpty(str)){
                                    showMsg(StatisticalActivity.this, "暂无数据");
                                }else{
                                    Intent intent = new Intent(StatisticalActivity.this, ListMineProductActivity.class);
                                    intent.putExtra("is_type", "1");
                                    startActivity(intent);
                                }
                            }
                            break;
                            case 2:
                            {
                                //草种
                                if("0".equals(str) || StringUtil.isNullOrEmpty(str)){
                                    showMsg(StatisticalActivity.this, "暂无数据");
                                }else{
                                    Intent intent = new Intent(StatisticalActivity.this, ListMineProductActivity.class);
                                    intent.putExtra("is_type", "2");
                                    startActivity(intent);
                                }
                            }
                            break;
                            case 3:
                            {
                                //物流
                                if("0".equals(str) || StringUtil.isNullOrEmpty(str)){
                                    showMsg(StatisticalActivity.this, "暂无数据");
                                }else{
                                    Intent intent = new Intent(StatisticalActivity.this, SelectWuliuTypeActivity.class);
                                    startActivity(intent);
                                }
                            }
                            break;
                            case 4:
                            {
                                //新闻
                                Intent intent = new Intent(StatisticalActivity.this, ListMineNewsActivity.class);
                                startActivity(intent);
                            }
                            break;
                            case 5:
                            {
                                //订单
                                Intent intent = new Intent(StatisticalActivity.this, MineOrdersActivity.class);
                                startActivity(intent);
                            }
                            break;
                            case 6:
                            {
                                //粉丝
                                Intent intent = new Intent(StatisticalActivity.this, MineFensiActivity.class);
                                startActivity(intent);
                            }
                            break;
                            case 7:
                            {
                                //名企排行
                                Intent intent = new Intent(StatisticalActivity.this, ListMingqiActivity.class);
                                startActivity(intent);
                            }
                            break;
                        }
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

    private void getStatical() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.appGetStatistical,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    StatisticalObjData data = getGson().fromJson(s, StatisticalObjData.class);
                                    if(data != null){
                                        statisticalObj = data.getData();
                                        if(statisticalObj != null){
                                            lists.add(statisticalObj.getCpNumber());
                                            lists.add(statisticalObj.getJxNumber());
                                            lists.add(statisticalObj.getCzNumber());
                                            lists.add(statisticalObj.getWlNumber());
                                            lists.add(statisticalObj.getNewsNumber());
                                            lists.add(statisticalObj.getOrderNumber());
                                            lists.add(statisticalObj.getFavourNumber());
                                            lists.add(statisticalObj.getMqNumber());
                                        }
                                        adapter.notifyDataSetChanged();
                                    }
                                }else {
                                    Toast.makeText(StatisticalActivity.this, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(StatisticalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StatisticalActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
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
