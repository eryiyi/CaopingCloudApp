package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
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
import com.caoping.cloud.util.Constants;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.PublishPopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhl on 2016/8/30.
 * 添加头条评论
 */
public class AddNewsCommentActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private TextView reply;
    private EditText content;
    private TextView right_btn;
    private String mm_msg_id;
    private String comment_fplid;//父评论ID
    private String comment_fplid_name;//父评论Name

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_news_comment_activity);
        mm_msg_id = getIntent().getExtras().getString("mm_msg_id");
        comment_fplid = getIntent().getExtras().getString("comment_fplid");
        comment_fplid_name = getIntent().getExtras().getString("comment_fplid_name");
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("添加评论");
        reply = (TextView) this.findViewById(R.id.reply);
        content = (EditText) this.findViewById(R.id.content);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setOnClickListener(this);
        right_btn.setText("提交");
        if(!StringUtil.isNullOrEmpty(comment_fplid_name)){
            reply.setText("回复："+comment_fplid_name);
            reply.setVisibility(View.VISIBLE);
        }else{
            reply.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.right_btn:
            {
                if(StringUtil.isNullOrEmpty(content.getText().toString())){
                    showMsg(AddNewsCommentActivity.this, "请输入评论内容！");
                    return;
                }
                if(content.getText().toString().length() > 500){
                    showMsg(AddNewsCommentActivity.this, "评论内容超出限制，最多500字！");
                    return;
                }
                addComment();
            }
                break;
        }
    }


    private void addComment() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_NEWS_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    showMsg(AddNewsCommentActivity.this, "添加评论成功！");
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent(Constants.SEND_COMMENT_SUCCESS);
                                    sendBroadcast(intent1);
                                    finish();

                                }else{
                                    showMsg(AddNewsCommentActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(AddNewsCommentActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddNewsCommentActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_id", mm_msg_id);
                params.put("comment_emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                if(!StringUtil.isNullOrEmpty(content.getText().toString())){
                    params.put("comment_content", content.getText().toString());
                }
                if(!StringUtil.isNullOrEmpty(comment_fplid)){
                    params.put("comment_fplid", comment_fplid);

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
            showSelectPublishDialog();
        }
        return super.onKeyDown(keyCode, event);
    }
    private PublishPopWindow publishPopWindow;
    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(AddNewsCommentActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(AddNewsCommentActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            publishPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.btn_sure:
                    finish();
                    break;
                default:
                    break;
            }
        }
    };



}
