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
import com.caoping.cloud.entiy.DianPuFavour;
import com.caoping.cloud.util.StringUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * 收藏
 */
public class ItemFavourDianpuAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<DianPuFavour> findEmps;
    private Context mContext;
    Resources res;

    private OnClickContentItemListener onClickContentItemListener;
    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    public ItemFavourDianpuAdapter(List<DianPuFavour> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
        res = mContext.getResources();
    }

    @Override
    public int getCount() {
        return findEmps.size();
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
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_favour, null);
            holder.item_pic = (ImageView) convertView.findViewById(R.id.item_pic);
            holder.item_title = (TextView) convertView.findViewById(R.id.item_title);
            holder.item_prices = (TextView) convertView.findViewById(R.id.item_prices);
            holder.item_in = (ImageView) convertView.findViewById(R.id.item_in);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DianPuFavour cell = findEmps.get(position);//获得元素
        if (cell != null) {
            if (!StringUtil.isNullOrEmpty(cell.getCompany_pic())) {
                String[] picarr = cell.getCompany_pic().split(",");
                if (picarr != null) {
                    imageLoader.displayImage(picarr[0], holder.item_pic, CaopingCloudApplication.options, animateFirstListener);
                }
            }
            holder.item_title.setText(cell.getCompany_name());
            holder.item_prices.setText(cell.getEmp_name());

            holder.item_in.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 1, null);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        ImageView item_pic;//商品封面
        TextView item_title;//商品名字
        TextView item_prices;//价格
        ImageView item_in;
    }
}
