package com.henyiwu.simplerxjava

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.henyiwu.simplerxjava.realrx.RxBus
import com.henyiwu.simplerxjava.realrx.RxLifecycle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        openSecondActivity()
//        TestEntry.operatorMap()
//        TestEntry.operatorFlatMap()
//        TestEntry.operatorObserveOn()
//        RxJavaSubject.testAsyncSubject();
//        RxJavaSubject.testBehaviorSubject();
//        RxJavaSubject.testPublishSubject();
        testRxBus()
    }

    private fun openSecondActivity() {
        findViewById<TextView>(R.id.tvHello).setOnClickListener {
            val intent = Intent(this, LeakActivity::class.java)
            startActivity(intent)
        }
    }

    private fun testRxBus() {
        val subscribe = RxBus.get()
            .toObservable(String::class.java)
            .compose(RxLifecycle.bindLifecycle(this))
            .subscribe {
                Log.d("west", "接收到事件:$it")
            }
    }
}