package com.caoping.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.CarLength;

import java.util.List;

/**
 * author: zhanghl
 * Date: 2014/8/9
 * Time: 11:07
 * 车长
 */
public class ItemCarLengthAdapter extends BaseAdapter {
    private ViewHolder holder;
    private Context context;
    private List<CarLength> list;
    private LayoutInflater inflater;


    public ItemCarLengthAdapter(Context context, List<CarLength> list) {
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
            convertView = inflater.inflate(R.layout.item_car_length, null);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CarLength commentContent = list.get(position);
        if (commentContent != null) {
            holder.title.setText(commentContent.getCar_length_name());
        }

        return convertView;
    }

    class ViewHolder {
        TextView title;
    }
}
