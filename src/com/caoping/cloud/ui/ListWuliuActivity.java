package com.caoping.cloud.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.fragment.WuliuOneFragment;
import com.caoping.cloud.fragment.WuliuTwoFragment;
import com.caoping.cloud.util.HttpUtils;

/**
 * Created by zhl on 2016/8/30.
 * 物流信息
 */
public class ListWuliuActivity extends BaseActivity implements View.OnClickListener {
    private ImageView right_img;

    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private WuliuOneFragment oneFragment;
    private WuliuTwoFragment twoFragment;


    private TextView foot_one_text;
    private TextView foot_two_text;

    private ImageView foot_one;
    private ImageView foot_two;
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getResources();
        setContentView(R.layout.list_wuliu_activity);
        initView();
        fm = getSupportFragmentManager();
        switchFragment(R.id.foot_liner_one);
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        right_img = (ImageView) this.findViewById(R.id.right_img);
        right_img.setOnClickListener(this);

        foot_one = (ImageView) this.findViewById(R.id.foot_one);
        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        this.findViewById(R.id.foot_liner_one).setOnClickListener(this);
        this.findViewById(R.id.foot_liner_two).setOnClickListener(this);

        foot_one_text = (TextView) this.findViewById(R.id.foot_one_text);
        foot_two_text = (TextView) this.findViewById(R.id.foot_two_text);

    }


    public void switchFragment(int id) {
        if(id == R.id.back){
            finish();
        }else
        if(id == R.id.right_img){
            //发信息-找货 找车
            Intent intent = new Intent(ListWuliuActivity.this, PublishSelectWuliuTypeActivity.class);
            startActivity(intent);
        }else{
            fragmentTransaction = fm.beginTransaction();
            hideFragments(fragmentTransaction);
            switch (id) {
                case R.id.foot_liner_one:
                    if (twoFragment == null) {
                        twoFragment = new WuliuTwoFragment();
                        fragmentTransaction.add(R.id.content_frame, twoFragment);
                    } else {
                        fragmentTransaction.show(twoFragment);
                    }
                    foot_one.setImageResource(R.drawable.icon_car_p);
                    foot_two.setImageResource(R.drawable.icon_huo_n);

                    foot_one_text.setTextColor(res.getColor(R.color.green));
                    foot_two_text.setTextColor(res.getColor(R.color.text_color));
                    break;
                case R.id.foot_liner_two:
                    if (oneFragment == null) {
                        oneFragment = new WuliuOneFragment();
                        fragmentTransaction.add(R.id.content_frame, oneFragment);
                    } else {
                        fragmentTransaction.show(oneFragment);
                    }
                    foot_one.setImageResource(R.drawable.icon_car_n);
                    foot_two.setImageResource(R.drawable.icon_huo_p);

                    foot_one_text.setTextColor(res.getColor(R.color.text_color));
                    foot_two_text.setTextColor(res.getColor(R.color.green));

                    break;
            }
            fragmentTransaction.commit();
        }

    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
        if (twoFragment != null) {
            ft.hide(twoFragment);
        }
    }

    boolean isMobileNet, isWifiNet;

    @Override
    public void onClick(View v) {
        try {
            isMobileNet = HttpUtils.isMobileDataEnable(getApplicationContext());
            isWifiNet = HttpUtils.isWifiDataEnable(getApplicationContext());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(this, R.string.net_work_error, Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        switch (v.getId()){
            case R.id.back:
                finish();
                break;
        }
        switchFragment(v.getId());
    }

}
