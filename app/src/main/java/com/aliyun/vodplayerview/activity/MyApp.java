package com.aliyun.vodplayerview.activity;

import android.app.Application;

import com.aliyun.vodplayerview.Util.MyCrashHandler;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //暂时不写入外部环境
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }
}