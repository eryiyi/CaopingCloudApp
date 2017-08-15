package com.caoping.cloud.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.Transport;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 * 找货
 */
public class ItemWuliuHuoAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<Transport> lists;
    private Context mContect;
    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemWuliuHuoAdapter(List<Transport> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_wuliu_huo, null);
            holder.address_one = (TextView) convertView.findViewById(R.id.address_one);
            holder.address_two = (TextView) convertView.findViewById(R.id.address_two);
            holder.startTime = (TextView) convertView.findViewById(R.id.startTime);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            holder.btn_tel = (ImageView) convertView.findViewById(R.id.btn_tel);
            holder.btn_im = (ImageView) convertView.findViewById(R.id.btn_im);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Transport cell = lists.get(position);

        if(cell != null){
            holder.address_one.setText(cell.getStart_cityName()+cell.getStart_areaName());
            holder.address_two.setText(cell.getEnd_cityName()+cell.getEnd_areaName());

            holder.startTime.setText(cell.getStart_time());
            holder.dateline.setText(cell.getDateline());
            holder.content.setText(cell.getDetail()==null?"":cell.getDetail());
        }

        if (cell != null) {
            holder.btn_tel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 0, "1000");
                }
            });
            holder.btn_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, "1000");
                }
            });
        }

        return convertView;
    }

    class ViewHolder {
        TextView address_one;
        TextView address_two;
        TextView startTime;
        TextView dateline;
        TextView content;
        ImageView btn_tel;
        ImageView btn_im;

    }
}
