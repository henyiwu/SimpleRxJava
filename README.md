## SimpleRxJava

### Q&A

- Q：为什么subscribeOn只有最上游的一次调用生效？

  A：subscribeOn的原理是将上游事件订阅动作，包装成Runnable，通过线程池放入指定线程执行，且订阅顺序是从下游往上游订阅，所以不管有多少个subscribeOn，只有最上游的subscribeOn会最终生效。

- Q：为什么observerOn只会影响下游的观察者处理事件？

  A：同理subscribeOn，多次observerOn只有最后一次调用能将事件观察转入指定线程执行。

### RxJava Subject

- 同时代表一个观察者和被观察者，允许将事件从单个源多播到多个子观察者。

1. AsyncSubject：无论发射多少条数据，无论在订阅前发射还是在订阅后发射，都只会收到最后一条发射的数据
2. BehaviorSubject：只会接收到订阅前最后一条发射的数据以及订阅之后所有的数据
3. ReplySubject：会接收到全部的数据，无论订阅前后
4. PublishSubject：只会接收到订阅之后的所有数据