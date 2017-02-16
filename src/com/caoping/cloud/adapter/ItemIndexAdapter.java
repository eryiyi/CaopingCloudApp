package com.caoping.cloud.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.StatisticalObj;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 */
public class ItemIndexAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<String> lists;
    private Context mContect;
    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemIndexAdapter(List<String> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_index_layout, null);
            holder.item_number = (TextView) convertView.findViewById(R.id.item_number);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String cell = lists.get(position);
        if (cell != null) {
            //发了多少草坪数据 机械数据 草种数据
            //发了多少物流数据
            //发了多少新闻
            //有多少订单
            //有多少人关注你
            //你的公司在名企排行中排第几
            holder.item_number.setText(cell);
            if(position == 0){
                //草坪
                holder.item_number.setTextColor(res.getColor(R.color.index_one));
                holder.item_title.setText("草坪");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_blue));
            }
            if(position == 1){
                //机械
                holder.item_number.setTextColor(res.getColor(R.color.index_two));
                holder.item_title.setText("机械");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_green));
            }
            if(position == 2){
                //草种
                holder.item_number.setTextColor(res.getColor(R.color.index_three));
                holder.item_title.setText("草种");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_purple));
            }
            if(position == 3){
                //物流
                holder.item_number.setTextColor(res.getColor(R.color.index_four));
                holder.item_title.setText("物流信息");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_orange));
            }
            if(position == 4){
                //新闻
                holder.item_number.setTextColor(res.getColor(R.color.index_four));
                holder.item_title.setText("已发头条");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_brown));
            }
            if(position == 5){
                //订单
                holder.item_number.setTextColor(res.getColor(R.color.index_five));
                holder.item_title.setText("完成订单");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_blue_dark));
            }
            if(position == 6){
                //关注
                holder.item_number.setTextColor(res.getColor(R.color.index_six));
                holder.item_title.setText("我的粉丝");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_blue_dark));
            }
            if(position == 7){
                //排行
                holder.item_number.setTextColor(res.getColor(R.color.index_seven));
                holder.item_title.setText("名企排行");
                holder.item_number.setBackground(res.getDrawable(R.drawable.md_h_red));
            }
        }

        return convertView;
    }

    class ViewHolder {
        TextView item_number;
        TextView item_title;
    }
}
