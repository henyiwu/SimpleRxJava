package com.henyiwu.simplerxjava.core;

public class ObservableMap<T, U> extends AbstractObservableWithUpStream<T, U> {

    Function<T, U> function;

    public ObservableMap(ObservableSource<T> source, Function<T, U> function) {
        super(source);
        this.function = function;
    }

    @Override
    protected void subscribeActual(Observer<U> observer) {
        source.subscribe(new MapObserver<>(observer, function));
    }

    static class MapObserver<T, U> implements Observer<T> {

        final Observer<U> downStream;

        final Function<T, U> mapper;

        public MapObserver(Observer<U> downStream, Function<T, U> mapper) {
            this.downStream = downStream;
            this.mapper = mapper;
        }

        @Override
        public void onSubscribe() {
            downStream.onSubscribe();
        }

        @Override
        public void onNext(T t) {
            // map操作符的核心
            U u = mapper.apply(t);
            downStream.onNext(u);
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
