package com.henyiwu.simplerxjava.realrx;

import android.util.Log;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.AsyncSubject;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

public class RxJavaSubject {

    private static final String TAG = "RxJavaSubject";

    /**
     * AsyncSubject特点:无论发射多少条数据，无论在订阅前发射还是在订阅后发射，都只会收到最后一条发射的数据
     * 结果:打印c
     * 如果先onComplete再发射c，打印b
     */
    public static void testAsyncSubject() {
        AsyncSubject<Object> asyncSubject = AsyncSubject.create();
        asyncSubject.onNext("a");
        asyncSubject.onNext("b");
        Disposable subscribe = asyncSubject.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "object: " + o);
            }
        });
        asyncSubject.onNext("c");
        // 触发事件
        asyncSubject.onComplete();
    }

    /**
     * BehaviorSubject特点:只会接收到订阅前最后一条发射的数据以及订阅之后所有的数据
     * 结果:
     * RxJavaSubject: object: b
     * RxJavaSubject: object: c
     * RxJavaSubject: object: d
     */
    public static void testBehaviorSubject() {
        BehaviorSubject<Object> behaviorSubject = BehaviorSubject.create();
        behaviorSubject.onNext("a");
        behaviorSubject.onNext("b");
        Disposable subscribe = behaviorSubject.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "object: " + o);
            }
        });
        behaviorSubject.onNext("c");
        behaviorSubject.onNext("d");
        // 触发事件
        behaviorSubject.onComplete();
    }

    /**
     * 会接收到全部的数据，无论订阅前后
     * RxJavaSubject: object: a
     * RxJavaSubject: object: b
     * RxJavaSubject: object: c
     * RxJavaSubject: object: d
     */
    public static void testReplySubject() {
        ReplaySubject<Object> replySubject = ReplaySubject.create();
        replySubject.onNext("a");
        replySubject.onNext("b");
        Disposable subscribe = replySubject.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "object: " + o);
            }
        });
        replySubject.onNext("c");
        replySubject.onNext("d");
        // 触发事件
        replySubject.onComplete();
    }

    /**
     * 只会接收到订阅之后的所有数据
     * RxJavaSubject: object: c
     * RxJavaSubject: object: d
     */
    public static void testPublishSubject() {
        PublishSubject<Object> publishSubject = PublishSubject.create();
        publishSubject.onNext("a");
        publishSubject.onNext("b");
        Disposable subscribe = publishSubject.subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                Log.d(TAG, "object: " + o);
            }
        });
        publishSubject.onNext("c");
        publishSubject.onNext("d");
        // 触发事件
        publishSubject.onComplete();
    }
}
