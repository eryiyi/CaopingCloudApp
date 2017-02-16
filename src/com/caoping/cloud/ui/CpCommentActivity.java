package com.caoping.cloud.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.fragment.*;

/**
 * Created by zhl on 2016/8/30.
 * 订单评论：我点评的 评论我的
 */
public class CpCommentActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;

    private CpCommentOneFragment oneFragment;
    private CpCommentTwoFragment twoFragment;

//    private ImageView foot_one;
//    private ImageView foot_two;
//
    private TextView foot_one_text;
    private TextView foot_two_text;

    //设置底部图标
    Resources res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cp_comment_activity);
        initView();
        res = getResources();
        fm = getSupportFragmentManager();
        initView();
        switchFragment(R.id.foot_liner_one);
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("商品评论");

//        foot_one = (ImageView) this.findViewById(R.id.foot_one);
//        foot_two = (ImageView) this.findViewById(R.id.foot_two);
        this.findViewById(R.id.foot_liner_one).setOnClickListener(this);
        this.findViewById(R.id.foot_liner_two).setOnClickListener(this);

        foot_one_text = (TextView) this.findViewById(R.id.foot_one_text);
        foot_two_text = (TextView) this.findViewById(R.id.foot_two_text);

    }

    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_liner_one:
                if (oneFragment == null) {
                    oneFragment = new CpCommentOneFragment();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }
//                foot_one.setImageResource(R.drawable.nav_home);
//                foot_two.setImageResource(R.drawable.nav_shop);
//
                foot_one_text.setTextColor(res.getColor(R.color.green));
                foot_two_text.setTextColor(res.getColor(R.color.text_color));
                break;
            case R.id.foot_liner_two:
                if (twoFragment == null) {
                    twoFragment = new CpCommentTwoFragment();
                    fragmentTransaction.add(R.id.content_frame, twoFragment);
                } else {
                    fragmentTransaction.show(twoFragment);
                }
//                foot_one.setImageResource(R.drawable.nav_home_p);
//                foot_two.setImageResource(R.drawable.nav_shop_p);
//
                foot_one_text.setTextColor(res.getColor(R.color.text_color));
                foot_two_text.setTextColor(res.getColor(R.color.green));
                break;
        }
        fragmentTransaction.commit();
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
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            default:
            {
                switchFragment(view.getId());
            }
            break;
        }
    }
}
