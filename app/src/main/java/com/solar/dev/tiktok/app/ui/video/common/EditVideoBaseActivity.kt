package com.solar.dev.tiktok.app.ui.video.common

import androidx.databinding.ViewDataBinding
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.google.android.exoplayer2.SimpleExoPlayer
import com.solar.dev.tiktok.app.utils.AppPlayer

abstract class EditVideoBaseActivity<DB : ViewDataBinding> : BaseActivity<DB>() {

    var listener: PlayerListener? = null
    val appPlayer by lazy {
        AppPlayer().apply {
            bindToLifecycle(this@EditVideoBaseActivity)
        }
    }

    open fun getContainer(): Int {
        return -1
    }

    fun init(path: String) {
        appPlayer.init(path)
    }

    fun play() {
        appPlayer.play()
    }

    fun pause() {
//        audioHelper.stopRequestAudio()
        appPlayer.pause()
    }

    fun stop() {
//        audioHelper.stopRequestAudio()
        appPlayer.stop()
    }

    fun release() {
        appPlayer.release()
    }

    fun reset() {
        appPlayer.seek(0, true)
    }

    fun seekTo(current: Long) {
        appPlayer.seek(current, appPlayer.isPlaying())
    }

    fun duration(): Long? {
        return appPlayer.duration
    }

    fun setSpeed(value: Float) {
        appPlayer.setSpeed(value)
    }

    fun getMedia(): SimpleExoPlayer? {
        return appPlayer.media
    }

    interface PlayerListener {
        fun onProgress(progress: Long)
    }
}