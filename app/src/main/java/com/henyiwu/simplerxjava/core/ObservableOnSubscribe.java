package com.henyiwu.simplerxjava.core;

/**
 * 让被观察者和事件发射器建立关系
 */
public interface ObservableOnSubscribe<T> {

    void subscribe(Emitter<T> emitter);

}
