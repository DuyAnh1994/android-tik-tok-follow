package com.solar.dev.tiktok.app.utils

import `in`.srain.cube.views.ptr.PtrClassicFrameLayout
import `in`.srain.cube.views.ptr.PtrFrameLayout
import `in`.srain.cube.views.ptr.PtrHandler
import android.view.View
import com.solar.dev.tiktok.app.model.app.DeliverCurrent

object DataUtils {

    var FLAG_OPEN_TIK_TOK_APP_BY_VIDEO = false
    var FLAG_OPEN_TIK_TOK_APP_BY_USER = false
    var deliverCurr = DeliverCurrent()


    fun initPullToRefreshViewPager(
        view: PtrClassicFrameLayout,
        checkCanDoRefresh: () -> Boolean,
        refresh: () -> Unit
    ) {
        view.disableWhenHorizontalMove(true)
        view.isKeepHeaderWhenRefresh = true
        view.setPtrHandler(object : PtrHandler {
            override fun checkCanDoRefresh(
                frame: PtrFrameLayout?,
                content: View?,
                header: View?
            ): Boolean {
                return checkCanDoRefresh()
            }

            override fun onRefreshBegin(frame: PtrFrameLayout?) {
                refresh()
            }
        })
    }
}