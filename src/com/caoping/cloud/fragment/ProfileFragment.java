package com.caoping.cloud.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.adapter.ItemProfileBtnAdapter;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.ApplyGysData;
import com.caoping.cloud.data.MinePackageData;
import com.caoping.cloud.data.PayAmountObjData;
import com.caoping.cloud.entiy.ApplyGys;
import com.caoping.cloud.entiy.MinePackage;
import com.caoping.cloud.entiy.PayAmountObj;
import com.caoping.cloud.entiy.ProfileBtnObj;
import com.caoping.cloud.ui.*;
import com.caoping.cloud.upload.CommonUtil;
import com.caoping.cloud.util.CompressPhotoUtil;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.SelectPhoPopWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhl on 2016/10/16.
 * 我的
 */
public class ProfileFragment extends BaseFragment  implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private View view;
    private Resources res;

    private ImageView cover;//头像
    private RelativeLayout relate_name_one;//登录时显示
    private RelativeLayout relate_name_two;//未登录时显示
    private TextView name;//name
    private TextView mobile;//手机号
    private TextView company;//公司
    private TextView btn_login_reg;//d登录注册按钮

    private ImageView sm_icon_ver_pp;//实名认证
    private ImageView sm_icon_ver_com;//企业认证

    private ImageButton btn_tixian_shape;//提现
    private ImageButton btn_chongzhi_shape;//充值
    private TextView jineCount;//金额


    private String txpic = "";
    private SelectPhoPopWindow deleteWindow;
    private String pics = "";
    private static final File PHOTO_CACHE_DIR = new File(Environment.getExternalStorageDirectory() + "/liangxun/PhotoCache");

    private TextView profile_content;//亲爱的%s先生，您加入草坪云已%s天，\n消费%s元，收入%s元。
    private TextView profile_txt_two;//发布的
    private TextView profile_txt_three;//卖出的
    private TextView profile_txt_four;//买到的
    private TextView profile_txt_five;//知名度


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBoradcastReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.profile_fragment, null);
        res = getActivity().getResources();

        initView();
        initData();
        getPayAmount();
        return view;
    }

    private void initView() {
//        view.findViewById(R.id.btn_caigou).setOnClickListener(this);
//        view.findViewById(R.id.btn_gongying).setOnClickListener(this);
        cover = (ImageView) view.findViewById(R.id.cover);
        cover.setOnClickListener(this);
        relate_name_one = (RelativeLayout) view.findViewById(R.id.relate_name_one);
        relate_name_two = (RelativeLayout) view.findViewById(R.id.relate_name_two);
        name= (TextView) view.findViewById(R.id.name);
        mobile= (TextView) view.findViewById(R.id.mobile);
        company= (TextView) view.findViewById(R.id.company);
        btn_login_reg= (TextView) view.findViewById(R.id.btn_login_reg);
        sm_icon_ver_pp= (ImageView) view.findViewById(R.id.sm_icon_ver_pp);
        sm_icon_ver_com= (ImageView) view.findViewById(R.id.sm_icon_ver_com);

        view.findViewById(R.id.btn_edit).setOnClickListener(this);

        btn_tixian_shape = (ImageButton) view.findViewById(R.id.btn_tixian_shape);
        btn_chongzhi_shape = (ImageButton) view.findViewById(R.id.btn_chongzhi_shape);

        btn_tixian_shape.setOnClickListener(this);
        btn_chongzhi_shape.setOnClickListener(this);
        jineCount = (TextView) view.findViewById(R.id.jineCount);

        view.findViewById(R.id.btn_guanzhu).setOnClickListener(this);
        view.findViewById(R.id.btn_favour).setOnClickListener(this);
        view.findViewById(R.id.btn_news).setOnClickListener(this);
        view.findViewById(R.id.btn_wuliu).setOnClickListener(this);
        view.findViewById(R.id.btn_wuliu_t).setOnClickListener(this);

        view.findViewById(R.id.btn_caoyuan).setOnClickListener(this);
        view.findViewById(R.id.btn_company).setOnClickListener(this);
        view.findViewById(R.id.btn_comment).setOnClickListener(this);
        view.findViewById(R.id.btn_upload).setOnClickListener(this);
        view.findViewById(R.id.btn_renzheng).setOnClickListener(this);
        view.findViewById(R.id.btn_set).setOnClickListener(this);
        view.findViewById(R.id.btn_zhangdan).setOnClickListener(this);

        profile_content = (TextView) view.findViewById(R.id.profile_content);
        profile_txt_two = (TextView) view.findViewById(R.id.profile_txt_two);
        profile_txt_three = (TextView) view.findViewById(R.id.profile_txt_three);
        profile_txt_four = (TextView) view.findViewById(R.id.profile_txt_four);
        profile_txt_five = (TextView) view.findViewById(R.id.profile_txt_five);

        profile_txt_two.setOnClickListener(this);
        profile_txt_three.setOnClickListener(this);
        profile_txt_four.setOnClickListener(this);
        profile_txt_five.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_zhangdan:
            {
                //账单
                Intent intent = new Intent(getActivity(), MinePackageActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.cover:
            {
                //点击头像
                ShowPickDialog();
            }
                break;
            case R.id.profile_txt_four:
//            case R.id.btn_caigou:
            {
                //采购
                Intent intent = new Intent(getActivity(), MineOrdersActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.profile_txt_three:
//            case R.id.btn_gongying:
            {
                //销售
                Intent intent = new Intent(getActivity(), MineOrdersManagerActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_tixian_shape:
            {
                //提现
                Intent intent = new Intent(getActivity(), MineCountActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_chongzhi_shape:
            {
                //充值
                Intent intent = new Intent(getActivity(), BankCardCztxActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_edit:
            {
                Intent intent = new Intent(getActivity(), SetProfileActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_guanzhu:
            {
                Intent intent = new Intent(getActivity(), MineGuanzhuActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.btn_favour:
            {
                Intent intent = new Intent(getActivity(), MineFavourActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_news:
            {
                Intent intent = new Intent(getActivity(), ListMineNewsActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_wuliu:
            {
                Intent intent = new Intent(getActivity(), MineWuliuActivity.class);
                intent.putExtra("titleStr", "我是车主");
                intent.putExtra("is_type", "0");
                startActivity(intent);
            }
            break;
            case R.id.btn_wuliu_t:
            {
                //我是货主
                Intent intent = new Intent(getActivity(), MineWuliuActivity.class);
                intent.putExtra("titleStr", "我是货主");
                intent.putExtra("is_type", "1");
                startActivity(intent);
            }
            break;
            case R.id.btn_company:
            {
                //公司简介
                Intent intent = new Intent(getActivity(), CompanyDetailActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_caoyuan:
            {
                //发布记录
//
                Intent intent = new Intent(getActivity(), ListMineProductActivity.class);
                intent.putExtra("is_type", "0");
                startActivity(intent);
            }
            break;
            case R.id.btn_comment:
            {
                //评论我的
                Intent intent = new Intent(getActivity(), CpCommentActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.btn_upload:
            {
                //上传坐标
                if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("company_id", ""), String.class))){
                    Intent intent  = new Intent(getActivity(), UpLocationActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getActivity(), "请先完善您的公司简介！", Toast.LENGTH_SHORT).show();
                }
            }
            break;
            case R.id.btn_renzheng:
            {
                //
            }
            break;
            case R.id.btn_set:
            {
                Intent intent = new Intent(getActivity(), MineSettingActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.profile_txt_two:
            {
                getGys();
            }
                break;

            case R.id.profile_txt_five:
            {
                Intent intent = new Intent(getActivity(), MineFensiActivity.class);
                startActivity(intent);
            }
            break;
        }
    }

    private void getGys() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_APPLY_GYS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    ApplyGysData data = getGson().fromJson(s, ApplyGysData.class);
                                    if(data != null){
                                        ApplyGys applyGys = data.getData();
                                        if(applyGys !=null){
                                            if("1".equals(applyGys.getIs_check())){
                                                //说明申请个供应商通过了
                                                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                                intent.putExtra("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                                                startActivity(intent);
                                            }else if("0".equals(applyGys.getIs_check())){
                                                //申请了 还没审核
                                                Toast.makeText(getActivity(),  "您的申请已经提交，请等待管理员审核！", Toast.LENGTH_SHORT).show();
                                            }else if("2".equals(applyGys.getIs_check())){
                                                //说明申请 没通过
                                                Toast.makeText(getActivity(),  "您的申请没有通过，请联系客服！", Toast.LENGTH_SHORT).show();
                                            }else{
                                                //说明没申请
                                                Intent intent = new Intent(getActivity(), ApplyGysActivity.class);
                                                startActivity(intent);
                                            }
                                        }else{
                                            Intent intent = new Intent(getActivity(), ApplyGysActivity.class);
                                            startActivity(intent);
                                        }
                                    }else{
                                        Intent intent = new Intent(getActivity(), ApplyGysActivity.class);
                                        startActivity(intent);
                                    }
                                }else{
                                    Toast.makeText(getActivity(),  jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.get_data_error, Toast.LENGTH_SHORT).show();
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

    // 选择相册，相机
    private void ShowPickDialog() {
        deleteWindow = new SelectPhoPopWindow(getActivity(), itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(getActivity().findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent camera = new Intent(
                            MediaStore.ACTION_IMAGE_CAPTURE);
                    //下面这句指定调用相机拍照后的照片存储的路径
                    camera.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                            .fromFile(new File(Environment
                                    .getExternalStorageDirectory(),
                                    "ppCover.jpg")));
                    startActivityForResult(camera, 2);
                }
                break;
                case R.id.mapstorage: {
                    Intent mapstorage = new Intent(Intent.ACTION_PICK, null);
                    mapstorage.setDataAndType(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(mapstorage, 1);
                }
                break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // 如果是直接从相册获取
            case 1:
                if (data != null) {
                    startPhotoZoom(data.getData());
                }
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/ppCover.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(photo);
            if (photo != null) {
                pics = CompressPhotoUtil.saveBitmap2file(photo, System.currentTimeMillis() + ".jpg", PHOTO_CACHE_DIR);
                cover.setImageBitmap(photo);
                //保存头像
                sendFile(pics);
            }
        }
    }


    public void sendFile(final String pic) {
        File f = new File(pic);
        final Map<String, File> files = new HashMap<String, File>();
        files.put("file", f);
        Map<String, String> params = new HashMap<String, String>();
        CommonUtil.addPutUploadFileRequest(
                getActivity(),
                InternetURL.UPLOAD_FILE,
                files,
                params,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    String coverStr = jo.getString("data");
                                    sendCover(coverStr);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                null);
    }


    //修改头像
    private void sendCover(final String coverStr) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_INFO_COVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    save("empCover", InternetURL.INTERNAL+coverStr);
                                    //调用广播
//                                    Intent intent1 = new Intent("update_cover_success");
//                                    getActivity().sendBroadcast(intent1);
                                }else {
                                    Toast.makeText(getActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), R.string.add_failed, Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getActivity(), R.string.add_failed, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("empId", getGson().fromJson(getSp().getString("empId", ""), String.class));
                params.put("empCover", coverStr);
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
    //获得钱包
    public void getPackage(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.APP_GET_PACKAGE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            MinePackageData data = getGson().fromJson(s, MinePackageData.class);
                            if (data.getCode() == 200) {
                                MinePackage minePackage = data.getData();
                                if(minePackage != null){
                                    jineCount.setText("￥"+ minePackage.getPackage_money());
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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


    void initData(){
        //更新用户信息
        imageLoader.displayImage(getGson().fromJson(getSp().getString("empCover", ""), String.class), cover, CaopingCloudApplication.txOptions, animateFirstListener);
        name.setText(getGson().fromJson(getSp().getString("empName", ""), String.class));
        mobile.setText(getGson().fromJson(getSp().getString("empMobile", ""), String.class));
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("package_money", ""), String.class))){
            jineCount.setText("￥"+getGson().fromJson(getSp().getString("package_money", ""), String.class));
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_shiming_rz", ""), String.class))){
            if("0".equals(getGson().fromJson(getSp().getString("is_shiming_rz", ""), String.class))){
                //没有实名认证
                sm_icon_ver_pp.setImageDrawable(res.getDrawable(R.drawable.sm_icon_ver_pp_gray));
            }else{
                sm_icon_ver_pp.setImageDrawable(res.getDrawable(R.drawable.sm_icon_ver_pp));
            }
        }
        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_qiye_rz", ""), String.class))){
            if("0".equals(getGson().fromJson(getSp().getString("is_qiye_rz", ""), String.class))){
                //没有实名认证
                sm_icon_ver_com.setImageDrawable(res.getDrawable(R.drawable.sm_icon_ver_com_gray));
            }else{
                sm_icon_ver_com.setImageDrawable(res.getDrawable(R.drawable.sm_icon_ver_com));
            }
        }
//        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//            if("0".equals(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//                sm_v1.setImageDrawable(res.getDrawable(R.drawable.sm_v1_p));
//            }else{
//                sm_v1.setImageDrawable(res.getDrawable(R.drawable.sm_v1));
//            }
//        }
//        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//            if("0".equals(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//                sm_v2.setImageDrawable(res.getDrawable(R.drawable.sm_v2_p));
//            }else{
//                sm_v2.setImageDrawable(res.getDrawable(R.drawable.sm_v2));
//            }
//        }
//        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//            if("0".equals(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//                sm_v4.setImageDrawable(res.getDrawable(R.drawable.sm_v4_p));
//            }else{
//                sm_v4.setImageDrawable(res.getDrawable(R.drawable.sm_v4));
//            }
//        }
//        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//            if("0".equals(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//                sm_v5.setImageDrawable(res.getDrawable(R.drawable.sm_v5_p));
//            }else{
//                sm_v5.setImageDrawable(res.getDrawable(R.drawable.sm_v5));
//            }
//        }
//        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//            if("0".equals(getGson().fromJson(getSp().getString("is_vip_one", ""), String.class))){
//                sm_v6.setImageDrawable(res.getDrawable(R.drawable.sm_v6_p));
//            }else{
//                sm_v6.setImageDrawable(res.getDrawable(R.drawable.sm_v6));
//            }
//        }

        if(!StringUtil.isNullOrEmpty(getGson().fromJson(getSp().getString("company_name", ""), String.class))){
            company.setText(getGson().fromJson(getSp().getString("company_name", ""), String.class));
        }

    }


    PayAmountObj payAmountObj ;
    public void getPayAmount(){
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.getProfileMemberInfo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            PayAmountObjData data = getGson().fromJson(s, PayAmountObjData.class);
                            if (data.getCode() == 200) {
                                payAmountObj = data.getData();
                                if(payAmountObj != null){
                                    profile_content.setText("亲爱的"+getGson().fromJson(getSp().getString("empName", ""), String.class)
                                            +"先生，您加入草坪云已"+(payAmountObj.getRuzhuNumber()==null?"1":payAmountObj.getRuzhuNumber())+"天，消费"+(payAmountObj.getZhichuAmount()==null?"0":payAmountObj.getZhichuAmount())+"元，收入"+(payAmountObj.getShouruAmount()==null?"0":payAmountObj.getShouruAmount())+"元。");
                                    profile_txt_two.setText("我的草原"+(payAmountObj.getCpNumber()==null?"0":payAmountObj.getCpNumber()));
                                    profile_txt_three.setText("销售订单"+(payAmountObj.getGoodsCountTwo()==null?"0":payAmountObj.getGoodsCountTwo()));
                                    profile_txt_four.setText("采购订单"+(payAmountObj.getGoodsCountOne()==null?"0":payAmountObj.getGoodsCountOne()));
                                    profile_txt_five.setText("粉丝"+(payAmountObj.getFensiNumber()==null?"0":payAmountObj.getFensiNumber()));
                                }
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
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


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("update_cover_success")) {
                initData();
            }
            if(action.equals("update_location_success")){
                //更新当前城市
            }
            if(action.equals("update_mine_package_success")){
                //更新零钱
                getPackage();
            }
            if(action.equals("pay_chongzhi_success")){
                //更新零钱
                getPackage();
            }
        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("update_cover_success");
        myIntentFilter.addAction("update_location_success");//更新选择的城市
        myIntentFilter.addAction("update_mine_package_success");//更新零钱
        myIntentFilter.addAction("pay_chongzhi_success");//更新零钱
        //注册广播
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }
}
