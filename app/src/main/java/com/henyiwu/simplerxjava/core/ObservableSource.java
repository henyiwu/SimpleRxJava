package com.henyiwu.simplerxjava.core;

/**
 * 被观察者的顶层接口
 */
public interface ObservableSource<T> {

    /**
     * 对应通用观察者模式的addObserver()
     */
    void subscribe(Observer<T> observer);

}
