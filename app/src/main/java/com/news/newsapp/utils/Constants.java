package com.news.newsapp.utils;

import android.content.Context;

import com.news.newsapp.R;

import java.util.concurrent.TimeoutException;

public class Constants {
    public static String COUNTRY="IN";
    public  static  String BASE_URL="https://newsapi.org/";
    public static  String LANGUAGE="EN";



    public static String fetchErrorMessage(Throwable throwable, Context context) {
        String errorMsg = context.getResources().getString(R.string.error_msg_unknown);
        if(Connection_Check.checkConnection(context))
        {
            errorMsg = context.getResources().getString(R.string.error_msg_no_internet);

        }
        else if(throwable instanceof TimeoutException)
        {
            errorMsg = context.getResources().getString(R.string.error_msg_timeout);

        }
        return errorMsg;
    }

}
