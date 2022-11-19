package com.henyiwu.simplerxjava;

import android.app.Application;
import android.util.Log;

import io.reactivex.functions.Consumer;
import io.reactivex.plugins.RxJavaPlugins;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                // 在这里全局捕获到rxjava抛出的异常
                Log.d("west", "BaseApplication, accept : " + throwable);
            }
        });
    }
}
