package com.news.newsapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.news.newsapp.R;
import com.news.newsapp.models.Article;
import com.news.newsapp.ui.SettingBottomMenu;
import com.news.newsapp.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

public class TopNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Article> articleList;
    PaginationAdapterCallback mCallback;
    private static final int ITEM1 = 0;
    private static final int ITEM2 = 1;

    private static final int LOADING = 2;
    private boolean retryPageLoad = false;
    private boolean isLoadingAdded = false;
    private String errorMsg;

    public interface OnItemClickListener
    {
        void onItemClick(int pos,String title,String source,String datetime,String desc,String content,String url);
        void onMoreClick(int pos);
    }
    OnItemClickListener onItemClickListener;
    public TopNewsAdapter(Context context,PaginationAdapterCallback mCallback,OnItemClickListener onItemClickListener)
    {
        this.context=context;
        articleList=new ArrayList<>();
        this.mCallback=mCallback;
        this.onItemClickListener=onItemClickListener;
        Log.e("adapter_callback","running");
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_feed_layout, parent, false);
            return new ArticlesHolder(view);
        }
       else if (viewType == ITEM2) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_without_image, parent, false);
            return new ArticlesNewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false);
            return new LoadingHolder(view);
        }     }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ArticlesHolder) {
            showArticle((ArticlesHolder) holder, position);

        } else if (holder instanceof LoadingHolder) {
            showLoading((LoadingHolder) holder, position);
        }
        else if(holder instanceof ArticlesNewHolder)
        {
            showNewArticle((ArticlesNewHolder)holder,position);
        }
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }
    public class LoadingHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        ProgressBar progressBar;
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingHolder(@NonNull View itemView) {
            super(itemView);
            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);
            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:
                    showRetry(false, null);
                    mCallback.retryPageLoad();
                    break;
            }
        }
    }
    public class ArticlesHolder extends RecyclerView.ViewHolder
    {  RoundedImageView profile_image;
       TextView newsTitle,newsSource,newsDateTime;
       ImageButton more_btn;

        public ArticlesHolder(@NonNull View itemView) {
            super(itemView);
            profile_image=itemView.findViewById(R.id.profile_image);
            newsTitle=itemView.findViewById(R.id.news_title);
            newsSource=itemView.findViewById(R.id.news_source);
            newsDateTime=itemView.findViewById(R.id.news_datetime);
            more_btn=itemView.findViewById(R.id.more_btn);

        }
    }
    public class ArticlesNewHolder extends RecyclerView.ViewHolder
    { TextView newsTitle,newsSource,newsDateTime;
    ImageButton more_btn;

        public ArticlesNewHolder(@NonNull View itemView) {
            super(itemView);
            newsTitle=itemView.findViewById(R.id.news_title);
            newsSource=itemView.findViewById(R.id.news_source);
            newsDateTime=itemView.findViewById(R.id.news_datetime);
            more_btn=itemView.findViewById(R.id.more_btn);

        }
    }
    void showLoading(LoadingHolder holder, int position)
    {
        if (retryPageLoad) {
            holder.mErrorLayout.setVisibility(View.VISIBLE);
            holder.mProgressBar.setVisibility(View.GONE);

            holder.mErrorTxt.setText(
                    errorMsg != null ?
                            errorMsg :
                            context.getString(R.string.error_msg_unknown));

        } else {
            holder.mErrorLayout.setVisibility(View.GONE);
            holder.mProgressBar.setVisibility(View.VISIBLE);
        }
    }
    void showArticle(ArticlesHolder holder,int pos)
    {  if(articleList.get(pos).getUrlToImage()!=null)
       {
           Glide.with(context).load(articleList.get(pos).getUrlToImage()).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.profile_image);

       }

        holder.newsTitle.setText(articleList.get(pos).getTitle());
        holder.newsSource.setText(articleList.get(pos).getSource().getName());
        holder.newsDateTime.setText(articleList.get(pos).getPublishedAt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(pos,articleList.get(pos).getTitle(),articleList.get(pos).getSource().getName(),articleList.get(pos).getPublishedAt(),articleList.get(pos).getDescription(),articleList.get(pos).getContent(),articleList.get(pos).getUrl());
            }
        });
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onMoreClick(pos);
            }
        });

    }
    void showNewArticle(ArticlesNewHolder holder,int pos)
    {

        holder.newsTitle.setText(articleList.get(pos).getTitle());
        holder.newsSource.setText(articleList.get(pos).getSource().getName());
        holder.newsDateTime.setText(articleList.get(pos).getPublishedAt());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(pos,articleList.get(pos).getTitle(),articleList.get(pos).getSource().getName(),articleList.get(pos).getPublishedAt(),articleList.get(pos).getDescription(),articleList.get(pos).getContent(),articleList.get(pos).getUrl());
            }
        });
        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onMoreClick(pos);
            }
        });

    }

    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(articleList.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }
    public int getItemViewType(int position) {
        return  (position==articleList.size()-1&&isLoadingAdded)?LOADING:(articleList.get(position).getUrlToImage()!=null?ITEM1:ITEM2);
//        return (position == articleList.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    public void add(Article r) {
        articleList.add(r);
        notifyItemInserted(articleList.size() - 1);
    }

    public void addAll(List<Article> moveResults) {
        for (Article result : moveResults) {
            add(result);
        }
    }

    public void remove(Article r) {
        int position = articleList.indexOf(r);
        if (position > -1) {
            articleList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public Article getItem(int position) {
        return articleList.get(position);
    }


    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Article() );
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = articleList.size() - 1;
        Article result = getItem(position);

        if (result != null) {
            articleList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public  void updateCollections(Context context,List<Article> catList )
    {    Log.e("cat_array_list",catList+"");
        this.context=context;
        Log.e("cat_arr","running");
        final DiffUtil.DiffResult result=DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return articleList.size();
            }

            @Override
            public int getNewListSize() {
                return catList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return articleList.get(oldItemPosition).getTitle().equalsIgnoreCase(catList.get(newItemPosition).getTitle());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Article new_main_cat=articleList.get(oldItemPosition);
                Article old_main_cat=catList.get(newItemPosition);
                return new_main_cat.getTitle().equalsIgnoreCase(old_main_cat.getTitle());
            }
        });
        this.articleList=catList;
        result.dispatchUpdatesTo(this);
    }

}
