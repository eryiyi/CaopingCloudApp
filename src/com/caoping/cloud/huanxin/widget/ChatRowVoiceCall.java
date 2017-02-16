package com.caoping.cloud.huanxin.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.db.DBHelper;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.huanxin.Constant;
import com.caoping.cloud.util.StringUtil;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ChatRowVoiceCall extends EaseChatRow{

    private TextView contentvView;
    private ImageView iconView;
    private ImageView iv_userhead;
    private Context context;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public ChatRowVoiceCall(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
        this.context = context;
    }

    @Override
    protected void onInflatView() {
        if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_received_voice_call : R.layout.ease_row_sent_voice_call, this);
        // video call
        }else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.ease_row_received_video_call : R.layout.ease_row_sent_video_call, this);
        }
    }

    @Override
    protected void onFindViewById() {
        contentvView = (TextView) findViewById(R.id.tv_chatcontent);
        iconView = (ImageView) findViewById(R.id.iv_call_icon);
        iv_userhead = (ImageView) findViewById(R.id.iv_userhead);
    }

    @Override
    protected void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        contentvView.setText(txtBody.getMessage());
        if(!StringUtil.isNullOrEmpty(message.getFrom())){
           Member member = DBHelper.getInstance(context).getMemberId(message.getFrom());
            if(member != null){
                imageLoader.displayImage(member.getEmpCover(), iv_userhead, CaopingCloudApplication.txOptions, animateFirstListener);
            }
        }
    }
    
    @Override
    protected void onUpdateView() {
        
    }

    @Override
    protected void onBubbleClick() {
        
    }

  

}
