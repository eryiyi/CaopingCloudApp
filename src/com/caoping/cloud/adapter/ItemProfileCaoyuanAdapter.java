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
import com.caoping.cloud.entiy.CpObj;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 * 草坪
 */
public class ItemProfileCaoyuanAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<CpObj> lists;
    private Context mContect;
    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemProfileCaoyuanAdapter(List<CpObj> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_profile_caoyuan, null);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.company = (TextView) convertView.findViewById(R.id.company);
            holder.money = (TextView) convertView.findViewById(R.id.money);
            holder.saleCount = (TextView) convertView.findViewById(R.id.saleCount);
            holder.btn_address = (TextView) convertView.findViewById(R.id.btn_address);

            holder.sm_v1 = (ImageView) convertView.findViewById(R.id.sm_v1);
            holder.sm_v2 = (ImageView) convertView.findViewById(R.id.sm_v2);
            holder.sm_v4 = (ImageView) convertView.findViewById(R.id.sm_v4);
            holder.sm_v5 = (ImageView) convertView.findViewById(R.id.sm_v5);
            holder.sm_v6 = (ImageView) convertView.findViewById(R.id.sm_v6);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        CpObj cell = lists.get(position);
        if (cell != null) {
            holder.title.setText(cell.getCloud_caoping_title()==null?"":cell.getCloud_caoping_title());
            holder.company.setText(cell.getEmp_name()==null?"":cell.getEmp_name());
            if(!StringUtil.isNullOrEmpty(cell.getCloud_caoping_address())){
                holder.btn_address.setText(cell.getCloud_caoping_address());
                holder.btn_address.setVisibility(View.VISIBLE);
            }else {
                holder.btn_address.setVisibility(View.GONE);
            }
            holder.money.setText("￥"+cell.getCloud_caoping_prices());
            if(!StringUtil.isNullOrEmpty(cell.getCloud_caoping_pic())){
                //说明有图片
                String[] arras = cell.getCloud_caoping_pic().split(",");
                if(arras != null && arras.length > 0){
                    imageLoader.displayImage(arras[0], holder.cover, CaopingCloudApplication.txOptions, animateFirstListener);
                    holder.cover.setVisibility(View.VISIBLE);
                }else {
                    holder.cover.setVisibility(View.GONE);
                }
            }else{
                holder.cover.setVisibility(View.GONE);
            }
            holder.saleCount.setText("销量"+cell.getSale_count());
        }


        return convertView;
    }

    static class ViewHolder {
        ImageView cover;
        TextView title;
        TextView company;
        TextView money;
        TextView saleCount;
        TextView btn_address;

        ImageView sm_v1;
        ImageView sm_v2;
        ImageView sm_v4;
        ImageView sm_v5;
        ImageView sm_v6;
    }
}
