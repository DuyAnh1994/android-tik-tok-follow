package com.solar.dev.tiktok.app.ui.main

import android.view.View
import com.dev.anhnd.mybaselibrary.model.IBaseView

class MainListener {

    interface DataBindingListener : IBaseView {

        fun onClickTikTok(v: View)

        fun onClickEditVideo(v: View)
    }

}