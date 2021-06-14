package com.solar.dev.tiktok.app.ui.main

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityMainBinding
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.ui.tiktok.common.TikTokActivity
import com.solar.dev.tiktok.app.ui.video.common.EditVideoActivity
import com.solar.dev.tiktok.app.utils.animNext
import kotlinx.coroutines.*

class MainActivity : BaseActivity<ActivityMainBinding>(), MainListener.DataBindingListener {

    companion object {
        const val WAIT_TIME = 2000L
    }

    private var mIsConfirmBackPressed = false

    private lateinit var permission: Array<String>
    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }

    override fun getLayoutId() = R.layout.activity_main

    override fun fixSingleTask() = false

    override fun setUp() {
        binding.bindingListener = this
        permission = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
        )
        doPermission(permission)
    }

    override fun observerViewModel() {
        observer(videoRepository.saveSuccessfully) {
            it?.let {
                if (it) {
                    binding.relativeSave.visibility = View.VISIBLE
                    GlobalScope.launch {
                        delay(1000)
                        withContext(Dispatchers.Main) {
                            binding.relativeSave.visibility = View.INVISIBLE
                        }
                    }
                } else {
                    binding.relativeSave.visibility = View.INVISIBLE
                    videoRepository.saveSuccessfully.value = false
                }
            }
        }
    }

    override fun onClickTikTok(v: View) {
        if (checkPermission(permission)) {
            startActivity(Intent(this, TikTokActivity::class.java))
            animNext()
        } else {
            doPermission(permission)
        }
    }

    override fun onClickEditVideo(v: View) {
        if (checkPermission(permission)) {
            startActivity(Intent(this, EditVideoActivity::class.java))
            animNext()
        } else {
            doPermission(permission)
        }
    }

    override fun onBackPressed() {
        if (mIsConfirmBackPressed) {
            super.onBackPressed()
            return
        }
        mIsConfirmBackPressed = true
        Toast.makeText(this, getString(R.string.msg_back_again), Toast.LENGTH_SHORT).show()
        Handler(Looper.getMainLooper()).postDelayed({ mIsConfirmBackPressed = false }, WAIT_TIME)

        // clear instance
        TikTokRepository.clear()
        EditVideoRepository.clear()
        CrawlDataRepository.clear()
    }

    private fun doPermission(permission: Array<String>) {
        doRequestPermission(permission, {}, {})
    }

    override fun onResume() {
        super.onResume()
//        doPermission(permission)
    }
}