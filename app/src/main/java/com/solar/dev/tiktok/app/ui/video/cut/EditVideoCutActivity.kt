package com.solar.dev.tiktok.app.ui.video.cut

import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityCutBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.ConfirmBar
import com.solar.dev.tiktok.app.ui.dialog.*
import com.solar.dev.tiktok.app.ui.video.common.EditVideoBaseActivity
import com.solar.dev.tiktok.app.ui.video.preview.EditVideoPreviewActivity
import com.solar.dev.tiktok.app.utils.*
import com.solar.dev.tiktok.app.utils.ffmpeg.FFMpegCallback
import com.solar.dev.tiktok.app.utils.ffmpeg.MyFFmpeg
import java.io.File

class EditVideoCutActivity : EditVideoBaseActivity<ActivityCutBinding>(),
    CutListener.DataBindingListener,
    ConfirmBar.AppBarEditVideoListener,
    CancelListener,
    SaveListener {

    companion object {
        private val TAG = EditVideoCutActivity::class.java.simpleName
    }

    /**
     * Common -------------------------------------------------------------------------------------
     */
    var rootDuration: Long = 0L
    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }
    val trimFragment = TrimFragment()
    val cutOutFragment = CutOutFragment()
    var state = State.TRIM
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }
    private val dialogSave by lazy {
        SaveDialog(this).apply {
            listener = this@EditVideoCutActivity
        }
    }
    private val dialogCancel by lazy {
        CancelDialog().apply {
            listener = this@EditVideoCutActivity
        }
    }
    var isDiscard = false

    override fun getLayoutId() = R.layout.activity_cut

    override fun setUp() {
        binding.bindingListener = this
        binding.confirmBar.apply {
            listener = this@EditVideoCutActivity
            setTitle(getString(R.string.trim_cut_out))
        }
        appPlayer.playerView = binding.playerView
        val video = videoRepository.currentVideo.value!!
        initPlayer(video)
        appPlayer.listener = AppPlayerImpl()
        listener = PlayerImpl()

        val adapter = ViewPagerCutAdapter(this, supportFragmentManager, 1).apply {
            addFragment(trimFragment, getString(R.string.trim), 0)
            addFragment(cutOutFragment, getString(R.string.cut_out), 1)
        }

        binding.viewPager.apply {
            isPagingEnable = false
            offscreenPageLimit = adapter.count
            setAdapter(adapter)
        }

        binding.tabLayout.setupWithViewPager(binding.viewPager)

        for (i in 0..1) {
            binding.tabLayout.getTabAt(i)?.customView = adapter.getTabView(i)
        }

        binding.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            @RequiresApi(Build.VERSION_CODES.M)
            override fun onPageSelected(position: Int) {
                turnOffPlayer {
                    when (position) {
                        0 -> {
                            setTabView(0)
                            state = State.TRIM
                            seekTo(trimFragment.trimStart)
                        }
                        1 -> {
                            setTabView(1)
                            state = State.CUT_OUT
                            seekTo(0)
                        }
                    }
                    checkStateDone()
                }
            }
        })
    }

    override fun observerViewModel() {
        /*observer(videoRepository.currentVideo) {
            it?.let { video ->
                initPlayer(video)
            }
        }*/
    }

    private fun checkStateDone() {
        if (state == State.TRIM) {
            trimFragment.checkDoneTrim()
        } else if (state == State.CUT_OUT) {
            cutOutFragment.checkDoneCutOut()
        }
    }

    private fun getTabView(index: Int): View? {
        return binding.tabLayout.getTabAt(index)?.customView
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setTabView(index: Int) {
        when (index) {
            0 -> {
                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_red)
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_black)

                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorWhile_100))
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorHint))
            }
            1 -> {
                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_black)
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_point_selected_red)

                getTabView(0)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorHint))
                getTabView(1)?.findViewById<TextView>(R.id.tvTitle)?.setTextColor(getColor(R.color.colorWhile_100))
            }
        }
    }

    fun loading(loading: Boolean) {
        if (loading) {
            if (!loadingDialog.isAdded) {
                loadingDialog.show(supportFragmentManager, "LoadingDialog")
            }
        } else {
            if (loadingDialog.isAdded) {
                loadingDialog.dismiss()
            }
        }
    }

    fun setDuration(value: Long) {
        binding.tvDuration.text = CalculatorUtils.getDuration(value.toInt())
    }

    private fun initPlayer(video: Video) {
        init(video.absolutePath)
        binding.tvDuration.text = CalculatorUtils.getDuration(video.duration)
        rootDuration = video.duration.toLong()
        trimFragment.apply {
            trimStart = (rootDuration * 0.0).toLong()
            trimEnd = (rootDuration * 1.0).toLong()
//            post()
            seekTo(trimStart)
        }
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

    override fun onClickPlay(v: View) {
        getMedia()?.let { mp ->
            if (mp.isPlaying) {
                Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
                pause()
            } else {
                if (state == State.TRIM) {
                    cutOutFragment.removeCallback()
                    trimFragment.post()
                } else if (state == State.CUT_OUT) {
                    trimFragment.removeCallback()
                    cutOutFragment.post()
                }
                Glide.with(this).load(R.drawable.bt_pause).into(binding.ibPlay)
                play()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animPrevious()
    }

    override fun onClickCancel(v: View) {
        dialogSave.dismiss()
        MyFFmpeg.cancel()
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

    override fun onClickDone(v: View) {
        super.onClickDone(v)
        turnOffPlayer {
            val output = File(FileUtils.getAudioTempPath("mp4").trim()).absolutePath
            if (state == State.TRIM) {
                val start = CalculatorUtils.getDuration(trimFragment.trimStart)
                val end = CalculatorUtils.getDuration(trimFragment.trimEnd)
                MyFFmpeg.cut(
                    File(videoRepository.currentVideo.value!!.absolutePath),
                    output,
                    start,
                    end,
                    trimFragment.trimDuration,
                    object : FFMpegCallback {
                        override fun onStart() {
                            super.onStart()
                            dialogSave.show(supportFragmentManager, "SaveDialog")
                        }

                        override fun onSuccess(output: File) {
                            super.onSuccess(output)
                            setResult(EditVideoPreviewActivity.RESULT_OK_TRIM, Intent().putExtra(Constant.VIDEO_TEMP_TRIM_PATH, output.absolutePath))
                            dialogSave.dismiss()
                            onBackPressed()
                        }

                        override fun onFailure(error: Exception?) {
                            super.onFailure(error)
                        }
                    })
            } else if (state == State.CUT_OUT) {
                val endFirst = cutOutFragment.cutOutEndFirst.toInt()
                val startSecond = cutOutFragment.cutOutStartSecond.toInt()
                val duration = rootDuration

                MyFFmpeg.cutOut(
                    File(videoRepository.currentVideo.value!!.absolutePath),
                    output,
                    endFirst,
                    startSecond,
                    duration,
                    object : FFMpegCallback {
                        override fun onStart() {
                            super.onStart()
                            dialogSave.show(supportFragmentManager, "LoadingSaveDialog")
                        }

                        override fun onSuccess(output: File) {
                            super.onSuccess(output)
                            setResult(EditVideoPreviewActivity.RESULT_OK_TRIM, Intent().putExtra(Constant.VIDEO_TEMP_TRIM_PATH, output.absolutePath))
                            dialogSave.dismiss()
                            onBackPressed()
                        }

                        override fun onFailure(error: Exception?) {
                            super.onFailure(error)
                        }
                    })
            }
        }
    }

    private fun turnOffPlayer(onSuccess: () -> Unit) {
        Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
        stop()
        onSuccess.invoke()
    }

    fun getConfirmBar() = binding.confirmBar

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
}

enum class State {
    TRIM, CUT_OUT
}

class CutListener {
    interface DataBindingListener {

        fun onClickPlay(v: View)
    }
}
