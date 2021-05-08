package com.news.newsapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.news.newsapp.R;
import com.news.newsapp.adapter.BannerAdapter;
import com.news.newsapp.adapter.SearchNewAdapter;
import com.news.newsapp.models.Article;
import com.news.newsapp.models.top_news.AllTopNewsResponse;
import com.news.newsapp.utils.ApiClient;
import com.news.newsapp.utils.Connection_Check;
import com.news.newsapp.utils.Constants;
import com.news.newsapp.utils.PaginationAdapterCallback;
import com.news.newsapp.utils.PaginationScrollListener;
import com.news.newsapp.utils.UrlRequest;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity implements PaginationAdapterCallback, SearchNewAdapter.OnItemClickListener {
   List<Article> articleList;
    EditText editText;
    LinearLayout noResultLayout;
    ImageView clearQueryImageView,VoiceSearchImageView;
    LinearLayoutManager layoutManager;
    Button clear_btn;
    ProgressBar progress_bar;
    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES;
    private int currentPage = PAGE_START;
    boolean isPageMore=false;
    SearchNewAdapter searchNewAdapter;
    String query;
    final int VOICE_SEARCH_CODE = 3012;
    ImageView backImg;
    FrameLayout toggle_frame;
    RecyclerView searchRv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__search__new);
        editText = findViewById(R.id.search_edit_text);
        noResultLayout = findViewById(R.id.no_result_layout);
        clearQueryImageView = findViewById(R.id.clear_search_query);
        VoiceSearchImageView = findViewById(R.id.voice_search_query);
        progress_bar=findViewById(R.id.progress_bar);
        backImg=findViewById(R.id.back_image_view);
        toggle_frame=findViewById(R.id.toggle_frame);
        searchRv=findViewById(R.id.search_list);
        articleList=new ArrayList<>();
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                query=s.toString();
                toggleImageView(query);
                searchQuery(query);
            }
        });
        clearQueryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        VoiceSearchImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
        setSearchRv();

    }
    void searchQuery(String str)
    {
        Log.e("str_",str+"");
        Log.e("str_length",str.length()+"");
        if(!TextUtils.isEmpty(str))
        {
            if(str.length()>=2)
            {

                if(searchRv.getVisibility()==View.GONE)
                {
                    searchRv.setVisibility(View.VISIBLE);
                }
                if(Connection_Check.checkConnection(getApplicationContext()))
                {
                    showProgress();
                    if(searchNewAdapter!=null)
                    {
                        searchNewAdapter.clear();
                    }
//                        findDatumList.clear();
                    loadTopnews(str);
                }
                else
                {    if(searchNewAdapter!=null)
                {
                    searchNewAdapter.clear();
                }
                }
            }
            else
            {
                if(searchNewAdapter!=null)
                {
                    searchNewAdapter.clear();
                }

            }

        }
        else
        {
            if(searchNewAdapter!=null)
            {
                searchNewAdapter.clear();
            }
//            setRecentSearch();
        }
    }
    void showProgress()
    {
        clearQueryImageView.setVisibility(View.INVISIBLE);
        progress_bar.setVisibility(View.VISIBLE);
    }
    void hideProgress()
    {
        progress_bar.setVisibility(View.INVISIBLE);
        clearQueryImageView.setVisibility(View.VISIBLE);
    }
    void  setSearchRv()
    {
        searchRv.hasFixedSize();
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        searchRv.setLayoutManager(layoutManager);
        searchNewAdapter=new SearchNewAdapter(getApplicationContext(),SearchActivity.this,SearchActivity.this);
        searchRv.setAdapter(searchNewAdapter);
        searchRv.addOnScrollListener(new PaginationScrollListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;
                Log.e("current_page_eve",currentPage+"");
                loadTopnews(query);
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
    }
    void toggleImageView(String query)
    {
        if(!query.isEmpty())
        {
            toggle_frame.setVisibility(View.VISIBLE);
        }
        else
        {
            toggle_frame.setVisibility(View.GONE);
        }
    }
    public void startVoiceRecognition() {
        Intent intent = new Intent("android.speech.action.RECOGNIZE_SPEECH");
        intent.putExtra("android.speech.extra.LANGUAGE_MODEL", "free_form");
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        intent.putExtra("android.speech.extra.PROMPT", "Speak Now");
        this.startActivityForResult(intent, VOICE_SEARCH_CODE);
    }

    void loadTopnews(String query)
    {
        Call<AllTopNewsResponse> call;
        call= ApiClient.getInstance().create(UrlRequest.class).searchNews(getString(R.string.api_key),query, "en");
        call.enqueue(new Callback<AllTopNewsResponse>() {
            @Override
            public void onResponse(Call<AllTopNewsResponse> call, Response<AllTopNewsResponse> response) {
                Log.e("response_status",response.code()+"");
                Log.e("response_body",response.body()+"");
                hideProgress();
                if(noResultLayout.getVisibility()== View.VISIBLE)
                {
                    noResultLayout.setVisibility(View.GONE);
                }

                if(response.isSuccessful())
                {
                    AllTopNewsResponse allTopNewsResponse=response.body();
                    Log.e("news_res",allTopNewsResponse.getStatus()+"");
                    if(allTopNewsResponse.getStatus().equalsIgnoreCase("ok"))
                    {
                        if(!allTopNewsResponse.getArticles().isEmpty())
                        {   searchRv.setVisibility(View.VISIBLE);
                            if (currentPage != PAGE_START)
                                try {
                                    searchNewAdapter.removeLoadingFooter();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            parseReviews(allTopNewsResponse);
                            if (currentPage != TOTAL_PAGES)
                                searchNewAdapter.addLoadingFooter();
                            else isLastPage = true;
                            isLoading = false;
                        }
                        else
                        {
                            if(searchNewAdapter!=null)
                            {
                                searchNewAdapter.clear();
                            }
                            noResultLayout.setVisibility(View.VISIBLE);
                        }

                    }
                }
                else
                {
                    if(searchNewAdapter!=null)
                    {
                        searchNewAdapter.clear();
                    }
                }
            }

            @Override
            public void onFailure(Call<AllTopNewsResponse> call, Throwable t) {
                Log.e("news_fragment",t.getMessage()+"");
                hideProgress();
                if(noResultLayout.getVisibility()== View.VISIBLE)
                {
                    noResultLayout.setVisibility(View.GONE);
                }
                if (currentPage == PAGE_START) {
                    Log.e("failure_search",t.getMessage()+"");
                }
                else
                {
                    searchNewAdapter.showRetry(true,Constants.fetchErrorMessage(t,getApplicationContext()));
                }
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

                    searchNewAdapter.addAll(articleList);

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
                searchNewAdapter.addAll(articleList);

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
    public void onItemClick(int pos) {

    }
}