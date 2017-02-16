package com.caoping.cloud.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import com.caoping.cloud.CaopingCloudApplication;
import com.caoping.cloud.R;
import com.caoping.cloud.entiy.Member;
import com.caoping.cloud.pinyin.PingYinUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhl on 2016/8/22.
 */
public class ContactAdapter extends BaseAdapter implements SectionIndexer {
    private Context mContext;
    private List<Member> mNicks = new ArrayList<Member>();

    static ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
    static ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类

    @SuppressWarnings("unchecked")
    public ContactAdapter(Context mContext, List<Member> nicks) {
        this.mContext = mContext;
        this.mNicks = nicks;
        // 排序(实现了中英文混排)
//        String[] arrays = new String[mNicks.size()];
//        for(int i=0;i<mNicks.size();i++){
//            arrays[i]=mNicks.get(i).getMm_emp_nickname();
//        }
//        Arrays.sort(mNicks, new PinyinComparator());
    }

    @Override
    public int getCount() {
        return mNicks.size();
    }

    @Override
    public Object getItem(int position) {
        return mNicks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final String nickName = mNicks.get(position).getEmpName();
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.contact_item, null);
            viewHolder = new ViewHolder();
            viewHolder.tvCatalog = (TextView) convertView
                    .findViewById(R.id.contactitem_catalog);
            viewHolder.ivAvatar = (ImageView) convertView
                    .findViewById(R.id.contactitem_avatar_iv);
            viewHolder.tvNick = (TextView) convertView
                    .findViewById(R.id.contactitem_nick);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String catalog = PingYinUtil.converterToFirstSpell(nickName)
                .substring(0, 1);
        if (position == 0) {
            viewHolder.tvCatalog.setVisibility(View.VISIBLE);
            viewHolder.tvCatalog.setText(catalog);
            imageLoader.displayImage( mNicks.get(position).getEmpCover(),  viewHolder.ivAvatar, CaopingCloudApplication.txOptions, animateFirstListener);
        } else {
            String lastCatalog = PingYinUtil.converterToFirstSpell(
                    mNicks.get(position - 1).getLevelName()).substring(0, 1);
            if (catalog.equals(lastCatalog)) {
                viewHolder.tvCatalog.setVisibility(View.GONE);
            } else {
                viewHolder.tvCatalog.setVisibility(View.VISIBLE);
                viewHolder.tvCatalog.setText(catalog);
            }
            imageLoader.displayImage( mNicks.get(position ).getEmpCover(),  viewHolder.ivAvatar, CaopingCloudApplication.txOptions, animateFirstListener);
        }


        viewHolder.tvNick.setText(nickName);
        return convertView;
    }

    static class ViewHolder {
        TextView tvCatalog;// 目录
        ImageView ivAvatar;// 头像
        TextView tvNick;// 昵称
    }

    @Override
    public int getPositionForSection(int section) {
        for (int i = 0; i < mNicks.size(); i++) {
            String l = PingYinUtil.converterToFirstSpell(mNicks.get(i).getEmpName())
                    .substring(0, 1);
            char firstChar = l.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    @Override
    public Object[] getSections() {
        return null;
    }
}
