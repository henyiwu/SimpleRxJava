package com.henyiwu.simplerxjava.realrx;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * 通用组件，结合compose，lifecycleOwner，实现事件监听与声明周期绑定，当activity（或其他）生命周期走到onDestroy时，
 * 调用compositeDisposable.dispose()取消订阅事件，防止内存泄漏
 *
 * 使用:
 * Observable.create(new ObservableOnSubscribe<Object>() {
 *      public void subscribe(ObservableEmitter<Object> emitter) {
 *          emitter.onNext("aaa");
 *      }
 * })
 * .compose(RxLifecycle.bindLifecycle(this))
 * @param <T>
 */
public class RxLifecycle<T> implements LifecycleObserver, ObservableTransformer<T, T> {

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        Log.d("RxLifecycle", "onDestroy: 取消rx订阅链");
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    /**
     * 当有一条订阅链调用.compose(RxLifecycle.bindLifecycle(this))时，就添加一次compositeDisposable.add(disposable)
     * 当这个页面生命周期onDestroy时，该页面所有订阅链取消调用
     */
    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.doOnSubscribe(new Consumer<Disposable>() {
            @Override
            public void accept(Disposable disposable) throws Exception {
                compositeDisposable.add(disposable);
            }
        });
    }

    public static <T> RxLifecycle<T> bindLifecycle(LifecycleOwner lifecycleOwner) {
        RxLifecycle<T> lifecycle = new RxLifecycle<>();
        lifecycleOwner.getLifecycle().addObserver(lifecycle);
        return lifecycle;
    }
}
