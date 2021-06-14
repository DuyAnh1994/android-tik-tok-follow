package com.dev.anhnd.mybaselibrary.application

import android.app.Application
import com.dev.anhnd.mybaselibrary.utils.app.initBaseApplication
import com.dev.anhnd.mybaselibrary.utils.app.initPrefData
import org.jetbrains.annotations.NotNull

abstract class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        initBaseApplication()
        initPrefData(preferenceName())
        setUp()
    }

    abstract fun setUp()

    @NotNull
    abstract fun preferenceName(): String
}