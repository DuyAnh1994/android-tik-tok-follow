package com.solar.dev.tiktok.app.ui.tiktok.getstar.common

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class GetStarListener {

    interface DataBindingListener : IBaseView {

    }

}

class GetStarLikeListener {
    interface DataBindingListener : IBaseView {

        fun onClickLikeNow(v: View)

        fun onClickSkip(v: View)
    }
}

class GetStarFollowListener {
    interface DataBindingListener : IBaseView {

        fun onClickFollowNow(v: View)

        fun onClickSkip(v: View)
    }
}