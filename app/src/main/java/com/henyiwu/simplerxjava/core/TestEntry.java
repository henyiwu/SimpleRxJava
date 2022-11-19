package com.henyiwu.simplerxjava.core;

import android.util.Log;

import com.henyiwu.simplerxjava.core.scheduler.Scheduler;
import com.henyiwu.simplerxjava.core.scheduler.Schedulers;

public class TestEntry {

    public static void operatorFlatMap() {
        // 通过ObservableOnSubscribe关联起来，ObservableOnSubscribe中会创建emitter
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(Emitter<Object> emitter) {
                emitter.onNext("111");
                emitter.onNext("222");
                emitter.onNext("333");
//                emitter.onError(new Throwable());
                emitter.onComplete();
            }
        }).flatMap(new Function<Object, ObservableSource<Object>>() {
            // 1. Observable.flatMap(Function)，返回一个ObservableFlatMap
            // 2. 返回一个ObservableFlatMap中注册了上游ObservableCreate的监听事件，将观察者替换成自己的MergeObserver
            // 3. MergeObserver观察到事件变化后，调用apply暴露给使用者，获得一个新的ObservableSource，并监听它的事件
            // 4. 新ObservableSource事件发生变化时（emitter.onNext("after Flat Map : " + o)），
            //    在ObservableFlatMap中监听，并传递给下游
            @Override
            public ObservableSource<Object> apply(Object o) {
                // 创建一个新的被观察者，重新发射
                return Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(Emitter<Object> emitter) {
                        // 返回新的被观察者，可以将上游事件进行自定义
                        emitter.onNext("事件第一次，after Flat Map: " + o);
                        emitter.onNext("事件第二次，after Flat Map : " + o);
                        emitter.onNext("事件第三次，after Flat Map : " + o);
                    }
                });
            }
        }).subscribe(new Observer<Object>() {
            @Override
            public void onSubscribe() {
                Log.d("west", "onSubscribe");
            }

            @Override
            public void onNext(Object o) {
                Log.d("west", "onNext " + o);
            }

            @Override
            public void onComplete() {
                Log.d("west", "onComplete");
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("west", "onError " + throwable);
            }
        });
    }

    public static void operatorMap() {
        // 通过ObservableOnSubscribe关联起来，ObservableOnSubscribe中会创建emitter，做为事件发生的源头
        // ObservableCreate持有ObservableOnSubscribe的实例，创建emitter并调用ObservableOnSubscribe.subscribe()
        Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(Emitter<Object> emitter) {
                        // subscribeOn决定这段代码执行的线程
                        Log.d("west", "subscribe currentThread " + Thread.currentThread());
                        emitter.onNext("111");
                        emitter.onNext("222");
                        emitter.onNext("333");
//                emitter.onError(new Throwable());
                        emitter.onComplete();
                    }
                }).map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        return "after map : " + o;
                    }
                })
                // 指定上游的线程后，如果不显式指定下游线程，下游也会在该线程中执行
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Observer<Object>() {
                    // 1. Observable.map(Function) 返回一个ObservableMap
                    // 2. ObservableMap中注册了上游ObservableCreate的监听事件，将观察者换成自己的MapObserver
                    // 3. MapObserver观察到事件变化后，调用apply暴露给使用者，让使用者自己对事件进行处理
                    // 4. 处理完成后，将事件继续传递给下游（ObservableMap持有下游的实例）
                    @Override
                    public void onSubscribe() {
                        Log.d("west", "onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d("west", "onNext, currentThread" + Thread.currentThread());
                        Log.d("west", "onNext " + o);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("west", "onComplete");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d("west", "onError " + throwable);
                    }
                });
    }

    /**
     * 下游订阅上游事件，下游订阅上游事件，下游订阅上游事件...套娃
     * 源头发出事件时，上游监听、处理，再发给下游...套娃
     */
    public static void operatorObserveOn() {
        Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(Emitter<Object> emitter) {
                        Log.d("west", "subscribe currentThread " + Thread.currentThread());
                        emitter.onNext("111");
                        emitter.onNext("222");
                        emitter.onNext("333");
                        emitter.onComplete();
                    }
                })
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        return "after map : " + o;
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observerOn(Schedulers.mainThread())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        // currentThread is MainThread
                        // observerOn使下游事件进入主线程执行
                        Log.d("west", "apply1 currentThread : " + Thread.currentThread());
                        return o;
                    }
                })
                .observerOn(Schedulers.newThread())
                .map(new Function<Object, Object>() {
                    @Override
                    public Object apply(Object o) {
                        // 上面指定进入新线程，这段代码在新线程执行
                        Log.d("west", "apply2 currentThread : " + Thread.currentThread());
                        return o;
                    }
                })
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe() {
                        Log.d("west", "onSubscribe");
                    }

                    @Override
                    public void onNext(Object o) {
                        Log.d("west", "onNext, currentThread" + Thread.currentThread());
                        Log.d("west", "onNext " + o);
                    }

                    @Override
                    public void onComplete() {
                        Log.d("west", "onComplete");
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.d("west", "onError " + throwable);
                    }
                });
    }
}
