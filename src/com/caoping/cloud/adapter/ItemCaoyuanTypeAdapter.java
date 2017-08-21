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
import com.caoping.cloud.entiy.Cpuse;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 * 基地 类型
 */
public class ItemCaoyuanTypeAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Cpuse> lists;
    private Context mContect;
    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemCaoyuanTypeAdapter(List<Cpuse> lists, Context mContect) {
        this.lists = lists;
        this.mContect = mContect;
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_caoyuan_type, null);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            holder.item_name = (TextView) convertView.findViewById(R.id.item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Cpuse cell = lists.get(position);
        if (cell != null) {
            holder.item_name.setText(cell.getCloud_caoping_use_cont());
            imageLoader.displayImage(cell.getCloud_caoping_use_pic(), holder.item_pic, CaopingCloudApplication.options, animateFirstListener);
        }


        return convertView;
    }

    class ViewHolder {
        ImageView item_pic;
        TextView item_name;
    }
}
