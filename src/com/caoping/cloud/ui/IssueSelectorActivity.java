package com.caoping.cloud.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.caoping.cloud.R;
import com.caoping.cloud.base.BaseActivity;

public class IssueSelectorActivity extends BaseActivity {
    private Handler mHandler;
    private ImageView fabu_top_img;

    private LayoutInflater layoutInflater;
    private DisplayMetrics dm;

    private View erjiView;
    private PopupWindow fabuErjiPopup;

    private View yijiView;
    private PopupWindow fabuYijiPopup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_issue_selector);
        fabu_top_img = (ImageView) findViewById(R.id.fabu_top_img);
        layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 300);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            showYijiFabu(fabu_top_img);
        }
    };


    private void showYijiFabu(View parent) {
        yijiView = layoutInflater.inflate(R.layout.fabu_popwindow_layout, null);
        // 创建一个PopuWidow对象
        if (fabuYijiPopup == null) {
            fabuYijiPopup = new PopupWindow(yijiView, dm.widthPixels,
                    LinearLayout.LayoutParams.MATCH_PARENT);
        }
        fabuYijiPopup.setAnimationStyle(R.style.popWindow_animation_in2out);

        fabuYijiPopup.setFocusable(false);
        // 设置允许在外点击消失
        // fabuYijiPopup.setOutsideTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        // fabuYijiPopup.setBackgroundDrawable(new BitmapDrawable());
        // PopupWindow的显示及位置设置
        fabuYijiPopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
        yijiView.findViewById(R.id.fabu_popup_dongtai).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent product = new Intent(IssueSelectorActivity.this, AddProductActivity.class);
                        startActivity(product);
                        finish();
                    }
                });
        yijiView.findViewById(R.id.fabu_popup_zijin).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cz = new Intent(IssueSelectorActivity.this, AddCaozhongActivity.class);
                        startActivity(cz);
                        finish();
                    }
                });
        yijiView.findViewById(R.id.fabu_popup_shebei).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent cz = new Intent(IssueSelectorActivity.this, AddJixieActivity.class);
                        startActivity(cz);
                        finish();
                    }
                });
        yijiView.findViewById(R.id.fabu_popup_juben).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //toutiao
                        Intent cz = new Intent(IssueSelectorActivity.this, PublishRecordActivity.class);
                        startActivity(cz);
                        finish();
                    }
                });
        yijiView.findViewById(R.id.fabu_popup_rencai).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //che
                        Intent cz = new Intent(IssueSelectorActivity.this, PublishCarActivity.class);
                        startActivity(cz);
                        finish();
                    }
                });
        yijiView.findViewById(R.id.fabu_popup_changdi).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //huo
                        Intent cz = new Intent(IssueSelectorActivity.this, PublishCarGoodsActivity.class);
                        startActivity(cz);
                        finish();
                    }
                });
//        yijiView.findViewById(R.id.fabu_popup_juben).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fabuYijiPopup.dismiss();
//                        showErjiFabu(v);
//                    }
//                });
//        yijiView.findViewById(R.id.fabu_popup_rencai).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fabuYijiPopup.dismiss();
//                        showErjiFabu(v);
//                    }
//                });
//        yijiView.findViewById(R.id.fabu_popup_shebei).setOnClickListener(
//                new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        fabuYijiPopup.dismiss();
//                        showErjiFabu(v);
//                    }
//                });
        yijiView.findViewById(R.id.fabu_popup_close_img).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.fade_in,
                                R.anim.fade_out);
                    }
                });
    }

    private void showErjiFabu(final View parent) {
        switch (parent.getId()) {
//            case R.id.fabu_popup_changdi:
//                erjiView = layoutInflater
//                        .inflate(R.layout.fabu_changdi_popup, null);
//                break;
//            case R.id.fabu_popup_juben:
//                erjiView = layoutInflater.inflate(R.layout.fabu_juben_popup, null);
//                break;
//            case R.id.fabu_popup_rencai:
//                erjiView = layoutInflater.inflate(R.layout.fabu_rencai_popup, null);
//                break;
//            case R.id.fabu_popup_shebei:
//                erjiView = layoutInflater.inflate(R.layout.fabu_shebei_popup, null);
//                break;
        }
        // 创建一个PopuWidow对象
        fabuErjiPopup = new PopupWindow(erjiView, dm.widthPixels,
                LinearLayout.LayoutParams.MATCH_PARENT);
        fabuErjiPopup.setAnimationStyle(R.style.popWindow_animation_in2out);
        fabuErjiPopup.setFocusable(false);
        // 设置允许在外点击消失
        // fabuErjiPopup.setOutsideTouchable(true);
        // 设置背景，这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
        // fabuErjiPopup.setBackgroundDrawable(new BitmapDrawable());
        // PopupWindow的显示及位置设置
        fabuErjiPopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
        erjiView.findViewById(R.id.fabu_erji_fanhui).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fabuErjiPopup.dismiss();
                        showYijiFabu(v);
                    }
                });
        erjiView.findViewById(R.id.fabu_erji_quxiao).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(R.anim.fade_in,
                                R.anim.fade_out);
                    }
                });
        erjiView.findViewById(R.id.fabu_erji_qiu).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        switch (parent.getId()) {
                            case R.id.fabu_popup_changdi:
                                break;
                            case R.id.fabu_popup_juben:
                                break;
                            case R.id.fabu_popup_rencai:
                                break;
                            case R.id.fabu_popup_shebei:
                                break;
                        }
                    }
                });
        erjiView.findViewById(R.id.fabu_erji_chu).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        switch (parent.getId()) {
                            case R.id.fabu_popup_changdi:
                                break;
                            case R.id.fabu_popup_juben:
                                break;
                            case R.id.fabu_popup_rencai:
                                break;
                            case R.id.fabu_popup_shebei:
                                break;
                        }
                    }
                });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (fabuYijiPopup != null && fabuYijiPopup.isShowing()) {
                fabuYijiPopup.dismiss();
            }
            if (fabuErjiPopup != null && fabuErjiPopup.isShowing()) {
                fabuErjiPopup.dismiss();
            }
            finish();
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        return true;
    }

    ;
}
