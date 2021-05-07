package com.news.newsapp.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.news.newsapp.R;
import com.news.newsapp.adapter.BannerAdapter;
import com.news.newsapp.models.Article;
import com.news.newsapp.models.top_news.AllTopNewsResponse;
import com.news.newsapp.utils.ApiClient;
import com.news.newsapp.utils.Constants;
import com.news.newsapp.utils.UrlRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
   List<Article> articleList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search__new);
    }
    void loadTopnews()
    {
        Call<AllTopNewsResponse> call;
        call= ApiClient.getInstance().create(UrlRequest.class).searchNews(getString(R.string.api_key),terms, "en");


        Log.e("category",terms+"");
        Log.e("lan", Constants.LANGUAGE+"");
        call.enqueue(new Callback<AllTopNewsResponse>() {
            @Override
            public void onResponse(Call<AllTopNewsResponse> call, Response<AllTopNewsResponse> response) {
                Log.e("response_status",response.code()+"");
                Log.e("response_body",response.body()+"");
                if(response.isSuccessful())
                {
                    AllTopNewsResponse allTopNewsResponse=response.body();
                    Log.e("news_res",allTopNewsResponse.getStatus()+"");
                    if(allTopNewsResponse.getStatus().equalsIgnoreCase("ok"))
                    {
                        if(!allTopNewsResponse.getArticles().isEmpty())
                        { if(!isBanner)
                        {
                            List<Article> list=allTopNewsResponse.getArticles();
                            for(int i=0;i<limit;i++)
                            {
                                Log.e("banner_list",list.get(i).getTitle()+"");

                                bannerList.add(list.get(i));
                            }
                            bannerAdapter=new BannerAdapter(getActivity(),bannerList);
                            banners_rv.setAdapter(bannerAdapter);
                            isBanner=true;
                        }

                            if (currentPage != PAGE_START)
                                try {
                                    newsAdapter.removeLoadingFooter();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            parseReviews(allTopNewsResponse);
                            if (currentPage != TOTAL_PAGES)
                                newsAdapter.addLoadingFooter();
                            else isLastPage = true;
                            isLoading = false;
                        }

                    }
                }
                else
                {

                }
            }

            @Override
            public void onFailure(Call<AllTopNewsResponse> call, Throwable t) {
                Log.e("news_fragment",t.getMessage()+"");
                animationView.pauseAnimation();
                loader.setVisibility(View.GONE);
            }
        });
    }
    void parseReviews(AllTopNewsResponse businessReviewsResponse)
    {
//        if (businessReviewsResponse.getData().getTotalNumberOfPage() != 0)
//        {
//            TOTAL_PAGES = businessReviewsResponse.getData().getTotalNumberOfPage();
//
//        }
//        else {
//            TOTAL_PAGES = 1;
//        }
        Log.e("total_pages_combos", TOTAL_PAGES + "");
        TOTAL_PAGES = 1;

        articleList=businessReviewsResponse.getArticles();
        if (currentPage == PAGE_START) {
            try {
                if (!articleList.isEmpty()) {
                    List<Article> l=new ArrayList<>();
                    for(int i=limit;i<articleList.size();i++)
                    {
                        l.add(articleList.get(i));
                    }
                    newsAdapter.addAll(l);

                }
                else
                {
//                    business_details_review.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
//                        progressBar.setVisibility(View.GONE);

            }

        }
        else {
            try {
                newsAdapter.addAll(articleList);

//                        progressBar.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
//                        progressBar.setVisibility(View.GONE);

            }
        }

    }

}