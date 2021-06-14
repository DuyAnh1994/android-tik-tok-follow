package com.solar.dev.tiktok.app

import com.dev.anhnd.mybaselibrary.application.BaseApplication

class MyApp : BaseApplication() {


    override fun setUp() {
    }

    override fun preferenceName() = getString(R.string.app_name)
}