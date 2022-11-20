package com.henyiwu.simplerxjava;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.henyiwu.simplerxjava.core.Emitter;
import com.henyiwu.simplerxjava.core.Function;
import com.henyiwu.simplerxjava.realrx.RxBus;
import com.henyiwu.simplerxjava.realrx.RxLifecycle;
import com.henyiwu.simplerxjava.realrx.SchedulerTransformer;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LeakActivity extends AppCompatActivity {

    Disposable disposable;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    Object object = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak2);
        rxJavaLeakTest();
        testRxBus();
    }

    private void testRxBus() {
        RxBus.get().post("发射来自LeakActivity的事件");
    }

    private void rxJavaLeakTest() {
        Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) {
                        try {
                            Thread.sleep(5000);
                            emitter.onNext("aaa");
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .compose(new SchedulerTransformer<>())
                .compose(RxLifecycle.bindLifecycle(this))
//                .subscribe(new Consumer<Object>() {
//                    @Override
//                    public void accept(Object o) throws Exception {
//                        // 5s内退出，依然会执行这段代码，activity已经泄露
//                        TextView textView = (TextView) findViewById(R.id.tv);
//                        textView.setText(o.toString());
//                        Log.d("west", "onNext " + o);
//                    }
//                });
                .subscribe(new Observer<Object>() {

                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        // 5s内退出，依然会执行这段代码，activity已经泄露
                        TextView textView = (TextView) findViewById(R.id.tv);
                        textView.setText(o.toString());
                        Log.d("west", "onNext " + o);
                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
//        compositeDisposable.add(disposable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 如果事件没执行完成，会抛异常，在BaseApplication已全局处理
        compositeDisposable.dispose();
    }
}