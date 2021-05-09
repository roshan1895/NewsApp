package com.news.newsapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.news.newsapp.R;
import com.news.newsapp.adapter.BannerAdapter;
import com.news.newsapp.adapter.SliderAdapter;
import com.news.newsapp.adapter.TopNewsAdapter;
import com.news.newsapp.models.Article;
import com.news.newsapp.models.top_news.AllTopNewsResponse;
import com.news.newsapp.ui.home.HomeFragment;
import com.news.newsapp.utils.ApiClient;
import com.news.newsapp.utils.Constants;
import com.news.newsapp.utils.PaginationAdapterCallback;
import com.news.newsapp.utils.PaginationScrollListener;
import com.news.newsapp.utils.UrlRequest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback, TopNewsAdapter.OnItemClickListener {
    Toolbar toolbar;
    ImageButton search_btn;
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
    String category="general";
    String tabName="all";
    ViewPager pager;
    int limit=5;
    boolean isBanner=false;
    List<Article> bannerList;
    SliderAdapter sliderAdapter;
    RecyclerView banners_rv;
    BannerAdapter bannerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=findViewById(R.id.toolbar);
        search_btn=findViewById(R.id.search_btn);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });  tab_home=findViewById(R.id.tab_home);
        loader=findViewById(R.id.loader);
        animationView=findViewById(R.id.animationView);
        newsRv=findViewById(R.id.news_rv);
        banners_rv=findViewById(R.id.banners_rv);
        banners_rv.hasFixedSize();
        banners_rv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false));
        bannerList=new ArrayList<>();
        setupTab();
        newsRv.hasFixedSize();
        layoutManager=new LinearLayoutManager(this);
        newsRv.setLayoutManager(layoutManager);
        newsAdapter=new TopNewsAdapter(this, MainActivity.this,MainActivity.this);
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
        tab_home.addTab(tab_home.newTab().setText("General").setIcon(R.drawable.ic_headlines));
        tab_home.addTab(tab_home.newTab().setText("Business").setIcon(R.drawable.nav_business));
        tab_home.addTab(tab_home.newTab().setText("Entertainment").setIcon(R.drawable.nav_entertainment));
        tab_home.addTab(tab_home.newTab().setText("Health").setIcon(R.drawable.nav_health));
        tab_home.addTab(tab_home.newTab().setText("Science").setIcon(R.drawable.nav_science));
        tab_home.addTab(tab_home.newTab().setText("Sports").setIcon(R.drawable.nav_sports));
        tab_home.addTab(tab_home.newTab().setText("Technology").setIcon(R.drawable.nav_tech));
        tab_home.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("tab_pos",tab.getPosition()+"");
                switch (tab.getPosition())
                {
                    case 0:
                        setData(tab.getText().toString());
                        break;
                    case 1:
                        setData(tab.getText().toString());
                        break;
                    case 2:
                        setData(tab.getText().toString());
                        break;
                    case 3:
                        setData(tab.getText().toString());
                        break;
                    case 4:
                        setData(tab.getText().toString());
                        break;
                    case 5:
                        setData(tab.getText().toString());
                        break;
                    case 6:
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
        Call<AllTopNewsResponse>  call= ApiClient.getInstance().create(UrlRequest.class).topNewsByCat(getString(R.string.api_key),category, "en");

        Log.e("category",category+"");
        Log.e("lan", Constants.LANGUAGE+"");
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
                            bannerAdapter=new BannerAdapter(getApplicationContext(),bannerList);
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


    @Override
    public void onItemClick(int pos, String title, String source, String datetime, String desc, String content, String url) {
        Intent intent=new Intent(MainActivity.this,NewsDetailsScrollingActivity.class);
        intent.putExtra("news_title",title);
        intent.putExtra("news_source",source);
        intent.putExtra("news_datetime",datetime);
        intent.putExtra("news_desc",desc);
        intent.putExtra("news_content",content);
        intent.putExtra("news_url",url);
        startActivity(intent);
    }

    @Override
    public void onMoreClick(int pos) {
        SettingBottomMenu settingBottomMenu=new SettingBottomMenu();
        settingBottomMenu.show(getSupportFragmentManager(),"SettingBottomSheet");
    }
}