package com.example.myapplication;

import android.app.Application;

import com.king.naiveutils.BaseUtils;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BaseUtils.init(this);
    }

}
