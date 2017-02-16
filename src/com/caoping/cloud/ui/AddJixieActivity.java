package com.caoping.cloud.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
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
import com.caoping.cloud.upload.CommonUtil;
import com.caoping.cloud.util.CommonDefine;
import com.caoping.cloud.util.FileUtils;
import com.caoping.cloud.util.ImageUtils;
import com.caoping.cloud.util.StringUtil;
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
 * Created by zhl on 2016/8/30.
 */
public class AddJixieActivity extends BaseActivity implements View.OnClickListener{
    private TextView title;
    private TextView right_btn;

    private EditText name;
    private EditText content;
    private NoScrollGridView gridView1;
    private TextView address;
    private EditText money;
    private TextView guige;
    private TextView typeTwo;

    private final static int SELECT_LOCAL_PHOTO = 110;
    private Publish_mood_GridView_Adapter adapter;
    private static int REQUEST_CODE = 1;
    private Uri uri;
    private SelectPhoPopWindow deleteWindow;
//    AsyncHttpClient client = new AsyncHttpClient();
    private ArrayList<String> dataList = new ArrayList<String>();
    private ArrayList<String> tDataList = new ArrayList<String>();
    private List<String> uploadPaths = new ArrayList<String>();

    private LinearLayout liner_address;
    private CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_jixie_activity);
        dataList.add("camera_default");
        initView();
    }

    private void initView() {
        this.findViewById(R.id.back).setOnClickListener(this);
        this.findViewById(R.id.right_btn).setVisibility(View.VISIBLE);
        right_btn = (TextView) this.findViewById(R.id.right_btn);
        right_btn.setText("提交");
        right_btn.setOnClickListener(this);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("机械发布");
        this.findViewById(R.id.btn_gg).setOnClickListener(this);
        this.findViewById(R.id.liner_type_two).setOnClickListener(this);

        name = (EditText) this.findViewById(R.id.name);
        content = (EditText) this.findViewById(R.id.content);
        gridView1 = (NoScrollGridView) this.findViewById(R.id.gridView1);
        address = (TextView) this.findViewById(R.id.address);
        money = (EditText) this.findViewById(R.id.money);
        guige = (TextView) this.findViewById(R.id.guige);
        typeTwo = (TextView) this.findViewById(R.id.typeTwo);

        guige.setText("选择规格");
        typeTwo.setText("选择用途");

        address.setOnClickListener(this);

        adapter = new Publish_mood_GridView_Adapter(this, dataList);
        gridView1.setAdapter(adapter);
        gridView1.setOnItemClickListener(new GridView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String path = dataList.get(position);
                if (path.indexOf("camera_default") != -1) {
                    showSelectImageDialog();
                } else {
                    Intent intent = new Intent(AddJixieActivity.this, ImageDelActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("path", dataList.get(position));
                    startActivityForResult(intent, CommonDefine.DELETE_IMAGE);
                }
            }
        });
        liner_address = (LinearLayout) this.findViewById(R.id.liner_address);
        if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress)){
            address.setText(CaopingCloudApplication.locationAddress);
            liner_address.setVisibility(View.VISIBLE);
        }else {
            liner_address.setVisibility(View.GONE);
        }
        checkbox = (CheckBox) this.findViewById(R.id.checkbox);
    }


    private String cloud_caoping_guige_id;
    private String cloud_caoping_use_id;

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                showSelectPublishDialog();
                break;
            case R.id.btn_gg:
            {
                //规格
                Intent intent = new Intent(AddJixieActivity.this, SelectJixieGuigeActivity.class);
                startActivityForResult(intent, 1000);
            }
                break;

            case R.id.liner_type_two:
            {
                //草坪用途
                Intent intent = new Intent(AddJixieActivity.this, SelectJixieUseActivity.class);
                startActivityForResult(intent, 1000);
            }
                break;
            case R.id.address:
            {
                //选择地址
            }
                break;
            case R.id.right_btn:
            {
                //发布
                if(StringUtil.isNullOrEmpty(name.getText().toString())){
                    showMsg(AddJixieActivity.this, "请输入标题！");
                    return;
                }
                if(name.getText().toString().length() > 100){
                    showMsg(AddJixieActivity.this, "标题超出字段限制，100字以内！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(content.getText().toString())){
                    showMsg(AddJixieActivity.this, "请输入内容！");
                    return;
                }
                if(content.getText().toString().length() > 1000){
                    showMsg(AddJixieActivity.this, "内容超出字段限制，1000字以内！");
                    return;
                }
                if(dataList.size() == 1){
                    showMsg(AddJixieActivity.this, "请至少选择一张图片！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(cloud_caoping_guige_id)){
                    showMsg(AddJixieActivity.this, "请选择规格！");
                    return;
                }
                if(StringUtil.isNullOrEmpty(money.getText().toString())){
                    showMsg(AddJixieActivity.this, "请输入价格！");
                    return;
                }

                if(StringUtil.isNullOrEmpty(cloud_caoping_use_id)){
                    showMsg(AddJixieActivity.this, "请选择用途！");
                    return;
                }
                if (uploadPaths.size() ==( dataList.size()-1)) {
                    publishAll();
                }else {
                    uploadPaths.clear();
                    addPic();
                }
            }
            break;

        }
    }

    void addPic(){
        for (int i = 1; i < dataList.size(); i++) {
            File f = new File(dataList.get(i));
            final Map<String, File> files = new HashMap<String, File>();
            files.put("file", f);
            Map<String, String> params = new HashMap<String, String>();
            CommonUtil.addPutUploadFileRequest(
                    AddJixieActivity.this,
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
                                        if (uploadPaths.size() == (dataList.size() - 1)) {
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
        }
    }

    //上传完图片后开始发布
    private void publishAll() {
        final StringBuffer filePath = new StringBuffer();
        for (int i = 0; i < uploadPaths.size(); i++) {
            filePath.append(uploadPaths.get(i));
            if (i != uploadPaths.size() - 1) {
                filePath.append(",");
            }
        }
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.SAVE_CP_OBJ_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo =  new JSONObject(s);
                                String code = jo.getString("code");
                                if (code.equals("200")) {
                                    Toast.makeText(AddJixieActivity.this, R.string.publish_success, Toast.LENGTH_SHORT).show();
                                    //调用广播，刷新主页
                                    Intent intent1 = new Intent("add_product_success");
                                    sendBroadcast(intent1);
                                    finish();
                                }else{
                                    showMsg(AddJixieActivity.this, jo.getString("message"));
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
                        Toast.makeText(AddJixieActivity.this, R.string.publish_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("cloud_caoping_pic", String.valueOf(filePath));
                params.put("cloud_jixie_guige_id", cloud_caoping_guige_id);
                params.put("cloud_jixie_use_id", cloud_caoping_use_id);
                if(!StringUtil.isNullOrEmpty( name.getText().toString())){
                    params.put("cloud_caoping_title", name.getText().toString());
                }else {
                    params.put("cloud_caoping_title", "");
                }
                if(!StringUtil.isNullOrEmpty(content.getText().toString())){
                    params.put("cloud_caoping_content", content.getText().toString());
                }else{
                    params.put("cloud_caoping_content", "");
                }
                if(!StringUtil.isNullOrEmpty(money.getText().toString())){
                    params.put("cloud_caoping_prices", money.getText().toString());
                }else {
                    params.put("cloud_caoping_prices", "");
                }
                params.put("emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                if(checkbox.isChecked()){
                    //选择了地址
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.latStr)){
                        params.put("lat", CaopingCloudApplication.latStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.lngStr)){
                        params.put("lng", CaopingCloudApplication.lngStr);
                    }
                    if(!StringUtil.isNullOrEmpty(CaopingCloudApplication.locationAddress)){
                        params.put("cloud_caoping_address", CaopingCloudApplication.locationAddress);
                    }
                }
                params.put("is_type", "2");
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
        deleteWindow = new SelectPhoPopWindow(AddJixieActivity.this, itemsOnClick);
        //显示窗口
        deleteWindow.showAtLocation(AddJixieActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
        if(requestCode == 1000){
            // 根据上面发送过去的请求吗来区别
            switch (resultCode) {
                case 1001:
                {
                    cloud_caoping_guige_id = data.getStringExtra("cloud_caoping_guige_id");
                    String cloud_caoping_guige_cont = data.getStringExtra("cloud_caoping_guige_cont");
                    if(!StringUtil.isNullOrEmpty(cloud_caoping_guige_cont)){
                        guige.setText(cloud_caoping_guige_cont);
                    }
                }
                break;

                case 1003:
                {
                    cloud_caoping_use_id = data.getStringExtra("cloud_caoping_use_id");
                    String cloud_caoping_use_cont = data.getStringExtra("cloud_caoping_use_cont");
                    if(!StringUtil.isNullOrEmpty(cloud_caoping_use_cont)){
                        typeTwo.setText(cloud_caoping_use_cont);
                    }
                }
                break;
                default:
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
                    Intent intent = new Intent(AddJixieActivity.this, AlbumActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("dataList", getIntentArrayList(dataList));
                    bundle.putString("editContent", content.getText().toString());
                    intent.putExtras(bundle);
                    startActivityForResult(intent, CommonDefine.TAKE_PICTURE_FROM_GALLERY);
                }
                break;
                default:
                    break;
            }
        }

    };


    private PublishPopWindow publishPopWindow;
    // 选择是否退出发布
    private void showSelectPublishDialog() {
        publishPopWindow = new PublishPopWindow(AddJixieActivity.this, itemOnClick);
        publishPopWindow.showAtLocation(AddJixieActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
            showSelectPublishDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
