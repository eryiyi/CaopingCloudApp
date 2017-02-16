package com.caoping.cloud.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.Publish_mood_GridView_Adapter;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.NewsObjSingleData;
import com.caoping.cloud.upload.CommonUtil;
import com.caoping.cloud.util.*;
import com.caoping.cloud.widget.CustomProgressDialog;
import com.caoping.cloud.widget.NoScrollGridView;
import com.caoping.cloud.widget.PublishPopWindow;
import com.caoping.cloud.widget.SelectPhoPopWindow;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: ${zhanghailong}
 * Date: 2015/2/4
 * Time: 19:42
 * 类的功能、说明写在此处.
 */
public class PublishRecordActivity extends BaseActivity implements View.OnClickListener {
    private final static int SELECT_LOCAL_PHOTO = 110;

    private NoScrollGridView publish_moopd_gridview_image;//图片
    private Publish_mood_GridView_Adapter adapter;
    private TextView publish_pic_run;
    private TextView title;
    private ImageView add_pic;//添加图片按钮

    private PublishPopWindow publishPopWindow;

    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private static int REQUEST_CODE = 1;

    private Uri uri;
    private String mm_emp_id = "";//当前登陆者UUID


    private SelectPhoPopWindow deleteWindow;
//    AsyncHttpClient client = new AsyncHttpClient();

    private EditText msg_title;
    private EditText et_sendmessage;
    private CheckBox checkbox;
    private TextView address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.publish_pic_xml);
        initView();
        mm_emp_id = getGson().fromJson(getSp().getString("empId", ""), String.class);
        title.setText("发头条");
    }

    private void initView() {
        msg_title = (EditText) this.findViewById(R.id.msg_title);
        et_sendmessage = (EditText) this.findViewById(R.id.face_content);
       this.findViewById(R.id.back).setOnClickListener(this);
        publish_pic_run = (TextView) this.findViewById(R.id.publish_pic_run);
        publish_pic_run.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        publish_moopd_gridview_image = (NoScrollGridView) this.findViewById(R.id.publish_moopd_gridview_image);
        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        publish_moopd_gridview_image.setAdapter(adapter);
        publish_moopd_gridview_image.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.contains("camera_default") && position == dataList.size() - 1 && dataList.size() - 1 != 9) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(PublishRecordActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
        add_pic = (ImageView) this.findViewById(R.id.add_pic);
        add_pic.setOnClickListener(this);
        checkbox = (CheckBox) this.findViewById(R.id.checkbox);
        address = (TextView) this.findViewById(R.id.address);
        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress)){
            address.setText(CaopingCloudApplication.locationAddress);
        }else {
            address.setText("暂无定位位置");
        }
        address.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (!TextUtils.isEmpty(et_sendmessage.getText().toString().trim())|| dataList.size()!=0) {   //这里trim()作用是去掉首位空格，防止不必要的错误
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(et_sendmessage.getWindowToken(), 0); //强制隐藏键盘
                    showSelectPublishDialog();
                } else {
                    finish();
                }
                break;
            case R.id.publish_pic_run:
                uploadPaths.clear();
                if(StringUtil.isNullOrEmpty(msg_title.getText().toString())){
                    Toast.makeText(this, "请输入标题！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(msg_title.getText().toString().length() > 100){
                    Toast.makeText(this, "标题超出字段限制，最多100字", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isNullOrEmpty(et_sendmessage.getText().toString())) {
                    Toast.makeText(this, "请输入内容！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (et_sendmessage.getText().toString().length() > 2000){
                    Toast.makeText(this, "内容超出字段限制，最多2000字", Toast.LENGTH_SHORT).show();
                    return;
                }
                progressDialog = new CustomProgressDialog(PublishRecordActivity.this, "正在加载中",R.anim.custom_dialog_frame);
                progressDialog.setCancelable(true);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                //检查有没有选择图片
                if (dataList.size() == 0) {
                    publishMood();
                    return;
                } else {
                    for (int i = 0; i < dataList.size(); i++) {
                        File f = new File(dataList.get(i));
                        final Map<String, File> files = new HashMap<String, File>();
                        files.put("file", f);
                        Map<String, String> params = new HashMap<String, String>();
                        CommonUtil.addPutUploadFileRequest(
                                PublishRecordActivity.this,
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
                                                    uploadPaths.add(coverStr);
                                                    if (uploadPaths.size() == dataList.size()) {
                                                        publishAll();
                                                    }
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
                        //七牛
//                        Bitmap bm = FileUtils.getSmallBitmap(dataList.get(i));
//                        final String cameraImagePath = FileUtils.saveBitToSD(bm, System.currentTimeMillis() + ".jpg");
//                        Map<String,String> map = new HashMap<String, String>();
//                        map.put("space", InternetURL.QINIU_SPACE);
//                        RequestParams params = new RequestParams(map);
//                        client.get(InternetURL.UPLOAD_TOKEN ,params, new JsonHttpResponseHandler(){
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                super.onSuccess(statusCode, headers, response);
//                                try {
//                                    String token = response.getString("data");
//                                    UploadManager uploadManager = new UploadManager();
//                                    uploadManager.put(StringUtil.getBytes(cameraImagePath), StringUtil.getUUID(), token,
//                                            new UpCompletionHandler() {
//                                                @Override
//                                                public void complete(String key, ResponseInfo info, JSONObject response) {
//                                                    //key
//                                                    uploadPaths.add(key);
//                                                    if (uploadPaths.size() == dataList.size()) {
//                                                        publishAll();
//                                                    }
//                                                }
//                                            }, null);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                                super.onFailure(statusCode, headers, throwable, errorResponse);
//                            }
//                        });
                    }
                }
                break;
            case R.id.add_pic://打开选择框 ，相机还是相册
                showSelectImageDialog();
                break;
            case R.id.address:
            {
                //选择附近位置
//                todo
            }
                break;
        }
    }

    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(PublishRecordActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(PublishRecordActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { //监控/拦截/屏蔽返回键
//            if (!TextUtils.isEmpty(et_sendmessage.getText().toString().trim())|| dataList.size()!=0) {
                showSelectPublishDialog();
                return true;
//            } else {
//                finish();
//            }

        }
        return super.onKeyDown(keyCode, event);
    }

    //发布
    private void publishMood() {
        final String contentStr = et_sendmessage.getText().toString();
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    NewsObjSingleData data = getGson().fromJson(s, NewsObjSingleData.class);
                                    Toast.makeText(PublishRecordActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                    intent1.putExtra("addRecord", data.getData());
                                    sendBroadcast(intent1);
                                    finish();
                                }else{
                                    showMsg(PublishRecordActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(PublishRecordActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_type", "0");
                if(!StringUtil.isNullOrEmpty( msg_title.getText().toString())){
                    params.put("mm_msg_title", msg_title.getText().toString());
                }else {
                    params.put("mm_msg_title", "");
                }
                if(!StringUtil.isNullOrEmpty(contentStr)){
                    params.put("mm_msg_content", contentStr);
                }else{
                    params.put("mm_msg_content", "");
                }
                params.put("emp_id", mm_emp_id);
                if(checkbox.isChecked()){
                    //选择了地址
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                        params.put("lat", CaopingCloudApplication.latStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                        params.put("lng", CaopingCloudApplication.lngStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress)){
                        params.put("location", CaopingCloudApplication.locationAddress);
                    }
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

    //上传完图片后开始发布
    private void publishAll() {
        final String contentStr = et_sendmessage.getText().toString();
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_NEWS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    NewsObjSingleData data = getGson().fromJson(s, NewsObjSingleData.class);
                                    Toast.makeText(PublishRecordActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent(Constants.SEND_INDEX_SUCCESS);
                                    intent1.putExtra("addRecord", data.getData());
                                    sendBroadcast(intent1);
                                    finish();
                                }else{
                                    showMsg(PublishRecordActivity.this, jo.getString("message"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                        Toast.makeText(PublishRecordActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mm_msg_picurl", String.valueOf(filePath));
                params.put("mm_msg_type", "0");
                if(!StringUtil.isNullOrEmpty( msg_title.getText().toString())){
                    params.put("mm_msg_title", msg_title.getText().toString());
                }else {
                    params.put("mm_msg_title", "");
                }
                if(!StringUtil.isNullOrEmpty(contentStr)){
                    params.put("mm_msg_content", contentStr);
                }else{
                    params.put("mm_msg_content", "");
                }
                params.put("emp_id", mm_emp_id);
                if(checkbox.isChecked()){
                    //选择了地址
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                        params.put("lat", CaopingCloudApplication.latStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                        params.put("lng", CaopingCloudApplication.lngStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress)){
                        params.put("location", CaopingCloudApplication.locationAddress);
                    }
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

    // 选择相册，相机
    private void showSelectImageDialog() {
        deleteWindow = new SelectPhoPopWindow(PublishRecordActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(PublishRecordActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private void openCamera() {
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
        // 根据文件地址创建文件
        File file = new File(CommonDefine.FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
        uri = Uri.fromFile(file);
        // 设置系统相机拍摄照片完成后图片文件的存放地址
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        // 开启系统拍照的Activity
        startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
    }

    private ArrayList<String> getIntentArrayList(ArrayList<String> dataList) {

        ArrayList<String> tDataList = new ArrayList<String>();

        for (String s : dataList) {
//            if (!s.contains("camera_default")) {
            tDataList.add(s);
//            }
        }
        return tDataList;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case SELECT_LOCAL_PHOTO:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
//                        if (dataList.size() < 9) {
//                            dataList.add("camera_default");
//                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        finish();
                    }
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_CAMERA:
                    String sdStatus = Environment.getExternalStorageState();
                    if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
                        return;
                    }
                    Bitmap bitmap = ImageUtils.getUriBitmap(this, uri, 400, 400);
                    String cameraImagePath = FileUtils.saveBitToSD(bitmap, System.currentTimeMillis() + ".jpg");

                    dataList.add(cameraImagePath);
                    adapter.notifyDataSetChanged();
                    break;
                case CommonDefine.TAKE_PICTURE_FROM_GALLERY:
                    tDataList = data.getStringArrayListExtra("datalist");
                    if (tDataList != null) {
                        dataList.clear();
                        for (int i = 0; i < tDataList.size(); i++) {
                            String string = tDataList.get(i);
                            dataList.add(string);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    break;
                case CommonDefine.DELETE_IMAGE:
                    int position = data.getIntExtra("position", -1);
                    dataList.remove(position);
                    adapter.notifyDataSetChanged();
                    break;
            }
        }
    }



    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {
        public void onClick(View v) {
            deleteWindow.dismiss();
            switch (v.getId()) {
                case R.id.camera: {
                    Intent cameraIntent = new Intent();
                    cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    // 根据文件地址创建文件
                    File file = new File(CommonDefine.FILE_PATH);
                    if (file.exists()) {
                        file.delete();
                    }
                    uri = Uri.fromFile(file);
                    // 设置系统相机拍摄照片完成后图片文件的存放地址
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                    // 开启系统拍照的Activity
                    startActivityForResult(cameraIntent, CommonDefine.TAKE_PICTURE_FROM_CAMERA);
                }
                break;
                case R.id.mapstorage: {
                    Intent intent = new Intent(PublishRecordActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", et_sendmessage.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {

            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     *
     * @param v
     * @param event
     * @return
     */
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }



}
