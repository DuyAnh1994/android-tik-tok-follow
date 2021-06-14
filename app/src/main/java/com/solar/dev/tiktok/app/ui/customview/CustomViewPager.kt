package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ViewPager(ctx, attrs) {

    var isPagingEnable = true

    override fun onTouchEvent(ev: MotionEvent?): Boolean {
        return this.isPagingEnable && super.onTouchEvent(ev)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return this.isPagingEnable && super.onInterceptTouchEvent(ev)
    }
}