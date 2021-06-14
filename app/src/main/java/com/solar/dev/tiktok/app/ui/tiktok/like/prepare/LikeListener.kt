package com.solar.dev.tiktok.app.ui.tiktok.like.prepare

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class LikeListener {

    interface DataBindingListener : IBaseView {

        fun onClickConfirm(v: View) {}

        fun onClickSeeMore(v: View) {}
    }
}