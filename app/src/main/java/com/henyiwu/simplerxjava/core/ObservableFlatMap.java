package com.henyiwu.simplerxjava.core;

public class ObservableFlatMap<T, U> extends AbstractObservableWithUpStream<T, U> {

    Function<T, ObservableSource<U>> function;

    public ObservableFlatMap(ObservableSource<T> source, Function<T, ObservableSource<U>> function) {
        super(source);
        this.function = function;
    }

    @Override
    protected void subscribeActual(Observer<U> observer) {
        source.subscribe(new MergeObserver<>(observer, function));
    }

    static class MergeObserver<T, U> implements Observer<T> {

        final Observer<U> downStream;

        final Function<T, ObservableSource<U>> mapper;

        /**
         * @param downStream 把下游的观察者传入，执行自己的onNext后，再执行下游观察者的onNext
         */
        MergeObserver(Observer<U> downStream, Function<T, ObservableSource<U>> mapper) {
            this.downStream = downStream;
            this.mapper = mapper;
        }

        @Override
        public void onSubscribe() {
            downStream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            ObservableSource<U> observable = mapper.apply(t);
            observable.subscribe(new Observer<U>() {
                @Override
                public void onSubscribe() {

                }

                @Override
                public void onNext(U u) {
                    downStream.onNext(u);
                }

                @Override
                public void onComplete() {

                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        }

        @Override
        public void onComplete() {
            downStream.onComplete();
        }

        @Override
        public void onError(Throwable throwable) {
            downStream.onError(throwable);
        }
    }
}
