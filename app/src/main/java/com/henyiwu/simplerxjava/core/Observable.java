package com.henyiwu.simplerxjava.core;

import com.henyiwu.simplerxjava.core.scheduler.Scheduler;

/**
 * 被观察者核心抽象类
 * 也是框架的入口
 * <p>
 * 链式构建流：
 *
 * 事件源
 * ObservableOnSubscribe source = new ObservableOnSubscribe() {...}
 *
 * 订阅上一个事件
 * Observable.create(source) -> observable0 = new ObservableCreate(source)
 *
 * 订阅上一个事件
 * observable0.map() -> observable1 = new ObservableMap(observable0, ...)
 *
 * 订阅上一个事件
 * observable1.subscribeOn -> observable2 = new ObservableSubscribeOn(observable1,...)
 *
 * 订阅上一个事件
 * observable2.map() -> observable3 = new ObservableMap(observable2, ...)
 *
 * 订阅上一个事件
 * observable3.observeOn() -> observable4 = new ObservableObserveOn(observable3, ...)
 *
 * 订阅上一个事件
 * observable4.flatMap() -> observable5 = new ObservableFlatMap(observable4, ...)
 *
 * 每个被观察者都持有了上游被观察者的引用
 * 当订阅操作执行时，都会调用上游的被观察者来执行订阅操作，所以，从订阅过程来看，是最后一个被观察者依次向上游
 * 调用被观察者的subscribe()方法
 * </>
 *
 * <p>
 * 订阅流
 * Observer observer0 = new Observer() {...}
 * observable5.subscribe(observer0) -> observable5.subscribeActual(observer0)
 *
 * Observer observer1 = new MergeObserver(observer0) {...}
 * observable4.subscribe(observer1) -> observable4.subscribeActual(observer1)
 *
 * Observer observer2 = new ObserverOnObserver(observer1) {...}
 * observable3.subscribe(observer2) -> observable3.subscribeActual(observer2)
 *
 * Observer observer3 = new MapObserver(observer2) {...}
 * observable2.subscribe(observer3) -> observable2.subscribeActual(observer3)
 *
 * Observer observer4 = new SubscribeOnObserver(observer3) {...}
 * observable1.subscribe(observer4) -> observable1.subscribeActual(observer4)
 *
 * 发射源ObservableCreate
 * CreateEmitter<T> emitter = new CreateEmitter<T>(observer);
 * observable0.subscribe(emitter) -> 将emitter暴露给开发处理
 *
 * 每个被观察者订阅上游观察者时，都创建新的观察者，观察上游事件变化
 * <p/>
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
