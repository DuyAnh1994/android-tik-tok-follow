package com.solar.dev.tiktok.app.ui.video.common

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class EditVideoListener {

    interface DataBindingListener : IBaseView {

        fun onClickTikTok(v: View)

        fun onClickEditVideo(v: View)
    }

}