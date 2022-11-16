package com.henyiwu.simplerxjava.core;

/**
 * 抽象装饰类
 * rxjava的操作符基于装饰器模式，即新的操作符继承于该类，并对Observable进行增强，获取上游被观察者进行监听，
 * 创建自己的观察者，暴露接口给开发者处理，持有上游观察者将事件继续往下游分发
 */
public abstract class AbstractObservableWithUpStream<T, U> extends Observable<U> {

    // 上游被观察者，在此基础上进行功能扩展
    protected final ObservableSource<T> source;

    public AbstractObservableWithUpStream(ObservableSource<T> source) {
        this.source = source;
    }
}
