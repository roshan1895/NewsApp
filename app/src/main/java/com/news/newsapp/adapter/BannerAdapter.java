package com.news.newsapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.news.newsapp.R;
import com.news.newsapp.models.Article;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.MyViewHolder> {
    Context context;
    List<Article> list;
    public BannerAdapter(Context context,List<Article> list)
    {
        this.context=context;
        this.list=list;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.banner_item_layout,parent,false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(list.get(position).getUrlToImage()).into(holder.banner_img);
        holder.banner_title.setText(list.get(position).getTitle());
        holder.banner_source.setText(list.get(position).getSource().getName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView banner_img;
        TextView banner_title,banner_source;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            banner_img=itemView.findViewById(R.id.banner_img);
            banner_title=itemView.findViewById(R.id.banner_title);
            banner_source=itemView.findViewById(R.id.banner_source);
        }
    }
}
