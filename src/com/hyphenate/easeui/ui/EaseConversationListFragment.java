package com.hyphenate.easeui.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.base.InternetURL;
import com.caoping.cloud.data.EmpsData;
import com.caoping.cloud.db.DBHelper;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.huanxin.mine.MyEMConversation;
import com.caoping.cloud.huanxin.ui.ChatActivity;
import com.caoping.cloud.util.GuirenHttpUtils;
import com.caoping.cloud.util.StringUtil;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMConversationListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.widget.EaseConversationList;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * conversation list fragment
 *
 */
public class EaseConversationListFragment extends EaseBaseFragment implements OnClickListener {
    private View view;
    private Resources res;

	private final static int MSG_REFRESH = 2;
    protected EditText query;
    protected ImageButton clearSearch;
    protected boolean hidden;
    protected List<MyEMConversation> conversationList = new ArrayList<MyEMConversation>();
    protected EaseConversationList conversationListView;
    protected FrameLayout errorItemContainer;

    protected boolean isConflict;

    private InputMethodManager inputMethodManager;
    boolean isMobileNet, isWifiNet;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    protected EMConversationListener convListener = new EMConversationListener(){
		@Override
		public void onCoversationUpdate() {
			refresh();
		}
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.ease_fragment_conversation_list, null);
        res = getActivity().getResources();
        registerBoradcastReceiver();
        view.findViewById(R.id.andme).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //IM
//                if(!cpObj.getEmp_id().equals(getGson().fromJson(getSp().getString("empId", ""), String.class))){
                    Intent chatV = new Intent(getActivity(), ChatActivity.class);
                    chatV.putExtra("userId", "3c49e4fa3b8e4a089831f5855f0c4235");
                    chatV.putExtra("userName", "草坪云客服");
                    startActivity(chatV);
//                }else{
//                    Toast.makeText(DetailGoodsActivity.this, "不能和自己聊天！",Toast.LENGTH_SHORT).show();
//                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView() {
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) view.findViewById(R.id.list);
        query = (EditText) view.findViewById(R.id.query);
        clearSearch = (ImageButton) view.findViewById(R.id.search_clear);
        errorItemContainer = (FrameLayout) view.findViewById(R.id.fl_error_item);
    }
    
    @Override
    protected void setUpView() {
        conversationList.addAll(loadConversationList());
        if(conversationList != null && conversationList.size()>0){
            for(MyEMConversation emConversation:conversationList){

            }
        }
        conversationListView.init(conversationList);
        
        if(listItemClickListener != null){
            conversationListView.setOnItemClickListener( new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MyEMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }
        
        EMClient.getInstance().addConnectionListener(connectionListener);
        
        query.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                conversationListView.filter(s);
                if (s.length() > 0) {
                    clearSearch.setVisibility(View.VISIBLE);
                } else {
                    clearSearch.setVisibility(View.INVISIBLE);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
        clearSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                query.getText().clear();
                hideSoftKeyboard();
            }
        });
        
        conversationListView.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftKeyboard();
                return false;
            }
        });



        //判断是否有网
        try {
            isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
            isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
            if (!isMobileNet && !isWifiNet) {
                Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
            }else {
                getNickNamesByHxUserNames(getHxUsernames(conversationList));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getHxUsernames(List<MyEMConversation> conversationList) {
        StringBuffer strUser = new StringBuffer();
        for (int i = 0; i < conversationList.size(); i++) {
            strUser.append(conversationList.get(i).getEmConversation().getUserName());
            if (i < conversationList.size() - 1) {
                strUser.append(",");
            }
        }
        return strUser.toString();
    }
    
    protected EMConnectionListener connectionListener = new EMConnectionListener() {
        
        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                isConflict = true;
            } else {
               handler.sendEmptyMessage(0);
            }
        }
        
        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;
    
    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                onConnectionDisconnected();
                break;
            case 1:
                onConnectionConnected();
                break;
            
            case MSG_REFRESH:
	            {
	            	conversationList.clear();
	                conversationList.addAll(loadConversationList());

                    //判断是否有网
                    try {
                        isMobileNet = GuirenHttpUtils.isMobileDataEnable(getActivity());
                        isWifiNet = GuirenHttpUtils.isWifiDataEnable(getActivity());
                        if (!isMobileNet && !isWifiNet) {
                            Toast.makeText(getActivity(), "请检查网络链接", Toast.LENGTH_SHORT).show();
                        }else {
                            getNickNamesByHxUserNames(getHxUsernames(conversationList));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

	                break;
	            }
            default:
                break;
            }
        }
    };
    
    /**
     * connected to server
     */
    protected void onConnectionConnected(){
        errorItemContainer.setVisibility(View.GONE);
    }
    
    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected(){
        errorItemContainer.setVisibility(View.VISIBLE);
    }
    

    /**
     * refresh ui
     */
    public void refresh() {
    	if(!handler.hasMessages(MSG_REFRESH)){
    		handler.sendEmptyMessage(MSG_REFRESH);
    	}

    }
    
    /**
     * load conversation list
     * 
     * @return
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        +    */
    protected List<MyEMConversation> loadConversationList(){
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        List<Pair<Long, MyEMConversation>> sortList = new ArrayList<Pair<Long, MyEMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    MyEMConversation my = new MyEMConversation();
                    my.setEmConversation(conversation);
                    sortList.add(new Pair<Long, MyEMConversation>(my.getEmConversation().getLastMessage().getMsgTime(), my));
                }
            }
        }
        try {
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<MyEMConversation> list = new ArrayList<MyEMConversation>();
        for (Pair<Long, MyEMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }


    private void sortConversationByLastChatTime(List<Pair<Long, MyEMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, MyEMConversation>>() {
            @Override
            public int compare(final Pair<Long, MyEMConversation> con1, final Pair<Long, MyEMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    
   protected void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(isConflict){
            outState.putBoolean("isConflict", true);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
    }


    public interface EaseConversationListItemClickListener {
        void onListItemClicked(MyEMConversation conversation);
    }
    
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener){
        this.listItemClickListener = listItemClickListener;
    }

    //获得好友资料
    List<Member> emps = new ArrayList<Member>();
    private void getNickNamesByHxUserNames(final String hxUserNames) {
        StringRequest request = new StringRequest(
                Request.Method.POST,
                InternetURL.GET_INVITE_CONTACT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (StringUtil.isJson(s)) {
                            try {
                                JSONObject jo = new JSONObject(s);
                                String code = jo.getString("code");
                                if (Integer.parseInt(code) == 200) {
                                    EmpsData data = getGson().fromJson(s, EmpsData.class);
                                    emps = data.getData();
                                    if(conversationListView != null){
                                        conversationListView.refresh();
                                    }
                                    notifyMyAdapter();
                                      //保存到数据库
                                    if (data != null && data.getData() != null) {
                                        for (Member recordMsg : data.getData()) {
                                            Member recordMsgLocal = DBHelper.getInstance(getActivity()).getMemberId(recordMsg.getEmpId());
                                            if (recordMsgLocal != null) {
                                                //已经存在了 不需要插入了
                                            } else {
                                                DBHelper.getInstance(getActivity()).saveMember(recordMsg);
                                            }
                                        }
                                    }
                                }else {
                                    Toast.makeText(getActivity(), jo.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getActivity(), "获得数据失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("hxUserNames", hxUserNames);
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

    private void notifyMyAdapter() {
        for (MyEMConversation my : conversationList) {
            for (Member emp : emps) {
                if (my.getEmConversation().getUserName().equals(emp.getEmpId())) {
                    my.setEmp(emp);
                }
            }
        }
        if (conversationListView.adapter != null)
            conversationListView.adapter.notifyDataSetChanged();
    }

    //注册广播
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);
    }


    //广播接收动作
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        }
    }  ;

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mBroadcastReceiver);
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }
}
