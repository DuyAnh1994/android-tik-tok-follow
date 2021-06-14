package com.solar.dev.tiktok.app.ui.splash

import android.content.Intent
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.utils.app.putPrefData
import com.solar.dev.tiktok.app.BuildConfig
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivitySplashBinding
import com.solar.dev.tiktok.app.ui.main.MainActivity
import com.solar.dev.tiktok.app.utils.AppPlayer
import com.solar.dev.tiktok.app.utils.Constant
import com.solar.dev.tiktok.app.utils.animNext
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*

class SplashActivity : BaseActivity<ActivitySplashBinding>() {


    private val splashPlayer by lazy {
        AppPlayer().apply {
            bindToLifecycle(this@SplashActivity)
        }
    }

    override fun getLayoutId() = R.layout.activity_splash

    override fun setUp() {
        pvSplash
        splashPlayer.playerView = binding.pvSplash
        splashPlayer.init(R.raw.splash)
        splashPlayer.play()
        CoroutineScope(Dispatchers.Main).launch {
            /**
             * set star to test
             */
//            putPrefData(Constant.KEY_STARS, 100000)
            delay(1000)
            splashPlayer.apply {
                stop()
                release()
            }
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                animNext()
                finish()
            }
        }
    }

    override fun observerViewModel() {

    }
}