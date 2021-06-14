package com.solar.dev.tiktok.app.ui.video.cut

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentCutOutBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.VideoCutBar

class CutOutFragment : BaseFragment<FragmentCutOutBinding, EditVideoCutActivity>() {

    companion object {
        private val TAG = CutOutFragment::class.java.simpleName
    }

    /**
     * Cut out ------------------------------------------------------------------------------------
     */
    var cutOutEndFirst: Long = 0L
    var cutOutStartSecond: Long = 0L
    var cutOutDuration: Long = 0L

    /**
     * set progress for Cutout
     */
    private val handlerCutout = Handler(Looper.getMainLooper())
    private val runnableCutOut = object : Runnable {
        override fun run() {
            activity.getMedia()?.let { mp ->
                Log.d(TAG, "CutOut: ${mp.currentPosition}")
                if (mp.currentPosition > activity.rootDuration) {
                    activity.seekTo(0)
                }
                if (mp.currentPosition >= cutOutEndFirst - 100 && mp.currentPosition <= cutOutEndFirst + 100) {
                    activity.seekTo(cutOutStartSecond)
                }
                binding.vcbCutOut.setCenterProgress(mp.currentPosition)
                handlerCutout.postDelayed(this, 24)
            }
        }
    }

    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }

    override fun getLayoutId() = R.layout.fragment_cut_out

    override fun setUp() {
        binding.vcbCutOut.rangeChangeListener = CutOutRangeChangeImpl()
    }

    override fun observerViewModel() {
        /*observer(videoRepository.currentVideo) {
            it?.let { video ->
                initPlayer(video)
            }
        }*/
    }

    fun onTriggerLoadCutOut() {
        val video = videoRepository.currentVideo.value!!
        initPlayer(video)
    }

    private fun initPlayer(video: Video) {
        binding.vcbCutOut.setVideoPath(video.absolutePath, false)
        cutOutEndFirst = 0L
        cutOutStartSecond = video.duration.toLong()
    }

    fun post() {
        handlerCutout.post(runnableCutOut)
    }

    fun removeCallback() {
        handlerCutout.removeCallbacks(runnableCutOut)
    }

    inner class CutOutRangeChangeImpl : VideoCutBar.OnCutRangeChangeListener {
        override fun onRangeChanged(videoCutBar: VideoCutBar?, minValue: Long, maxValue: Long, thumbIndex: Int) {
            super.onRangeChanged(videoCutBar, minValue, maxValue, thumbIndex)
            cutOutEndFirst = minValue
            cutOutStartSecond = maxValue
            cutOutDuration = cutOutEndFirst + (activity.rootDuration - cutOutStartSecond)
            activity.setDuration(cutOutDuration)
            activity.seekTo(0)
            checkDoneCutOut()
        }
    }

    fun checkDoneCutOut() {
        if (cutOutEndFirst != 0L || cutOutStartSecond != activity.rootDuration) {
            handlerCutout.post(runnableCutOut)
            activity.getConfirmBar().buttonDone(R.drawable.bt_done_enable, true)
            activity.isDiscard = true
        } else {
            handlerCutout.removeCallbacks(runnableCutOut)
            activity.getConfirmBar().buttonDone(R.drawable.bt_done_disable, false)
            activity.isDiscard = false
        }
    }
}