package com.solar.dev.tiktok.app.utils

import android.os.Bundle
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.fragment.app.Fragment

interface IScreenTransitionHelper {

    fun getScreenCount(): Int

    fun transitionTo(
        fragment: Fragment,
        @AnimatorRes @AnimRes enter: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes exist: Int = android.R.anim.fade_out,
        @AnimatorRes @AnimRes popEnter: Int = android.R.anim.fade_out,
        @AnimatorRes @AnimRes popExist: Int = android.R.anim.fade_in,
    )

    fun transitionTo(
        fragment: Fragment,
        bundle: Bundle,
        @AnimatorRes @AnimRes enter: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes exist: Int = android.R.anim.fade_out
    )

    fun backScreen()

    fun backToActivityRoot()
}