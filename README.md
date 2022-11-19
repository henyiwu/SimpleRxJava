## SimpleRxJava

### Q&A

- Q：为什么subscribeOn只有最上游的一次调用生效？

  A：subscribeOn的原理是将上游事件订阅动作，包装成Runnable，通过线程池放入指定线程执行，且订阅顺序是从下游往上游订阅，所以不管有多少个subscribeOn，只有最上游的subscribeOn会最终生效。

- Q：为什么observerOn只会影响下游的观察者处理事件？

  A：同理subscribeOn，多次observerOn只有最后一次调用能将事件观察转入指定线程执行。