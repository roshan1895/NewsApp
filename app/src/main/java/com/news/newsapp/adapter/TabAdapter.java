package com.news.newsapp.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStatePagerAdapter {
    List<Fragment> fragmentList=new ArrayList<>();
    List<String> stringList=new ArrayList<>();
    public TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }
    public void addFragment( Fragment fragment,String title) {
        fragmentList.add(fragment);
        stringList.add(title);
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
//        NewsFragment fragment = new NewsFragment();
//        Bundle args = new Bundle();
//        args.putString("title",stringList.get(position));
//        fragment.setArguments(args);
//        return fragment;
        return fragmentList.get(position);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return stringList.get(position);
    }

    @Override
    public int getCount() {
        return stringList.size();
    }
}
