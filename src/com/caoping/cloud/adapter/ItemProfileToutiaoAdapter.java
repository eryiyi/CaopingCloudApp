package com.caoping.cloud.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.NewsObj;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 * 他的头条
 */
public class ItemProfileToutiaoAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<NewsObj> lists;
    private Context mContect;
    private Boolean flag;

    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemProfileToutiaoAdapter(List<NewsObj> lists, Context mContect, boolean flag) {
        this.lists = lists;
        this.mContect = mContect;
        this.flag = flag;
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        res = mContect.getResources();
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_profile_toutiao, null);
            holder.btn_del = (ImageView) convertView.findViewById(R.id.btn_del);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.company = (TextView) convertView.findViewById(R.id.company);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
            holder.btn_favour = (TextView) convertView.findViewById(R.id.btn_favour);
            holder.btn_comment = (TextView) convertView.findViewById(R.id.btn_comment);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        NewsObj cell = lists.get(position);
        if (cell != null) {
            holder.title.setText(cell.getMm_msg_title()==null?"":cell.getMm_msg_title());
            holder.company.setText(cell.getCompanyName()==null?"":cell.getCompanyName());
            holder.dateline.setText(cell.getDateline());
            if(!StringUtil.isNullOrEmpty(cell.getMm_msg_picurl())){
                //说明有图片
                String[] arras = cell.getMm_msg_picurl().split(",");
                if(arras != null && arras.length > 0){
                    imageLoader.displayImage(arras[0], holder.cover, CaopingCloudApplication.txOptions, animateFirstListener);
                    holder.cover.setVisibility(View.VISIBLE);
                }else {
                    holder.cover.setVisibility(View.GONE);
                }
            }else{
                holder.cover.setVisibility(View.GONE);
            }
            holder.btn_comment.setText(cell.getCommentNum()==null?"0":cell.getCommentNum());
            holder.btn_favour.setText(cell.getFavourNum()==null?"0":cell.getFavourNum());
            if(flag){
                holder.btn_del.setVisibility(View.VISIBLE);
            }else {
                holder.btn_del.setVisibility(View.GONE);
            }
            holder.btn_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickContentItemListener.onClickContentItem(position, 0, "1000");
                }
            });
        }


        return convertView;
    }

    class ViewHolder {
        ImageView cover;
        ImageView btn_del;
        TextView title;
        TextView company;
        TextView dateline;
        TextView btn_favour;
        TextView btn_comment;
    }
}
