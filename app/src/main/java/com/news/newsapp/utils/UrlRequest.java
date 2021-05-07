package com.news.newsapp.utils;

import com.news.newsapp.models.top_news.AllTopNewsResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UrlRequest {
    @GET("v2/top-headlines")
    Call<AllTopNewsResponse>topNewsByCat(@Query("apiKey") String key, @Query("category")String category, @Query("language")String language);
    @GET("v2/top-everything")
    Call<AllTopNewsResponse>searchNews(@Query("apiKey") String key, @Query("qInTitle")String title, @Query("language")String language);

}
