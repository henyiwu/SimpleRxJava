package com.henyiwu.simplerxjava.core;

import com.henyiwu.simplerxjava.core.scheduler.Scheduler;

/**
 * 将上游事件放入指定线程执行，同时下游事件也进入该线程执行
 */
public class ObservableSubscribeOn<T> extends AbstractObservableWithUpStream<T, T> {

    final Scheduler scheduler;

    public ObservableSubscribeOn(ObservableSource<T> source, Scheduler scheduler) {
        super(source);
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        observer.onSubscribe();
        // 返回指定线程对应的Worker类
        Scheduler.Worker worker = scheduler.createWorker();
        // 转入subscribeOn指定的线程执行
        worker.scheduler(new SchedulerTask(new SubscribeOnObserver<>(observer)));
    }

    static class SubscribeOnObserver<T> implements Observer<T> {
        final Observer<T> downstream;

        public SubscribeOnObserver(Observer<T> downstream) {
            this.downstream = downstream;
        }

        @Override
        public void onSubscribe() {
            downstream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            downstream.onNext(t);
        }

        @Override
        public void onComplete() {
            downstream.onComplete();
        }

        @Override
        public void onError(Throwable throwable) {
            downstream.onError(throwable);
        }
    }

    /**
     * 被放入subscribeOn指定的线程执行
     */
    final class SchedulerTask implements Runnable {
        final SubscribeOnObserver<T> parent;

        SchedulerTask(SubscribeOnObserver<T> parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            // 在指定的线程监听，后续的流程也在指定线程执行
            source.subscribe(parent);
        }
    }
}
