package com.solar.dev.tiktok.app.ui.video.speed

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.utils.app.getAppColor
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivitySpeedBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.ConfirmBar
import com.solar.dev.tiktok.app.ui.customview.VideoCutBar
import com.solar.dev.tiktok.app.ui.dialog.*
import com.solar.dev.tiktok.app.ui.video.common.EditVideoBaseActivity
import com.solar.dev.tiktok.app.ui.video.preview.EditVideoPreviewActivity
import com.solar.dev.tiktok.app.utils.*
import com.solar.dev.tiktok.app.utils.ffmpeg.FFMpegCallback
import com.solar.dev.tiktok.app.utils.ffmpeg.MyFFmpeg
import java.io.File

class EditVideoSpeedActivity : EditVideoBaseActivity<ActivitySpeedBinding>(),
    ConfirmBar.AppBarEditVideoListener,
    SpeedListener.DataBindingListener,
    CancelListener,
    SaveListener {

    /**
     * Trim ---------------------------------------------------------------------------------------
     */
    var speedStart: Long = 0L
    var speedEnd: Long = 0L
    var speedDuration: Long = 0L

    /**
     * set progress for Trim
     */
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            getMedia()?.let { mp ->
                if (mp.currentPosition > speedEnd) {
                    binding.apply {
                        vcbSpeed.setCenterProgress(speedStart)
                        seekTo(speedStart)
                    }
                } else {
                    binding.vcbSpeed.setCenterProgress(mp.currentPosition)
                }
                handler.postDelayed(this, 24)
            }
        }
    }

    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }
    private var speedCurrent = MutableLiveData(SPEED_X1)
    private val dialogSave by lazy {
        SaveDialog(this).apply {
            listener = this@EditVideoSpeedActivity
        }
    }
    private val dialogLoading by lazy {
        LoadingDialog(this)
    }
    private val dialogCancel by lazy {
        CancelDialog().apply {
            listener = this@EditVideoSpeedActivity
        }
    }
    private var isDiscard = false

    override fun getLayoutId() = R.layout.activity_speed

    override fun setUp() {
        binding.bindingListener = this
        binding.confirmBar.apply {
            listener = this@EditVideoSpeedActivity
            setTitle(getString(R.string.speed))
        }
        appPlayer.playerView = binding.playerView
        val videoTemp: Video? = intent.getParcelableExtra(Constant.INTENT_KEY_PUSH_EXTRA_VIDEO)
        videoTemp?.let {
            initPlayer(it)
        }

        appPlayer.listener = AppPlayerImpl()
        listener = PlayerImpl()

        binding.vcbSpeed.rangeChangeListener = SpeedRangeChangeImpl()
        binding.vcbSpeed.loadingListener = SpeedLoadingImpl()

        binding.apply {
            sbSpeed.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    val thumbX = sbSpeed.thumb
                    tvSpeed.apply {
                        text = if (p1 == 0) {
                            "0.5x"
                        } else {
                            "${p1}x"
                        }
                        x = thumbX.bounds.centerX().toFloat() + resources.getDimensionPixelSize(R.dimen._20sdp)
                        changeSpeed(p1)
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    tvSpeed.apply {
                        visibility = View.VISIBLE
                    }
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    tvSpeed.visibility = View.INVISIBLE
                }
            })
        }
    }

    override fun observerViewModel() {
//        observer(videoRepository.currentVideo) {
//            it?.let { video ->
//                initPlayer(video)
//            }
//        }
        observer(speedCurrent) {
            if (it == SPEED_X1) {
                binding.tvReset.apply {
                    isEnabled = false
                    setTextColor(getAppColor(R.color.colorHint))
                }
                binding.confirmBar.buttonDone(R.drawable.bt_done_disable, false)
                isDiscard = false
            } else {
                binding.tvReset.apply {
                    isEnabled = true
                    setTextColor(getAppColor(R.color.colorWhile_80))
                }
                binding.confirmBar.buttonDone(R.drawable.bt_done_enable, true)
                isDiscard = true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animPrevious()
    }

    override fun onClickClose(v: View) {
        super.onClickClose(v)
        if (isDiscard) {
            dialogCancel.show(supportFragmentManager, "CancelDialog")
        } else {
            turnOffPlayer {
                animPrevious()
                onBackPressed()
            }
        }
    }

    override fun onClickCancel(v: View) {
        dialogSave.dismiss()
        MyFFmpeg.cancel()
    }

    override fun onClickDiscard(v: View) {
        dialogCancel.dismiss()
        turnOffPlayer {
            animPrevious()
            onBackPressed()
        }
    }

    override fun onClickKeepEditing(v: View) {
        dialogCancel.dismiss()
    }

    override fun onClickDone(v: View) {
        super.onClickDone(v)
        turnOffPlayer {
            val output = File(FileUtils.getAudioTempPath("mp4").trim()).absolutePath
            MyFFmpeg.speed(
                File(videoRepository.currentVideo.value!!.absolutePath),
                output,
                speedCurrent.value!!,
                speedDuration,
                object : FFMpegCallback {
                    override fun onStart() {
                        super.onStart()
                        // show dialog
                        dialogSave.show(supportFragmentManager, "SaveDialog")
                    }

                    override fun onSuccess(output: File) {
                        super.onSuccess(output)
                        setResult(EditVideoPreviewActivity.RESULT_OK_SPEED, Intent().putExtra(Constant.VIDEO_TEMP_SPEED_PATH, output.absolutePath))
                        dialogSave.dismiss()
                        onBackPressed()
                    }

                    override fun onFailure(error: Exception?) {
                        super.onFailure(error)
                    }
                })
        }
    }

    override fun onClickPlay(v: View) {
        getMedia()?.let { mp ->
            if (mp.isPlaying) {
                Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
                pause()
                handler.removeCallbacks(runnable)
            } else {
                Glide.with(this).load(R.drawable.bt_pause).into(binding.ibPlay)
                play()
                handler.post(runnable)
            }
        }
    }

    override fun onClickReset(v: View) {
        binding.sbSpeed.progress = SPEED_LEVEL_1
        binding.tvReset.apply {
            setTextColor(getAppColor(R.color.colorHint))
            isEnabled = false
        }
    }

    private fun turnOffPlayer(onSuccess: () -> Unit) {
        Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
        stop()
        onSuccess.invoke()
    }

    private fun initPlayer(video: Video) {
        init(video.absolutePath)
        binding.tvDuration.text = CalculatorUtils.getDuration(video.duration)
        speedDuration = video.duration.toLong()
        speedEnd = speedDuration
        binding.vcbSpeed.setVideoPath(video.absolutePath, true)
    }

    private fun changeSpeed(value: Int) {
        when (value) {
            SPEED_LEVEL_SLOW -> {
                try {
                    speedCurrent.value = SPEED_SLOW
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SPEED_LEVEL_1 -> {
                try {
                    speedCurrent.value = SPEED_X1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SPEED_LEVEL_2 -> {
                try {
                    speedCurrent.value = SPEED_X2
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SPEED_LEVEL_3 -> {
                try {
                    speedCurrent.value = SPEED_X3
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            SPEED_LEVEL_4 -> {
                try {
                    speedCurrent.value = SPEED_X4
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        appPlayer.setSpeed(speedCurrent.value!!)
    }

    companion object {
        private val TAG = EditVideoSpeedActivity::class.java.simpleName

        private const val SPEED_LEVEL_SLOW = 0
        private const val SPEED_LEVEL_1 = 1
        private const val SPEED_LEVEL_2 = 2
        private const val SPEED_LEVEL_3 = 3
        private const val SPEED_LEVEL_4 = 4

        private const val SPEED_SLOW = 0.5F
        private const val SPEED_X1 = 1.0F
        private const val SPEED_X2 = 2.0F
        private const val SPEED_X3 = 3.0F
        private const val SPEED_X4 = 4.0F
    }

    inner class SpeedRangeChangeImpl : VideoCutBar.OnCutRangeChangeListener {
        override fun onRangeChanged(videoCutBar: VideoCutBar?, minValue: Long, maxValue: Long, thumbIndex: Int) {
            super.onRangeChanged(videoCutBar, minValue, maxValue, thumbIndex)
            speedStart = minValue
            speedEnd = maxValue
            speedDuration = speedEnd - speedStart
            seekTo(minValue)
        }
    }

    inner class PlayerImpl : PlayerListener {
        override fun onProgress(progress: Long) {
//            binding.vcbTrim.setCenterProgress(progress)
        }
    }

    inner class AppPlayerImpl : AppPlayer.IAppPlayerListener {
        override fun onLoadComplete() {
            super.onLoadComplete()
            pause()
        }
    }


    inner class SpeedLoadingImpl : VideoCutBar.ILoadingListener {
        override fun onLoadingStart() {
            super.onLoadingStart()
            if (!dialogLoading.isAdded) {
                dialogLoading.show(supportFragmentManager, "LoadingDialog")
            }
        }

        override fun onLoadingComplete() {
            super.onLoadingComplete()
            if (dialogLoading.isAdded) {
                dialogLoading.dismiss()
            }
        }

        override fun onLoadingError() {
            super.onLoadingError()
            if (dialogLoading.isAdded) {
                dialogLoading.dismiss()
            }
        }
    }
}


class SpeedListener {
    interface DataBindingListener {

        fun onClickPlay(v: View)

        fun onClickReset(v: View)
    }
}