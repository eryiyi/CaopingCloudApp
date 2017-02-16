package com.caoping.cloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.GoodsComment;
import com.caoping.cloud.ui.GalleryUrlActivity;
import com.caoping.cloud.util.StringUtil;
import com.caoping.cloud.widget.PictureGridview;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.List;

/**
 * author: ${zhanghailong}
 * Date: 2015/3/9
 * Time: 8:42
 * 评价 商品的
 */
public class ItemCommentAdapter extends BaseAdapter {
    private ViewHolder holder;
    private List<GoodsComment> findEmps;
    private Context mContext;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private OnClickContentItemListener onClickContentItemListener;

    public void setOnClickContentItemListener(OnClickContentItemListener onClickContentItemListener) {
        this.onClickContentItemListener = onClickContentItemListener;
    }

    public ItemCommentAdapter(List<GoodsComment> findEmps, Context mContext) {
        this.findEmps = findEmps;
        this.mContext = mContext;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_goods_comment, null);
            holder.cover = (ImageView) convertView.findViewById(R.id.cover);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.dateline = (TextView) convertView.findViewById(R.id.dateline);
            holder.cont = (TextView) convertView.findViewById(R.id.cont);
            holder.cloud_caoping_title = (TextView) convertView.findViewById(R.id.cloud_caoping_title);
            holder.gridview_detail_picture = (PictureGridview) convertView.findViewById(R.id.lstv);
            holder.startNumber = (RatingBar) convertView.findViewById(R.id.startNumber);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final GoodsComment cell = findEmps.get(position);
        if (findEmps != null) {
            if(!StringUtil.isNullOrEmpty(cell.getCover())){
                imageLoader.displayImage(cell.getCover(), holder.cover, CaopingCloudApplication.txOptions, animateFirstListener);
            }
            holder.name.setText(cell.getNickName());
            holder.dateline.setText(cell.getDateline());
            holder.cont.setText(cell.getContent()==null?"":cell.getContent());
            if(!StringUtil.isNullOrEmpty(cell.getStarNumber())){
                holder.startNumber.setRating(Float.valueOf(cell.getStarNumber() == null ? "0" : cell.getStarNumber()));
            }
            if (!StringUtil.isNullOrEmpty(cell.getComment_pic())) {
                //说明有图片
                final String[] picUrls = cell.getComment_pic().split(",");//图片链接切割
                if (picUrls.length > 0) {
                    //有多张图
                    holder.gridview_detail_picture.setSelector(new ColorDrawable(Color.TRANSPARENT));
                    holder.gridview_detail_picture.setVisibility(View.VISIBLE);
                    holder.gridview_detail_picture.setAdapter(new ImageGridViewAdapter(picUrls, mContext));
                    holder.gridview_detail_picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(mContext, GalleryUrlActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                            intent.putExtra("img_urls", picUrls);
                            intent.putExtra("position", position);
                            mContext.startActivity(intent);
                        }
                    });
                }else{
                    holder.gridview_detail_picture.setVisibility(View.GONE);
                }
            }else {
                holder.gridview_detail_picture.setVisibility(View.GONE);
            }
            if(!StringUtil.isNullOrEmpty(cell.getCloud_caoping_title())){
                holder.cloud_caoping_title.setText(cell.getCloud_caoping_title());
            }

            holder.cloud_caoping_title.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickContentItemListener.onClickContentItem(position, 0, null);
                }
            });

        }
        return convertView;
    }

    class ViewHolder {
        ImageView cover;
        TextView name;
        TextView dateline;
        TextView cont;
        TextView cloud_caoping_title;
        PictureGridview gridview_detail_picture;
        RatingBar startNumber;
    }
}
