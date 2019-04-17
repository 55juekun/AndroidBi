package com.aliyun.vodplayerview.activity;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.aliyun.vodplayerview.Util.MyCrashHandler;
import com.aliyun.vodplayerview.Util.User;
import com.aliyun.vodplayerview.updateUtils.OKHttpUpdateHttpService;
import com.xuexiang.xupdate.XUpdate;
import com.xuexiang.xupdate.entity.UpdateError;
import com.xuexiang.xupdate.listener.OnUpdateFailureListener;
import com.xuexiang.xupdate.utils.UpdateUtils;

import static com.xuexiang.xupdate.entity.UpdateError.ERROR.CHECK_NO_NEW_VERSION;

public class MyApp extends Application {
    /**@Description:
     * 该类会在整个app运行过程中一直存在。主要用于定义全局变量。
     * 定义user为全局变量，在其他地方也可以获取到
     * @author: 55juekun
     */
    private User user;
    private boolean showToast=false;

    public boolean isShowToast() {
        return showToast;
    }

    public void setShowToast(boolean showToast) {
        this.showToast = showToast;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initUpdate();
        //暂时不写入外部环境
        MyCrashHandler handler = new MyCrashHandler();
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }


    private void initUpdate() {
        XUpdate.get()
                .debug(true)
                .isWifiOnly(true)     //默认设置只在wifi下检查版本更新
                .isGet(true)          //默认设置使用get请求检查版本
                .isAutoMode(false)    //默认设置非自动模式，可根据具体使用配置
                .param("versionCode", UpdateUtils.getVersionCode(this)) //设置默认公共请求参数
                .param("appKey", getPackageName())
                .setOnUpdateFailureListener(new OnUpdateFailureListener() { //设置版本更新出错的监听
                    @Override
                    public void onFailure(UpdateError error) {
                        Log.d("55juekun", "onFailure: " + error);
                        if (showToast) {
                            if (error.getCode() == CHECK_NO_NEW_VERSION ) { //对不同错误进行处理
                                Toast.makeText(getApplicationContext(), "当前版本为最新版本", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                })
                .setIUpdateHttpService(new OKHttpUpdateHttpService()) //这个必须设置！实现网络请求功能。
                .init(this);   //这个必须初始化

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