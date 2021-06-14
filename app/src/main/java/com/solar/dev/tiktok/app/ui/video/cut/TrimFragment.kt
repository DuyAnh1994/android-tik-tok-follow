package com.solar.dev.tiktok.app.ui.video.cut

import android.os.Handler
import android.os.Looper
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentTrimBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.VideoCutBar

class TrimFragment : BaseFragment<FragmentTrimBinding, EditVideoCutActivity>() {

    companion object {
        private val TAG = TrimFragment::class.java.simpleName
    }

    /**
     * Trim ---------------------------------------------------------------------------------------
     */
    var trimStart: Long = 0L
    var trimEnd: Long = 0L
    var trimDuration: Long = 0L

    /**
     * set progress for Trim
     */
    private val handlerTrim = Handler(Looper.getMainLooper())
    private val runnableTrim = object : Runnable {
        override fun run() {
            activity.getMedia()?.let { mp ->
                if (mp.currentPosition > trimEnd) {
                    binding.apply {
                        vcbTrim.setCenterProgress(trimStart)
                        activity.seekTo(trimStart)
                    }
                } else {
                    binding.vcbTrim.setCenterProgress(mp.currentPosition)
                }
                handlerTrim.postDelayed(this, 24)
            }
        }
    }

    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }

    override fun getLayoutId() = R.layout.fragment_trim

    override fun setUp() {
        binding.vcbTrim.rangeChangeListener = TrimRangeChangeImpl()
        binding.vcbTrim.loadingListener = TrimLoadingImpl()
        val video = videoRepository.currentVideo.value!!
        initPlayer(video)
    }

    override fun observerViewModel() {
        /*observer(videoRepository.currentVideo) {
            it?.let { video ->
                initPlayer(video)
            }
        }*/
    }

    private fun initPlayer(video: Video) {
        binding.vcbTrim.setVideoPath(video.absolutePath, true)
    }

    fun post() {
        handlerTrim.post(runnableTrim)
    }

    fun removeCallback() {
        handlerTrim.removeCallbacks(runnableTrim)
    }

    inner class TrimRangeChangeImpl : VideoCutBar.OnCutRangeChangeListener {
        override fun onRangeChanged(videoCutBar: VideoCutBar?, minValue: Long, maxValue: Long, thumbIndex: Int) {
            super.onRangeChanged(videoCutBar, minValue, maxValue, thumbIndex)
            trimStart = minValue
            trimEnd = maxValue
            trimDuration = trimEnd - trimStart
            activity.setDuration(trimDuration)
            activity.seekTo(minValue)
            checkDoneTrim()
        }
    }

    fun checkDoneTrim() {
        if (trimStart != 0L || trimEnd != activity.rootDuration) {
            activity.getConfirmBar().buttonDone(R.drawable.bt_done_enable, true)
            activity.isDiscard = true
        } else {
            activity.getConfirmBar().buttonDone(R.drawable.bt_done_disable, false)
            activity.isDiscard = false
        }
    }

    inner class TrimLoadingImpl : VideoCutBar.ILoadingListener {
        override fun onLoadingStart() {
            super.onLoadingStart()
            activity.loading(true)
        }

        override fun onLoadingComplete() {
            super.onLoadingComplete()
            activity.loading(false)
            activity.cutOutFragment.onTriggerLoadCutOut()
        }

        override fun onLoadingError() {
            super.onLoadingError()
            activity.loading(false)
        }
    }
}