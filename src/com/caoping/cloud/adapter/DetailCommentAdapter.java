package com.caoping.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.Comment;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: zhanghl
 * Date: 2014/8/9
 * Time: 11:07
 * 评论
 */
public class DetailCommentAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context context;
    private List<Comment> list;
    private LayoutInflater inflater;
    private OnClickContentItemListener onClickContentItemListener;
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public DetailCommentAdapter(Context context, List<Comment> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.detail_comment, null);
            holder.comment_photo = (ImageView) convertView.findViewById(R.id.comment_photo);
            holder.comment_nickname = (TextView) convertView.findViewById(R.id.comment_nickname);
            holder.comment_f_nickname = (TextView) convertView.findViewById(R.id.comment_f_nickname);
            holder.comment_time = (TextView) convertView.findViewById(R.id.comment_time);
            holder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            holder.floor_comment = (TextView) convertView.findViewById(R.id.floor_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Comment commentContent = list.get(position);
        if (commentContent != null) {
            imageLoader.displayImage(commentContent.getEmp_cover(), holder.comment_photo, CaopingCloudApplication.options);
            holder.comment_nickname.setText(commentContent.getEmp_name());
            holder.comment_time.setText(commentContent.getDateline());
            if (StringUtil.isNullOrEmpty(commentContent.getComment_fplid())) {//没有父评论
                holder.comment_f_nickname.setVisibility(View.GONE);
            } else {
                holder.comment_f_nickname.setVisibility(View.VISIBLE);
                holder.comment_f_nickname.setText("回复<" + commentContent.getEmp_name_f() + ">: ");
            }
            if (!StringUtil.isNullOrEmpty(commentContent.getComment_content())) {
                holder.comment_content.setVisibility(View.VISIBLE);
                holder.comment_content.setText(commentContent.getComment_content());
            }
        }
        //头像点击事件
        holder.comment_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        //昵称点击事件
        holder.comment_nickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickContentItemListener.onClickContentItem(position, 1, null);
            }
        });
        holder.floor_comment.setText(String.valueOf(position + 1) + "#");
        return convertView;
    }

    class ViewHolder {
        ImageView comment_photo;//头像
        TextView comment_nickname;//用户昵称
        TextView comment_f_nickname;//父评论昵称
        TextView comment_time;//评论时间
        TextView comment_content;//评论内容
        TextView floor_comment;//评论楼层
    }
}
