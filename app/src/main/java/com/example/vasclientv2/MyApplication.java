package com.example.vasclientv2;

import android.app.Application;

public class MyApplication extends Application {
    private static MyApplication mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = (MyApplication) getApplicationContext();
    }

    public static MyApplication getContext() {
        return mContext;
    }
}
