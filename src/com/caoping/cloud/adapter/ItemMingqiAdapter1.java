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
import com.caoping.cloud.entiy.Company;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * Created by Administrator on 2015/5/27.
 * 名企排行
 */
public class ItemMingqiAdapter1 extends BaseAdapter {
    private ViewHolder holder;
    private List<Company> lists;
    private Context mContect;
    Resources res;

    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }


    public ItemMingqiAdapter1(List<Company> lists, Context mContect) {
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
            convertView = LayoutInflater.from(mContect).inflate(R.layout.item_mingqi1, null);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.company = (TextView) convertView.findViewById(R.id.company);
            holder.count = (TextView) convertView.findViewById(R.id.count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Company cell = lists.get(position);
        if (cell != null) {
            imageLoader.displayImage(cell.getCompany_pic(), holder.cover, CaopingCloudApplication.options, animateFirstListener);
            holder.name.setText(cell.getPname() + "·" + cell.getCityName());
            holder.title.setText(String.valueOf(position + 1));
            holder.company.setText(cell.getCompany_name());
            holder.count.setText("电话:"+cell.getCompany_tel());
        }

        holder.title.setText(String.valueOf(position+1));
        return convertView;
    }

    class ViewHolder {
        ImageView cover;
        TextView title;
        TextView name;
        TextView company;
        TextView count;
    }
}
