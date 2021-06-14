package com.solar.dev.tiktok.app.ui.tiktok.follow.prepare

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class FollowListener {

    interface DataBindingListener : IBaseView {

        fun onClickConfirm(v: View) {}

        fun onClickSeeMore(v: View) {}
    }
}