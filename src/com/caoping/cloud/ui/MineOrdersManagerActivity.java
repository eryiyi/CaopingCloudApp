package com.caoping.cloud.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.ItemManagerOrderAdapter;
import com.caoping.cloud.adapter.ItemMineOrderAdapter;
import com.caoping.cloud.adapter.OnClickContentItemListener;
import com.caoping.cloud.base.BaseActivity;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.OrdersVoDATA;
import com.caoping.cloud.data.SuccessData;
import com.caoping.cloud.entiy.OrderVo;
import com.caoping.cloud.library.PullToRefreshBase;
import com.caoping.cloud.library.PullToRefreshListView;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.OrderCancelPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/15.
 * 我的订单
 */
public class MineOrdersManagerActivity extends BaseActivity implements View.OnClickListener,OnClickContentItemListener {

    private PullToRefreshListView classtype_lstv;//列表
    private int pageIndex = 1;
    private static boolean IS_REFRESH = true;

    private ItemManagerOrderAdapter adapter;
    private List<OrderVo> orderVos = new ArrayList<>();
    private TextView text_one;
    private TextView text_two;
    private TextView text_three;
    private TextView text_four;
    private TextView text_five;
    private String status="";
    private String pay_status="";
    private String is_comment="";

    private OrderCancelPopWindow orderCancelPopWindow;//取消订单
    private OrderCancelPopWindow orderCancelPopWindowTwo;//确认收货
    private OrderCancelPopWindow orderCancelPopWindowThree;//删除订单
    private OrderCancelPopWindow orderCancelPopWindowFour;//退回

    private TextView title;
    private ImageView search_null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine_order_activity);
        registerBoradcastReceiver();
        if(StringUtil.isNullOrEmpty(status)){
            status = "";
        }
        initView();
//        private String status;//1生成订单,2支付订单,3取消订单,4作废订单,5完成订单，6物流运输中（卖家确认订单）',7退货
//        private String pay_status;//支付状态 0：未支付，1：已支付，2：退款'
//        rivate String is_comment;//是否评价； 默认0否  1是
        if("".equals(status)){
            text_one.setTextColor(getResources().getColor(R.color.red));
            text_two.setTextColor(getResources().getColor(R.color.text_color));
            text_three.setTextColor(getResources().getColor(R.color.text_color));
            text_four.setTextColor(getResources().getColor(R.color.text_color));
            text_five.setTextColor(getResources().getColor(R.color.text_color));
            initData();
        }
        if("1".equals(status)){
            text_one.setTextColor(getResources().getColor(R.color.text_color));
            text_two.setTextColor(getResources().getColor(R.color.red));
            text_three.setTextColor(getResources().getColor(R.color.text_color));
            text_four.setTextColor(getResources().getColor(R.color.text_color));
            text_five.setTextColor(getResources().getColor(R.color.text_color));
            initData();
        }
        if("2".equals(status)){
            text_one.setTextColor(getResources().getColor(R.color.text_color));
            text_two.setTextColor(getResources().getColor(R.color.text_color));
            text_three.setTextColor(getResources().getColor(R.color.red));
            text_four.setTextColor(getResources().getColor(R.color.text_color));
            text_five.setTextColor(getResources().getColor(R.color.text_color));
            initData();
        }
        if("6".equals(status)){
            text_one.setTextColor(getResources().getColor(R.color.text_color));
            text_two.setTextColor(getResources().getColor(R.color.text_color));
            text_three.setTextColor(getResources().getColor(R.color.text_color));
            text_four.setTextColor(getResources().getColor(R.color.red));
            text_five.setTextColor(getResources().getColor(R.color.text_color));
            initData();
        }
        if("5".equals(status)){
            text_one.setTextColor(getResources().getColor(R.color.text_color));
            text_two.setTextColor(getResources().getColor(R.color.text_color));
            text_three.setTextColor(getResources().getColor(R.color.text_color));
            text_four.setTextColor(getResources().getColor(R.color.text_color));
            text_five.setTextColor(getResources().getColor(R.color.red));
            initData();
        }

        initData();
    }

    private void initView() {
        search_null = (ImageView) this.findViewById(R.id.search_null);
        title = (TextView) this.findViewById(R.id.title);
        title.setText("我的销售订单");
        this.findViewById(R.id.back).setOnClickListener(this);
        classtype_lstv = (PullToRefreshListView) this.findViewById(R.id.lstv);
        adapter = new ItemManagerOrderAdapter(orderVos,MineOrdersManagerActivity.this);
        adapter.setOnClickContentItemListener(this);

        classtype_lstv.setMode(PullToRefreshBase.Mode.BOTH);
        classtype_lstv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineOrdersManagerActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = true;
                pageIndex = 1;
                initData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                String label = DateUtils.formatDateTime(MineOrdersManagerActivity.this, System.currentTimeMillis(),
                        DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                IS_REFRESH = false;
                pageIndex++;
                initData();
            }
        });
        classtype_lstv.setAdapter(adapter);
        classtype_lstv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderVo orderVo = orderVos.get(position-1);
                Intent detailView = new Intent(MineOrdersManagerActivity.this, DetailOrderActivity.class);
                detailView.putExtra("orderVo",orderVo);
                startActivity(detailView);
            }
        });

        text_one = (TextView) this.findViewById(R.id.text_one);
        text_two = (TextView) this.findViewById(R.id.text_two);
        text_three = (TextView) this.findViewById(R.id.text_three);
        text_four = (TextView) this.findViewById(R.id.text_four);
        text_five = (TextView) this.findViewById(R.id.text_five);
        text_one.setOnClickListener(this);
        text_two.setOnClickListener(this);
        text_three.setOnClickListener(this);
        text_four.setOnClickListener(this);
        text_five.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.text_one:
                text_one.setTextColor(getResources().getColor(R.color.red));
                text_two.setTextColor(getResources().getColor(R.color.text_color));
                text_three.setTextColor(getResources().getColor(R.color.text_color));
                text_four.setTextColor(getResources().getColor(R.color.text_color));
                text_five.setTextColor(getResources().getColor(R.color.text_color));
                status = "";
                is_comment = "";
                pay_status = "";
                initData();
                break;
            case R.id.text_two:
                text_one.setTextColor(getResources().getColor(R.color.text_color));
                text_two.setTextColor(getResources().getColor(R.color.red));
                text_three.setTextColor(getResources().getColor(R.color.text_color));
                text_four.setTextColor(getResources().getColor(R.color.text_color));
                text_five.setTextColor(getResources().getColor(R.color.text_color));
                status = "1";
                pay_status = "0";
                is_comment = "";
                initData();
                break;
            case R.id.text_three:
                text_one.setTextColor(getResources().getColor(R.color.text_color));
                text_two.setTextColor(getResources().getColor(R.color.text_color));
                text_three.setTextColor(getResources().getColor(R.color.red));
                text_four.setTextColor(getResources().getColor(R.color.text_color));
                text_five.setTextColor(getResources().getColor(R.color.text_color));
                status = "2";
                pay_status = "1";
                is_comment = "";
                initData();
                break;
            case R.id.text_four:
                text_one.setTextColor(getResources().getColor(R.color.text_color));
                text_two.setTextColor(getResources().getColor(R.color.text_color));
                text_three.setTextColor(getResources().getColor(R.color.text_color));
                text_four.setTextColor(getResources().getColor(R.color.red));
                text_five.setTextColor(getResources().getColor(R.color.text_color));
                status = "6";
                pay_status = "1";
                is_comment = "";
                initData();
                break;
            case R.id.text_five:
                text_one.setTextColor(getResources().getColor(R.color.text_color));
                text_two.setTextColor(getResources().getColor(R.color.text_color));
                text_three.setTextColor(getResources().getColor(R.color.text_color));
                text_four.setTextColor(getResources().getColor(R.color.text_color));
                text_five.setTextColor(getResources().getColor(R.color.red));
                status = "5";
                is_comment = "0";
                pay_status = "";
                initData();
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    OrderVo orderVoTmp;
    int tmpPosition;
    @Override
    public void onClickContentItem(int position, int flag, Object object) {
        orderVoTmp = orderVos.get(position);
        tmpPosition = position;
        switch (flag){

            case 3:
                //去付款
            {
                if(orderVoTmp != null){
                    Intent intent = new Intent(MineOrdersManagerActivity.this, PaySelectSingleActivity.class);
                    intent.putExtra("orderVoTmp", orderVoTmp);
                    startActivity(intent);
                }
            }
                break;
//            case 3:
//                //投诉
//                break;
            case 4:
                //取消订单
                showCancel();
                break;
            case 5:
                //评价
                if(orderVoTmp != null && !StringUtil.isNullOrEmpty(orderVoTmp.getCloud_caoping_id())){
                    if("0".equals(orderVoTmp.getIs_comment())){
                        Intent comment = new Intent(this, PublishGoodCommentActivity.class);
                        comment.putExtra("cloud_caoping_id", orderVoTmp.getCloud_caoping_id());
                        comment.putExtra("order_no", orderVoTmp.getOrder_no());
                        comment.putExtra("emp_id", (orderVoTmp.getEmp_id()==null?"":orderVoTmp.getEmp_id()));//商品所有者
                        startActivity(comment);
                    }
                }else {
                    showMsg(MineOrdersManagerActivity.this, "该商品暂不支持评论！");
                }
                break;
            case 6:
                //删除订单
                showDelete();
                break;
            case 7:
                //确认发货
                showSure();
                break;
        }
    }
    //取订单
    private void initData() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.MINE_ORDERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            OrdersVoDATA data = getGson().fromJson(s, OrdersVoDATA.class);
                            if (data.getCode() == 200) {
                                if (IS_REFRESH) {
                                    orderVos.clear();
                                }
                                orderVos.addAll(data.getData());
                                classtype_lstv.onRefreshComplete();
                                adapter.notifyDataSetChanged();
                                if(orderVos.size() == 0){
                                    search_null.setVisibility(View.VISIBLE);
                                    classtype_lstv.setVisibility(View.GONE);
                                }else{
                                    search_null.setVisibility(View.GONE);
                                    classtype_lstv.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("page", String.valueOf(pageIndex));
                params.put("seller_emp_id", getGson().fromJson(getSp().getString("empId", ""), String.class));
                if(!StringUtil.isNullOrEmpty(status)){
                    params.put("status", status);
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

    private void showCancel() {
        orderCancelPopWindow = new OrderCancelPopWindow(MineOrdersManagerActivity.this, itemsOnClick);
        //显示窗口
        orderCancelPopWindow.showAtLocation(MineOrdersManagerActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void showSure() {
        orderCancelPopWindowTwo = new OrderCancelPopWindow(MineOrdersManagerActivity.this, itemsOnClickTwo);
        //显示窗口
        orderCancelPopWindowTwo.showAtLocation(MineOrdersManagerActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void showDelete() {
        orderCancelPopWindowThree = new OrderCancelPopWindow(MineOrdersManagerActivity.this, itemsOnClickThree);
        //显示窗口
        orderCancelPopWindowThree.showAtLocation(MineOrdersManagerActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }
    private void showTuihuo() {
        orderCancelPopWindowFour = new OrderCancelPopWindow(MineOrdersManagerActivity.this, itemsOnClickFour);
        //显示窗口
        orderCancelPopWindowFour.showAtLocation(MineOrdersManagerActivity.this.findViewById(R.id.main), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindow.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //取消订单
                    cancelOrder();
                }
                break;
            }
        }
    };
    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClickTwo = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindowTwo.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //确认发货
                    sureOrder();
                }
                break;
            }
        }
    };

    private View.OnClickListener itemsOnClickThree = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindowThree.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //删除订单
                    deleteOrder();
                }
                break;
            }
        }
    };
    private View.OnClickListener itemsOnClickFour = new View.OnClickListener() {

        public void onClick(View v) {
            orderCancelPopWindowFour.dismiss();
            switch (v.getId()) {
                case R.id.sure:
                {
                    //退货
//                    tuiOrder();
//                    Intent intent = new Intent(MineOrdersActivity.this, TuihuoActivity.class);
//                    intent.putExtra("orderVoTmp", orderVoTmp);
//                    startActivity(intent);
                }
                break;
            }
        }
    };

    //取消订单
    private void cancelOrder() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
               InternetURL.UPDATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.get(tmpPosition).setStatus("3");
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("status", "3");
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

    //确认发货
    private void sureOrder() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.get(tmpPosition).setStatus("5");
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersManagerActivity.this, R.string.get_data_error, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("status", "6");
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

    //删除订单
    public void deleteOrder() {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_ORDER,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                orderVos.remove(tmpPosition);
                                adapter.notifyDataSetChanged();
                                Toast.makeText(MineOrdersManagerActivity.this, R.string.delete_order_success, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MineOrdersManagerActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersManagerActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersManagerActivity.this, R.string.cancel_order_error_one, Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", orderVoTmp.getOrder_no());
                params.put("status", "4");//4作废订单
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



    //用户已经评价
    public void updateCommentStatus(final String order_no) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.UPDATE_ORDER_COMMENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            SuccessData data = getGson().fromJson(s, SuccessData.class);
                            if (data.getCode() == 200) {
                                initData();
                            } else {
                                Toast.makeText(MineOrdersManagerActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MineOrdersManagerActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(MineOrdersManagerActivity.this, "操作失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("order_no", order_no);
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
            if (action.equals("update_order_success")) {
//                intent1.putExtra("order_no", orderVoTmp.getOrder_no());
//                intent1.putExtra("status", "7");
                String orderno = intent.getExtras().getString("order_no");
                String status = intent.getExtras().getString("status");
                if("7".equals(status)){
                    //退货申请成功
                    initData();
                }
            }
            if(action.equals("pay_single_order_success")){
                initData();
            }
            if(action.equals("add_goods_comment_success")){
                String order_no = intent.getExtras().getString("order_no");
                //更新订单状态
                updateCommentStatus(order_no);
            }

        }
    };

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("update_order_success");
        myIntentFilter.addAction("pay_single_order_success");
        myIntentFilter.addAction("add_goods_comment_success");//评价成功
        //注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }



}
