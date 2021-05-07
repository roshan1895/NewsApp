package com.news.newsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.news.newsapp.R;
import com.news.newsapp.models.Article;

import java.util.List;


public class SliderAdapter extends PagerAdapter {
    List<Article> sliderImageList;
    Context context;
    private LayoutInflater layoutInflater;
    RequestManager glide;
    private int pos=0;
    public interface OnItemClickListener {
        void onBannerClick(int position, Long type, Long catid, String url);


    }
    OnItemClickListener onItemClickListener;

    public SliderAdapter(Context context, List<Article> sliderImageList)
    {   this.context=context;
        this.sliderImageList=sliderImageList;
        layoutInflater= LayoutInflater.from(context);
        glide= Glide.with(context);


    }
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView=layoutInflater.inflate(R.layout.banner_item_layout,container,false);
        ImageView banner_img=itemView.findViewById(R.id.banner_img);
        TextView banner_title=itemView.findViewById(R.id.banner_title);
        TextView banner_source=itemView.findViewById(R.id.banner_source);
        glide.load(sliderImageList.get(position).getUrlToImage()).into(banner_img);
        banner_title.setText(sliderImageList.get(position).getTitle());
        banner_source.setText(sliderImageList.get(position).getSource().getName());

        container.addView(itemView);
        return itemView;
    }


    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }



    @Override
    public int getCount() {
        return sliderImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }



}
