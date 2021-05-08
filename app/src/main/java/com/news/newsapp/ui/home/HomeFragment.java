package com.news.newsapp.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.tabs.TabLayout;
import com.news.newsapp.R;
import com.news.newsapp.adapter.BannerAdapter;
import com.news.newsapp.adapter.SliderAdapter;
import com.news.newsapp.adapter.TopNewsAdapter;
import com.news.newsapp.models.Article;
import com.news.newsapp.models.top_news.AllTopNewsResponse;
import com.news.newsapp.utils.ApiClient;
import com.news.newsapp.utils.Constants;
import com.news.newsapp.utils.ExpandingViewPagerTransformer;
import com.news.newsapp.utils.PaginationAdapterCallback;
import com.news.newsapp.utils.PaginationScrollListener;
import com.news.newsapp.utils.UrlRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements PaginationAdapterCallback {

    private HomeViewModel homeViewModel;
    TabLayout tab_home;
    RecyclerView news_rv;
    LottieAnimationView animationView;
    RelativeLayout loader;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    RecyclerView newsRv;
    ProgressBar progressBar;
    LinearLayoutManager layoutManager;
    TopNewsAdapter newsAdapter;
    List<Article> articleList;
    String category="";
    String tabName="all";
    ViewPager pager;
    int limit=5;
    boolean isBanner=false;
    List<Article> bannerList;
    SliderAdapter sliderAdapter;
    RecyclerView banners_rv;
    BannerAdapter bannerAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tab_home=getView().findViewById(R.id.tab_home);
        loader=getView().findViewById(R.id.loader);
        animationView=getView().findViewById(R.id.animationView);
        newsRv=getView().findViewById(R.id.news_rv);
        banners_rv=getView().findViewById(R.id.banners_rv);
        banners_rv.hasFixedSize();
        banners_rv.setLayoutManager(new LinearLayoutManager(getActivity(),RecyclerView.HORIZONTAL,false));
        bannerList=new ArrayList<>();
//        pager=getView().findViewById(R.id.banner_pager);
//        pager.setPadding(180, 0, 180, 0);
//        pager.setPageMargin(16);
//        //pager.setPadding(viewPagerPadding+80, viewPagerPadding+80, 0, viewPagerPadding+80);
//        ExpandingViewPagerTransformer pagerTransformer=new ExpandingViewPagerTransformer();
//        pager.setPageTransformer(true,pagerTransformer);
        setupTab();
        newsRv.hasFixedSize();
        layoutManager=new LinearLayoutManager(getActivity());
        newsRv.setLayoutManager(layoutManager);
//        newsAdapter=new TopNewsAdapter(getActivity(),HomeFragment.this);
        newsRv.setAdapter(newsAdapter);
        newsRv.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading=true;
                currentPage+=1;
                loadTopnews();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        loadTopnews();

    }
    void setupTab()
    {
        tab_home.addTab(tab_home.newTab().setText("All"));
        tab_home.addTab(tab_home.newTab().setText("Business"));
        tab_home.addTab(tab_home.newTab().setText("Entertainment"));
        tab_home.addTab(tab_home.newTab().setText("General"));
        tab_home.addTab(tab_home.newTab().setText("Health"));
        tab_home.addTab(tab_home.newTab().setText("Science"));
        tab_home.addTab(tab_home.newTab().setText("Sports"));
        tab_home.addTab(tab_home.newTab().setText("Technology"));
        tab_home.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
Log.e("tab_pos",tab.getPosition()+"");
              switch (tab.getPosition())
              {
                  case 0: tabName="all";
                      articleList=new ArrayList<>();
                      if(newsAdapter!=null)
                      {
                          newsAdapter.clear();
                      }
                      newsAdapter.clear();
                      animationView.playAnimation();
                      loader.setVisibility(View.VISIBLE);
                      bannerList=new ArrayList<>();
                      isBanner=false;
                      loadTopnews();
                      break;
                  case 1: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 2: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 3: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 4: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 5: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 6: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
                  case 7: tabName=tab.getText().toString();
                      setData(tab.getText().toString());
                      break;
              }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    void setData(String name)
    {   Log.e("tab_name",name+"");
        articleList=new ArrayList<>();
        newsAdapter.clear();
        animationView.playAnimation();
        loader.setVisibility(View.VISIBLE);
         category=name;
        bannerList=new ArrayList<>();
        isBanner=false;
         loadTopnews();

    }
    void loadTopnews()
    {
        Call<AllTopNewsResponse> call;
        if(tabName.equalsIgnoreCase("all"))
        {
            call= ApiClient.getInstance().create(UrlRequest.class).topNewsByCat(getString(R.string.api_key),"", "en");

        }
        else
            call= ApiClient.getInstance().create(UrlRequest.class).topNewsByCat(getString(R.string.api_key),category, "en");

        Log.e("category",category+"");
         Log.e("lan",Constants.LANGUAGE+"");
        call.enqueue(new Callback<AllTopNewsResponse>() {
            @Override
            public void onResponse(Call<AllTopNewsResponse> call, Response<AllTopNewsResponse> response) {
               animationView.pauseAnimation();
               loader.setVisibility(View.GONE);
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

    @Override
    public void retryPageLoad() {

    }
}