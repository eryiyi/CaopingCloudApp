package com.caoping.cloud.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.caoping.cloud.R;
import com.caoping.cloud.adapter.AnimateFirstDisplayListener;
import com.caoping.cloud.base.BaseFragment;
import com.caoping.cloud.ui.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by zhl on 2016/10/16.
 */
public class FindFragment extends BaseFragment  implements View.OnClickListener {
    ImageLoader imageLoader = ImageLoader.getInstance();//图片加载类
    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    private View view;
    private Resources res;
    private TextView title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.find_fragment, null);
        res = getActivity().getResources();

        view.findViewById(R.id.liner_cyphb).setOnClickListener(this);
        view.findViewById(R.id.liner_tjcp).setOnClickListener(this);
        view.findViewById(R.id.liner_nearby_cy).setOnClickListener(this);
        view.findViewById(R.id.liner_head_news).setOnClickListener(this);
        view.findViewById(R.id.liner_wlxx).setOnClickListener(this);
        view.findViewById(R.id.liner_czgy).setOnClickListener(this);
        view.findViewById(R.id.liner_czjx).setOnClickListener(this);
        view.findViewById(R.id.back).setVisibility(View.GONE);
        title = (TextView) view.findViewById(R.id.title);
        title.setText("发现");
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.liner_cyphb:
            {
                //排行榜
                Intent intent = new Intent(getActivity(), ListMingqiActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_tjcp:
            {
                //推荐产品
                Intent intent = new Intent(getActivity(), ListTuijianProduceActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_nearby_cy:
            {
                //附近草原
                Intent intent = new Intent(getActivity(), ListCaoyuanActivity.class);
                intent.putExtra("tmpNearby", "1");
                startActivity(intent);
            }
                break;
            case R.id.liner_head_news:
            {
                //草原头条
                Intent intent = new Intent(getActivity(), NewsActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_wlxx:
            {
                //物流信息
                Intent intent = new Intent(getActivity(), ListWuliuActivity.class);
                startActivity(intent);
            }
                break;
            case R.id.liner_czgy:
            {
                //草种供应
                Intent intent = new Intent(getActivity(), ListCaozhongActivity.class);
                intent.putExtra("tmpNearby", "1");
                startActivity(intent);
            }
                break;
            case R.id.liner_czjx:
            {
                //草种机械
                Intent intent = new Intent(getActivity(), ListJixieActivity.class);
                intent.putExtra("cloud_jixie_use_id", "");
                intent.putExtra("tmpNearby", "1");
                startActivity(intent);
            }
                break;
        }
    }
}
