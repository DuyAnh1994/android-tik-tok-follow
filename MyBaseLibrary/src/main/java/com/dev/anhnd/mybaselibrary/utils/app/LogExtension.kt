package com.dev.anhnd.mybaselibrary.utils.app

import android.util.Log

fun Any.logD(message: Any) {
    Log.d(this::class.java.simpleName, "= [$message]")
}

fun Any.logE(message: Any) {
    Log.e(this::class.java.simpleName, "= [$message]")
}