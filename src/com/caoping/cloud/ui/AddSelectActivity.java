package com.caoping.cloud.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;

/**
 * Created by zhl on 2016/8/30.
 */
public class AddSelectActivity extends BaseActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.add_select_activity);
        initView();
    }

    private void initView() {
        this.findViewById(R.id.liner1).setOnClickListener(this);
        this.findViewById(R.id.liner2).setOnClickListener(this);
        this.findViewById(R.id.liner3).setOnClickListener(this);
        this.findViewById(R.id.liner4).setOnClickListener(this);
        this.findViewById(R.id.liner5).setOnClickListener(this);
        this.findViewById(R.id.liner6).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.liner1:
            {
                Intent product = new Intent(AddSelectActivity.this, AddProductActivity.class);
                startActivity(product);
                finish();
            }
                break;
            case R.id.liner2:
            {
                Intent cz = new Intent(AddSelectActivity.this, AddCaozhongActivity.class);
                startActivity(cz);
                finish();
            }
            break;
            case R.id.liner3:
            {
                Intent cz = new Intent(AddSelectActivity.this, AddJixieActivity.class);
                startActivity(cz);
                finish();
            }
            break;
            case R.id.liner4:
            {
                Intent cz = new Intent(AddSelectActivity.this, PublishRecordActivity.class);
                startActivity(cz);
                finish();
            }
            break;
            case R.id.liner5:
            {
                Intent cz = new Intent(AddSelectActivity.this, PublishCarActivity.class);
                startActivity(cz);
                finish();
            }
            break;
            case R.id.liner6:
            {
                Intent cz = new Intent(AddSelectActivity.this, PublishCarGoodsActivity.class);
                startActivity(cz);
                finish();
            }
            break;

        }
    }

    public void cancelBack(View view){
        finish();
    }
}
