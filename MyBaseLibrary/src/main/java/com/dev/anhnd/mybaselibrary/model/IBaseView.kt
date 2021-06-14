package com.dev.anhnd.mybaselibrary.model

import android.view.View
import androidx.annotation.StringRes

interface IBaseView {

    fun onShowLoading()

    fun onHideLoading()

    fun onShowMessage(message: String)

    fun onShowMessage(@StringRes resId: Int)

    fun hideKeyboard()

    fun showKeyboard(view : View?)

    fun handleError(errorCode: Int?, throwable: Throwable?)
}