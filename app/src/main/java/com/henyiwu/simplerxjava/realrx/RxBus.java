package com.henyiwu.simplerxjava.realrx;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class RxBus {

    private final Subject<Object> mBus;

    private static class Holder {
        private static final RxBus BUS = new RxBus();
    }

    private RxBus() {
        // 如果不使用toSerialized,这几个方法默认不是线程安全,onSubscribe,onNext,onError,onComplete
        mBus = PublishSubject.create().toSerialized();
    }

    public static RxBus get() {
        return Holder.BUS;
    }

    public void post(Object event) {
        mBus.onNext(event);
    }

    public <T> Observable<T> toObservable(Class<T> tClass) {
        return mBus.ofType(tClass);
    }
}
