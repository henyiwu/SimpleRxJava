package com.henyiwu.simplerxjava

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.henyiwu.simplerxjava.core.TestEntry
import com.henyiwu.simplerxjava.realrx.RxLifecycle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        TestEntry.operatorMap()
//        TestEntry.operatorFlatMap()
        TestEntry.operatorObserveOn()
    }
}