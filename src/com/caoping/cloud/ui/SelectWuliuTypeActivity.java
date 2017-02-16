package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;

/**
 * Created by zhl on 2016/8/30.
 * 选择货主车主
 */
public class SelectWuliuTypeActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_select_wuliu_type_activity);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("选择您的身份");

        this.findViewById(R.id.liner_one).setOnClickListener(this);
        this.findViewById(R.id.liner_two).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.liner_one:
            {
                Intent intent = new Intent(SelectWuliuTypeActivity.this, MineWuliuActivity.class);
                intent.putExtra("titleStr", "我是车主");
                intent.putExtra("is_type", "0");
                startActivity(intent);
                finish();
            }
             break;
            case R.id.liner_two:
            {
                Intent intent = new Intent(SelectWuliuTypeActivity.this, MineWuliuActivity.class);
                intent.putExtra("titleStr", "我是货主");
                intent.putExtra("is_type", "1");
                startActivity(intent);
                finish();
            }
            break;
        }
    }
}
