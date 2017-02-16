package com.caoping.cloud.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.huanxin.ui.ConversationListFragment;

/**
 * Created by zhl on 2016/8/30.
 */
public class MsgActivity extends BaseActivity implements View.OnClickListener {
    private TextView title;
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fm;
    Resources res;
    private ConversationListFragment oneFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg_activity);
        initView();

        res = getResources();
        fm = getSupportFragmentManager();

        switchFragment(R.id.foot_liner_one);
    }

    public void switchFragment(int id) {
        fragmentTransaction = fm.beginTransaction();
        hideFragments(fragmentTransaction);
        switch (id) {
            case R.id.foot_liner_one:
                if (oneFragment == null) {
                    oneFragment = new ConversationListFragment();
                    fragmentTransaction.add(R.id.content_frame, oneFragment);
                } else {
                    fragmentTransaction.show(oneFragment);
                }

                break;

        }
        fragmentTransaction.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (oneFragment != null) {
            ft.hide(oneFragment);
        }
    }

    boolean isMobileNet, isWifiNet;

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.GONE);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("消息");
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
