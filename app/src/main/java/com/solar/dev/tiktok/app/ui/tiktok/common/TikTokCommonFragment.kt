package com.solar.dev.tiktok.app.ui.tiktok.common

import androidx.databinding.ViewDataBinding
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment

abstract class TikTokCommonFragment<DB : ViewDataBinding, A : BaseActivity<*>> : BaseFragment<DB, A>() {


    override fun setUp() {

    }

}