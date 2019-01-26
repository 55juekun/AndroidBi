package com.aliyun.vodplayerview.activity;

import android.app.Application;

import com.aliyun.vodplayerview.Util.MyCrashHandler;
import com.aliyun.vodplayerview.Util.User;

public class MyApp extends Application {
    /**@Description:
     * 该类会在整个app运行过程中一直存在。主要用于定义全局变量。
     * 定义user为全局变量，在其他地方也可以获取到
     * @author: 55juekun
     */
    private User user;

    @Override
    public void onCreate() {
        super.onCreate();
        //暂时不写入外部环境
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User userser) {
        this.user=userser;
    }

    public boolean isLogin() {
        return user != null;
    }
}