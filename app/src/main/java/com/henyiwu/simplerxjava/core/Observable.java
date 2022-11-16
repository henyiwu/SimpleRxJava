package com.henyiwu.simplerxjava.core;

import com.henyiwu.simplerxjava.core.scheduler.Scheduler;

/**
 * 被观察者核心抽象类
 * 也是框架的入口
 */
public abstract class Observable<T> implements ObservableSource<T> {

    @Override
    public void subscribe(Observer<T> observer) {
        subscribeActual(observer);
    }

    protected abstract void subscribeActual(Observer<T> observer);

    public static <T> Observable<T> create(ObservableOnSubscribe<T> source) {
        // ObservableCreate内含有事件发射器
        return new ObservableCreate<>(source);
    }

    /**
     * 将Observable增强为ObservableMap
     */
    public <R> ObservableMap<T, R> map(Function<T, R> function) {
        return new ObservableMap<>(this, function);
    }

    /**
     * 将Observable增强为ObservableFlatMap
     */
    public <R> ObservableFlatMap<T, R> flatMap(Function<T, ObservableSource<R>> function) {
        return new ObservableFlatMap<>(this, function);
    }

    /**
     * 将Observable增强为ObservableSubscribeOn
     */
    public ObservableSubscribeOn<T> subscribeOn(Scheduler scheduler) {
        return new ObservableSubscribeOn<>(this, scheduler);
    }

    /**
     * 将Observable增强为ObservableObserverOn
     */
    public ObservableObserverOn<T> observerOn(Scheduler scheduler) {
        return new ObservableObserverOn<>(this, scheduler);
    }
}
