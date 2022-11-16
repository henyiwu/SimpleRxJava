package com.henyiwu.simplerxjava.core;

/**
 * 观察者
 */
public interface Observer<T> {

    // 与被观察者建立订阅关系时调用
    void onSubscribe();

    void onNext(T t);

    void onComplete();

    void onError(Throwable throwable);
}
