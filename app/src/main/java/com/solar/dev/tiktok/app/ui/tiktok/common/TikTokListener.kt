package com.solar.dev.tiktok.app.ui.tiktok.common

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class TikTokListener {

    interface DataBindingListener : IBaseView {

        fun onClickTikTok(v: View)

        fun onClickEditVideo(v: View)
    }

}