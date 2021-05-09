package com.news.newsapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.news.newsapp.R;
import com.news.newsapp.utils.Constants;

public class NewsDetailsScrollingActivity extends AppCompatActivity  implements AppBarLayout.OnOffsetChangedListener{
   AppBarLayout appBarLayout;
   CollapsingToolbarLayout collapsingToolbarLayout;
   ImageView header;
   Toolbar toolbar;
    private Menu collapsedMenu;
    private boolean appBarExpanded = true;
    String news_title,news_datetime,news_source,news_desc,news_content,news_url;
    TextView news_title_tv,news_source_tv,news_datetime_tv,news_desc_tv,news_content_tv;
    MaterialButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details_scrolling);
        appBarLayout=findViewById(R.id.app_bar);
        collapsingToolbarLayout=findViewById(R.id.toolbar_layout);
        header=findViewById(R.id.header);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout.addOnOffsetChangedListener(this);
        news_title=getIntent().getStringExtra("news_title");
        news_source=getIntent().getStringExtra("news_source");
        news_datetime=getIntent().getStringExtra("news_datetime");
        news_desc=getIntent().getStringExtra("news_desc");
        news_content=getIntent().getStringExtra("news_content");
        news_url=getIntent().getStringExtra("news_url");
        initView();
    }
    void initView()
    {
        button=findViewById(R.id.read_btn);
        news_title_tv=findViewById(R.id.news_title);
        news_source_tv=findViewById(R.id.news_source);
        news_datetime_tv=findViewById(R.id.news_datetime);
        news_desc_tv=findViewById(R.id.news_desc);
        news_content_tv=findViewById(R.id.news_content);
        setData();

    }
    void setData()
    {
        news_title_tv.setText(news_title);
        news_source_tv.setText(news_source);
        news_datetime_tv.setText(news_datetime);
        news_desc_tv.setText(news_desc);
        news_content_tv.setText(Constants.truncateExtra(news_content));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(news_url));
                startActivity(intent);
            }
        });
    }
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if ((Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange())) {
//                    title.setTextColor(getApplicationContext().getResources().getColor(R.color.text_color));
            appBarExpanded = false;
//            swipeRefresh.setEnabled(false);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black_variant), PorterDuff.Mode.SRC_ATOP);

            invalidateOptionsMenu();
            //            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
//                      htabCollapseToolbar.setTitle("Testing");
//            toolbar.setTitle("Event Name");
//                    Log.e("appbarexpanded",appBarExpanded+"");
//            invalidateOptionsMenu();
        } else if ((Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange() / 2)) {
            appBarExpanded = false;
//            swipeRefresh.setEnabled(false);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.black_variant), PorterDuff.Mode.SRC_ATOP);

            invalidateOptionsMenu();
            //            eventTitle.setTextColor(Color.parseColor("#1c1c1c"));
            //            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.text_color), PorterDuff.Mode.SRC_ATOP);
//                     htabCollapseToolbar.setTitle("");
//            toolbar.setTitle("");
//                    Log.e("appbarexpanded",appBarExpanded+"");

//            invalidateOptionsMenu();
        } else if (verticalOffset == 0) {
            //expanded
            appBarExpanded = true;
//            swipeRefresh.setEnabled(true);
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            invalidateOptionsMenu();
//            toolbar.getNavigationIcon().setColorFilter(getResources().getCo.lor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//                     htabCollapseToolbar.setTitle("");
//            toolbar.setTitle("");
//                    Log.e("appbarexpanded",appBarExpanded+"");

//            invalidateOptionsMenu();
//                    title.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
        } else {
            appBarExpanded = true;
            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

            invalidateOptionsMenu();
            //            swipeRefresh.setEnabled(true);
//                    title.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
//            toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
//                    htabCollapseToolbar.setTitle("");
//            toolbar.setTitle("");
//                    Log.e("appbarexpanded",appBarExpanded+"");
//            invalidateOptionsMenu();

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_news_details_scrolling, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded)) {
            //collapsed
            for (int i = 0; i < menu.size(); i++) {
                Drawable drawable = menu.getItem(i).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.black_variant), PorterDuff.Mode.SRC_ATOP);
                }
            }

        } else {
            for (int i = 0; i < menu.size(); i++) {
                Drawable drawable = menu.getItem(i).getIcon();
                if (drawable != null) {
                    drawable.mutate();
                    drawable.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
                }
            }

            //expanded

        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_share) {
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


}