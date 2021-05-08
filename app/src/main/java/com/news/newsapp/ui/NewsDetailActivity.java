package com.news.newsapp.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.news.newsapp.R;

public class NewsDetailActivity extends AppCompatActivity {
    WebView webView;
    String url;
    Toolbar toolbar;
    RelativeLayout loader;
    LottieAnimationView animationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        webView=findViewById(R.id.webView);
        loader=findViewById(R.id.loader);
        animationView=findViewById(R.id.animationView);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        url=getIntent().getStringExtra("web_url");
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(url);

    }
    public class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            animationView.pauseAnimation();
            loader.setVisibility(View.GONE);
        }
    }
}