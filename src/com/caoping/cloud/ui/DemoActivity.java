package com.caoping.cloud.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;

/**
 * Created by zhl on 2016/8/30.
 */
public class DemoActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_activity);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("设置");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
        }
    }
}
