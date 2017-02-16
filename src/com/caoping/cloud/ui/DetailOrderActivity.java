package com.caoping.cloud.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.ShoppingAddressSingleDATA;
import com.caoping.cloud.entiy.OrderVo;
import com.caoping.cloud.entiy.ShoppingAddress;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/15.
 * 订单详情
 */
public class DetailOrderActivity extends BaseActivity implements View.OnClickListener {
    private OrderVo orderVo;//传递过来的值
    private TextView order_status;

    //收货地址
    private TextView order_name;
    private TextView order_tel;
    private TextView order_location;
    //卖家信息
    private ImageView item_head;
    private TextView item_nickname;

    //订单信息
    private ImageView item_pic;
    private TextView item_content;
    private TextView item_prices;
    private TextView item_money;
    private TextView item_count;

    //功能按钮
    private Button button_two;

    private RelativeLayout liner_two;
    private RelativeLayout relative_one;
    private TextView order_dateline;//订单编号

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private TextView title;

    private ImageView erweima_order;//卖家扫一扫

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_order_activity);
        orderVo = (OrderVo) getIntent().getExtras().get("orderVo");
        initView();
        //填充数据
        initData();
        if(!StringUtil.isNullOrEmpty(orderVo.getAddress_id())){
            //获得收货地址
            getAddressById();
        }

    }

    private void initView() {
        title = (TextView) this.findViewById(R.id.title);
        title.setText("订单详情");
        this.findViewById(R.id.back).setOnClickListener(this);
        order_status = (TextView) this.findViewById(R.id.order_status);
        order_name = (TextView) this.findViewById(R.id.order_name);
        order_tel = (TextView) this.findViewById(R.id.order_tel);
        order_location = (TextView) this.findViewById(R.id.order_location);
        item_head = (ImageView) this.findViewById(R.id.item_head);
        item_nickname = (TextView) this.findViewById(R.id.item_nickname);
        item_pic = (ImageView) this.findViewById(R.id.item_pic);
        item_content = (TextView) this.findViewById(R.id.item_content);
        item_prices = (TextView) this.findViewById(R.id.item_prices);
        item_money = (TextView) this.findViewById(R.id.item_money);
        item_count = (TextView) this.findViewById(R.id.item_count);
        button_two = (Button) this.findViewById(R.id.button_two);
        relative_one = (RelativeLayout) this.findViewById(R.id.relative_one);
        liner_two = (RelativeLayout) this.findViewById(R.id.liner_two);
        order_dateline = (TextView) this.findViewById(R.id.order_dateline);

        button_two.setOnClickListener(this);
        item_head.setOnClickListener(this);
        item_nickname.setOnClickListener(this);
        relative_one.setOnClickListener(this);

        erweima_order = (ImageView) this.findViewById(R.id.erweima_order);
        erweima_order.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.relative_one:
            {
                if (orderVo != null && !StringUtil.isNullOrEmpty(orderVo.getCloud_caoping_id()) && !StringUtil.isNullOrEmpty(orderVo.getEmp_id())) {
                    Intent goodsdetail = new Intent(DetailOrderActivity.this, DetailGoodsActivity.class);
                    goodsdetail.putExtra("id", orderVo.getCloud_caoping_id());
                    startActivity(goodsdetail);
                }
            }
                break;
            case R.id.item_head:{
//                Intent profileView = new Intent(DetailOrderActivity.this, ProfilePersonalActivity.class);
//                profileView.putExtra(Constants.EMPID, orderVo.getSeller_emp_id());
//                startActivity(profileView);
            }
                break;
            case R.id.erweima_order:
            {
                //二维码
            }

                break;
            case R.id.button_two:
            {
                if(!StringUtil.isNullOrEmpty(orderVo.getEmp_mobile_seller())){
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + orderVo.getEmp_mobile_seller()));
                    this.startActivity(intent);
                }
            }
                break;
        }
    }

    Bitmap bitmap ;

    void initData(){
        StringBuilder datetime = new StringBuilder();
        datetime.append("订单编号:" + orderVo.getOrder_no());
        switch (Integer.parseInt(orderVo.getStatus())){
            //1生成订单,2支付订单,3取消订单,4作废订单,5完成订单', 6物流运输中（卖家确认订单）
            case 1:
                order_status.setText("等待买家付款");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:"+orderVo.getCreate_time());
                }
                break;
            case 2:
                order_status.setText("等待卖家发货");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:"+orderVo.getCreate_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getPay_time())) {
                    datetime.append("\n" + "付款时间:"+orderVo.getPay_time());
                }
                break;
            case 3:
                order_status.setText("订单已取消");
                break;
            case 4:
                order_status.setText("订单已作废");
                break;
            case 5:
                order_status.setText("订单已完成");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:" + orderVo.getCreate_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getPay_time())){
                    datetime.append("\n" + "付款时间:" + orderVo.getPay_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getSend_time())){
                    datetime.append("\n" + "发货时间:" + orderVo.getSend_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getAccept_time())){
                    datetime.append("\n" + "收货时间:" + orderVo.getAccept_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getCompletion_time())){
                    datetime.append("\n" + "完成时间:" + orderVo.getCompletion_time());
                }
                break;
            case 6:
                order_status.setText("卖家已发货");
                if(!StringUtil.isNullOrEmpty(orderVo.getCreate_time())){
                    datetime.append("\n" + "创建时间:" + orderVo.getCreate_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getPay_time())){
                    datetime.append("\n" + "付款时间:" + orderVo.getPay_time());
                }
                if(!StringUtil.isNullOrEmpty(orderVo.getSend_time())){
                    datetime.append("\n" + "发货时间:" + orderVo.getSend_time());
                }
                break;
        }

        imageLoader.displayImage((orderVo.getEmp_cover_seller()==null?"":orderVo.getEmp_cover_seller()), item_head, CaopingCloudApplication.txOptions, animateFirstListener);
        if(!StringUtil.isNullOrEmpty(orderVo.getCloud_caoping_pic())){
            String[] arras = orderVo.getCloud_caoping_pic().split(",");
            if(arras != null && arras.length>0){
                imageLoader.displayImage(arras[0], item_pic, CaopingCloudApplication.options, animateFirstListener);
            }
        }
        item_nickname.setText(orderVo.getEmp_name_seller()==null?"":orderVo.getEmp_name_seller());
        item_content.setText(orderVo.getCloud_caoping_title()==null?"":orderVo.getCloud_caoping_title());
        item_prices.setText(getResources().getString(R.string.money) + (orderVo.getCloud_caoping_prices()==null?"":orderVo.getCloud_caoping_prices()));
        item_count.setText(String.format(getResources().getString(R.string.item_count_adapter), (orderVo.getGoods_count() == null ? "" : orderVo.getGoods_count())));
        item_money.setText(String.format(getResources().getString(R.string.item_money_adapter), Double.valueOf(orderVo.getPayable_amount() == null ? "" : orderVo.getPayable_amount())));
        order_dateline.setText(datetime);

        if("1".equals(orderVo.getIs_dxk_order())){
            relative_one.setVisibility(View.GONE);
            liner_two.setVisibility(View.GONE);
        }else {
            relative_one.setVisibility(View.VISIBLE);
            liner_two.setVisibility(View.VISIBLE);
        }

    }

    void getAddressById(){
        //收货地址获得
        StringRequest request = new StringRequest(
                Request.Method.POST,
               InternetURL.GET_ADDRESS_BYID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            ShoppingAddressSingleDATA data = getGson().fromJson(s, ShoppingAddressSingleDATA.class);
                            if (data.getCode() == 200) {
                               //收货地址
                                initAddress(data.getData());
                            } else {
                                Toast.makeText(DetailOrderActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(DetailOrderActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(DetailOrderActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("address_id", orderVo.getAddress_id());
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

    void initAddress(ShoppingAddress shoppingAddress){
        //收货地址
        if(shoppingAddress != null){
            order_name.setText(shoppingAddress.getAccept_name());
            order_tel.setText(shoppingAddress.getPhone());
            order_location.setText(shoppingAddress.getProvinceName()+shoppingAddress.getCityName()+shoppingAddress.getAreaName()+shoppingAddress.getAddress());
        }

    }

}
