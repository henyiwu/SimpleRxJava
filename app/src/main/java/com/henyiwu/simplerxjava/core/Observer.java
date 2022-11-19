package com.henyiwu.simplerxjava.core;

/**
 * 观察者
 */
public interface Observer<T> {

    // 与被观察者建立订阅关系时调用
    void onSubscribe();

    // 上游事件变化时，回调当前观察者onNext()
    void onNext(T t);

    void onComplete();

    void onError(Throwable throwable);
}
