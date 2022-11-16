package com.henyiwu.simplerxjava.core;

import com.henyiwu.simplerxjava.core.scheduler.Scheduler;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 下游事件进入指定线程执行
 */
public class ObservableObserverOn<T> extends AbstractObservableWithUpStream<T, T> {

    final Scheduler scheduler;

    public ObservableObserverOn(ObservableSource<T> source, Scheduler scheduler) {
        super(source);
        this.scheduler = scheduler;
    }

    @Override
    protected void subscribeActual(Observer<T> observer) {
        Scheduler.Worker worker = scheduler.createWorker();
        // 观察到上游事件变化后，在ObserveOnObserver中监听，把事件分发到指定线程执行
        source.subscribe(new ObserveOnObserver<>(observer, worker));
    }

    static final class ObserveOnObserver<T> implements Observer<T>, Runnable {

        final Observer<T> downStream;
        final Scheduler.Worker worker;
        final Deque<T> queue;
        volatile boolean done;
        // 抛异常后，队列中有事件也不处理
        volatile Throwable throwable;
        volatile boolean over;

        ObserveOnObserver(Observer<T> downStream, Scheduler.Worker worker) {
            this.downStream = downStream;
            this.worker = worker;
            queue = new ArrayDeque<>();
        }

        @Override
        public void onSubscribe() {
            downStream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            // 把事件加入队列，offer不抛异常，只会返回false
            queue.offer(t);
            // 把事件从队列取出，分发到指定线程执行
            schedule();
        }

        private void schedule() {
            worker.scheduler(this);
        }

        @Override
        public void onComplete() {
            downStream.onComplete();
        }

        @Override
        public void onError(Throwable throwable) {
            this.throwable = throwable;
            downStream.onError(throwable);
        }

        @Override
        public void run() {
            drainNormal();
        }

        /**
         * 从队列中取出事件并处理
         */
        private void drainNormal() {
            final Deque<T> q = queue;
            final Observer<T> observer = downStream;
            while (true) {
                boolean d = done;
                T t = queue.poll(); // 取出并删除元素，不会抛异常，没有数据时返回null
                boolean empty = t == null;
                if (checkTerminated(d, empty, observer)) {
                    return;
                }
                if (empty) {
                    break;
                }
                observer.onNext(t);
            }
        }

        private boolean checkTerminated(boolean done, boolean empty, Observer<T> observer) {
            if (over) {
                queue.clear();
                return true;
            }
            if (done) {
                Throwable error = throwable;
                if (error != null) {
                    over = true;
                    observer.onError(error);
                } else if (empty) {
                    over = true;
                    observer.onComplete();
                    return true;
                }
            }
            return false;
        }
    }
}
