package com.solar.dev.tiktok.app.ui.video.crop

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.view.View
import com.bumptech.glide.Glide
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityCropBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.ConfirmBar
import com.solar.dev.tiktok.app.ui.customview.cropvideo.player.VideoPlayer
import com.solar.dev.tiktok.app.ui.dialog.*
import com.solar.dev.tiktok.app.ui.video.common.EditVideoBaseActivity
import com.solar.dev.tiktok.app.ui.video.preview.EditVideoPreviewActivity
import com.solar.dev.tiktok.app.utils.Constant
import com.solar.dev.tiktok.app.utils.DummyDataUtils
import com.solar.dev.tiktok.app.utils.FileUtils
import com.solar.dev.tiktok.app.utils.animPrevious
import com.solar.dev.tiktok.app.utils.ffmpeg.FFMpegCallback
import com.solar.dev.tiktok.app.utils.ffmpeg.MyFFmpeg
import java.io.File

class EditVideoCropActivity : EditVideoBaseActivity<ActivityCropBinding>(),
    CropListener.DataBindingListener,
    ConfirmBar.AppBarEditVideoListener,
    CancelListener,
    SaveListener {

    private val adapter by lazy {
        CropAdapter(this, DummyDataUtils.ratioCrop, CropImpl())
    }
    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }
    private val playerView by lazy {
        VideoPlayer(this)
    }
    private val dialogSave by lazy {
        SaveDialog(this).apply {
            listener = this@EditVideoCropActivity
        }
    }
    private val dialogLoading by lazy {
        LoadingDialog(this)
    }
    private val dialogCancel by lazy {
        CancelDialog().apply {
            listener = this@EditVideoCropActivity
        }
    }
    private var isDiscard = true
    private var initialed = false
    private var cropDuration: Long = 0L


    override fun getLayoutId() = R.layout.activity_crop

    override fun setUp() {
        binding.bindingListener = this
//        dialogLoading.show(supportFragmentManager, "LoadingDialog")
        val videoTemp: Video? = intent.getParcelableExtra(Constant.INTENT_KEY_PUSH_EXTRA_VIDEO)
        videoTemp?.let {
            initPlayer(it.absolutePath)
        }

        binding.confirmBar.apply {
            listener = this@EditVideoCropActivity
            setTitle(getString(R.string.crop))
            buttonDone(R.drawable.bt_done_enable, true)
        }
        binding.rvRatio.adapter = adapter
    }

    override fun observerViewModel() {
//        observer(videoRepository.currentVideo) {
//            it?.let { v ->
//                initPlayer(v.absolutePath)
//            }
//        }
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

    override fun onClickDone(v: View) {
        super.onClickDone(v)
        turnOffPlayer {
            val output = File(FileUtils.getAudioTempPath("mp4").trim()).absolutePath
            val cropRect = binding.cropVideoView.cropRect
            MyFFmpeg.crop(
                File(videoRepository.currentVideo.value!!.absolutePath),
                output,
                cropRect.right,
                cropRect.bottom,
                cropRect.left,
                cropRect.top,
                cropDuration,
                object : FFMpegCallback {
                    override fun onStart() {
                        super.onStart()
                        // show dialog
                        dialogSave.show(supportFragmentManager, "SaveDialog")
                    }

                    override fun onSuccess(output: File) {
                        super.onSuccess(output)
                        playerView.release()
                        setResult(EditVideoPreviewActivity.RESULT_OK_CROP, Intent().putExtra(Constant.VIDEO_TEMP_CROP_PATH, output.absolutePath))
                        dialogSave.dismiss()
                        onBackPressed()
                    }

                    override fun onFailure(error: Exception?) {
                        super.onFailure(error)
                    }
                })
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
        if (playerView.isPlaying) {
            Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
            playerView.play(false)
        } else {
            Glide.with(this).load(R.drawable.bt_pause).into(binding.ibPlay)
            playerView.play(true)
        }
    }

    override fun onClickCancel(v: View) {
        dialogSave.dismiss()
        MyFFmpeg.cancel()
    }

    private fun turnOffPlayer(onSuccess: () -> Unit) {
        Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
        playerView.play(false)
        onSuccess.invoke()
    }

    private fun initPlayer(path: String) {
        if (!initialed) {
            binding.cropVideoView.setPlayer(playerView.player)
            playerView.apply {
                initMediaSource(this@EditVideoCropActivity, path)
                play(false)
            }
            fetchVideoInfo(path)
            initialed = true
        }
    }

    private fun fetchVideoInfo(path: String) {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(path)
        val videoWidth = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!)
        val videoHeight = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!)
        val rotationDegrees = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)!!)
        val duration = Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)!!)
        binding.cropVideoView.initBounds(videoWidth, videoHeight, rotationDegrees)
        cropDuration = duration.toLong()
    }

    inner class CropImpl : CropAdapter.CropListener {
        override fun onItemClick(v: View, position: Int) {
            when (position) {
                0 -> {
                    binding.cropVideoView.setFixedAspectRatio(false)
                }
                1 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(1, 1)
                }
                2 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(4, 5)
                }
                3 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(4, 3)
                }
                4 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(9, 16)
                }
                5 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(16, 9)
                }
                6 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(4, 5)
                }
                7 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(2, 3)
                }
                8 -> {
                    binding.cropVideoView.setFixedAspectRatio(true)
                    binding.cropVideoView.setAspectRatio(3, 2)
                }
            }
        }
    }
}

class CropListener {
    interface DataBindingListener {
        fun onClickPlay(v: View)
    }
}