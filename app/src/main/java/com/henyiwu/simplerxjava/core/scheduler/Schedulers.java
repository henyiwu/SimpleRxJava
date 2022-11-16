package com.henyiwu.simplerxjava.core.scheduler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executors;

/**
 * 提供线程调度器
 */
public class Schedulers {

    private static Scheduler MAIN_THREAD = null;

    private static Scheduler NEW_THREAD = null;

    static {
        MAIN_THREAD = new HandlerScheduler(new Handler(Looper.getMainLooper()));
        // 演示使用，项目中需要自己定义线程池参数
        NEW_THREAD = new NewThreadScheduler(Executors.newScheduledThreadPool(2));
    }

    public static Scheduler mainThread() {
        return MAIN_THREAD;
    }

    public static Scheduler newThread() {
        return NEW_THREAD;
    }
}
