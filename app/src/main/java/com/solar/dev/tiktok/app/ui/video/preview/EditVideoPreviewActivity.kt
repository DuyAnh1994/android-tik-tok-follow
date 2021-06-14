package com.solar.dev.tiktok.app.ui.video.preview

import android.content.Intent
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.utils.app.logD
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityPreviewBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.customview.AppBarPreview
import com.solar.dev.tiktok.app.ui.customview.VideoCutBar
import com.solar.dev.tiktok.app.ui.dialog.CancelDialog
import com.solar.dev.tiktok.app.ui.dialog.CancelListener
import com.solar.dev.tiktok.app.ui.dialog.SaveDialog
import com.solar.dev.tiktok.app.ui.main.MainActivity
import com.solar.dev.tiktok.app.ui.video.common.EditVideoBaseActivity
import com.solar.dev.tiktok.app.ui.video.crop.EditVideoCropActivity
import com.solar.dev.tiktok.app.ui.video.cut.EditVideoCutActivity
import com.solar.dev.tiktok.app.ui.video.speed.EditVideoSpeedActivity
import com.solar.dev.tiktok.app.utils.*
import java.io.File

class EditVideoPreviewActivity : EditVideoBaseActivity<ActivityPreviewBinding>(),
    AppBarPreview.AppBarEditVideoListener,
    EditVideoPreviewListener.DataBindingListener,
    CancelListener {


    companion object {
        private val TAG = EditVideoPreviewActivity::class.java.simpleName
        private const val REQUEST_CODE_TRIM = 101
        private const val REQUEST_CODE_SPEED = 102
        private const val REQUEST_CODE_CROP = 103
        const val RESULT_OK_TRIM = 201
        const val RESULT_OK_SPEED = 202
        const val RESULT_OK_CROP = 203
    }

    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }

    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable {
        override fun run() {
            getMedia()?.let { mp ->
                if (mp.currentPosition > mp.duration) {
                    listener?.onProgress(0)
                } else {
                    logD(mp.currentPosition)
                    listener?.onProgress(mp.currentPosition)
                }
                handler.postDelayed(this, 1)
            }
        }
    }

    private var initialed = false
    private val dialogLoading by lazy {
        SaveDialog(this)
    }
    private val dialogCancel by lazy {
        CancelDialog().apply {
            listener = this@EditVideoPreviewActivity
        }
    }
    private var isDiscard = false


    override fun getLayoutId() = R.layout.activity_preview

    override fun setUp() {
        binding.bindingListener = this
        binding.appBar.apply {
            listener = this@EditVideoPreviewActivity
        }
        binding.videoCutBar.apply {
            controllerListener = ControllerImpl()
        }
        appPlayer.listener = AppPlayerImpl()
        listener = PlayerImpl()
        appPlayer.playerView = binding.playerView
        val video = videoRepository.currentVideo.value!!
        initPlayer(video)
    }

    override fun observerViewModel() {
//        observer(videoRepository.currentVideo) {
//            it?.let { video ->
//                initPlayer(video)
//            }
//        }
    }

    private fun initPlayer(video: Video) {
        if (!initialed) {
            init(video.absolutePath)
            binding.videoCutBar.setVideoPath(video.absolutePath)
            binding.tvDuration.text = CalculatorUtils.getDuration(video.duration)
            handler.post(runnable)
            initialed = false
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        binding.videoCutBar.clearHistoryBitmap(true)
        val parentTemp = "${Environment.getExternalStorageDirectory()}/${Constant.FOLDER_TEMP_EDIT}"
        FileUtils.deleteAllFileTemp(File(parentTemp))
        animPrevious()
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        if (isDiscard) {
            dialogCancel.show(supportFragmentManager, "CancelDialog")
        } else {
            turnOffPlayer {
                onBackPressed()
            }
        }
    }

    override fun onClickDiscard(v: View) {
        // Xoá video vừa edit trong thư mục temp
        dialogCancel.dismiss()
        turnOffPlayer {
            onBackPressed()
        }
    }

    override fun onClickKeepEditing(v: View) {
        dialogCancel.dismiss()
    }

    override fun onClickSave(v: View) {
        super.onClickSave(v)
        turnOffPlayer {
            if (!dialogLoading.isAdded) {
                dialogLoading.show(supportFragmentManager, "SaveDialog")
            }
            val src = File(videoRepository.currentVideo.value!!.absolutePath)
            val newPath = "${Environment.getExternalStorageDirectory()}/${Constant.FOLDER_INTERNAL_AUDIO_EDIT}/${src.name}"
            val dst = File(newPath)
            Log.d(TAG, "src: ${src.absolutePath} to dst: ${dst.absolutePath}")

            FileUtils.moveFileTempToOutput(src, dst) {
                val old = dst.absolutePath
                val new = FileUtils.getAudioOutputPath("mp4")
                Log.d(TAG, "old: $old, new: $new")
                val parentTemp = "${Environment.getExternalStorageDirectory()}/${Constant.FOLDER_TEMP_EDIT}"
                FileUtils.deleteAllFileTemp(File(parentTemp))
                FileUtils.rename(this, old, new) {
                    if (dialogLoading.isAdded) {
                        dialogLoading.dismiss()
                    }
                    videoRepository.saveSuccessfully.value = true
                    startActivity(Intent(this, MainActivity::class.java).apply {
                        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    })
                }
            }
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

    override fun onClickTrim(v: View) {
        turnOffPlayer {
            starEditVideo(EditVideoCutActivity::class.java, videoRepository.currentVideo.value!!, REQUEST_CODE_TRIM)
        }
    }

    override fun onClickSpeed(v: View) {
        turnOffPlayer {
            starEditVideo(EditVideoSpeedActivity::class.java, videoRepository.currentVideo.value!!, REQUEST_CODE_SPEED)
        }
    }

    override fun onClickCrop(v: View) {
        turnOffPlayer {
            starEditVideo(EditVideoCropActivity::class.java, videoRepository.currentVideo.value!!, REQUEST_CODE_CROP)
        }
    }

    private fun starEditVideo(activityEdit: Class<*>, video: Video, requestCode: Int) {
        startActivityForResult(Intent(this@EditVideoPreviewActivity, activityEdit).putExtra(Constant.INTENT_KEY_PUSH_EXTRA_VIDEO, video), requestCode)
        animNext()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_TRIM -> {
                if (resultCode == RESULT_OK_TRIM) {
                    data?.getStringExtra(Constant.VIDEO_TEMP_TRIM_PATH)?.let {
                        previewVideoResult(it)
                    }
                }
            }
            REQUEST_CODE_SPEED -> {
                if (resultCode == RESULT_OK_SPEED) {
                    data?.getStringExtra(Constant.VIDEO_TEMP_SPEED_PATH)?.let {
                        previewVideoResult(it)
                    }
                }
            }
            REQUEST_CODE_CROP -> {
                if (resultCode == RESULT_OK_CROP) {
                    data?.getStringExtra(Constant.VIDEO_TEMP_CROP_PATH)?.let {
                        previewVideoResult(it)
                    }
                }
            }
        }
    }

    private fun previewVideoResult(path: String) {
        try {
//            mTempPathVideo = path
            val uri = File(path).toUri()
            Log.d(TAG, "uri: $uri")
            // set video
            init(path)
            appPlayer.playerView = binding.playerView
            // set thumbnail
            binding.videoCutBar.setVideoPath(path)
            // set duration
            binding.tvDuration.text = CalculatorUtils.getDuration(this, uri.toString())
            // set current video
            val video = Video(uri, File(path).name, CalculatorUtils.getDurationLong(this, path), 0, "Temp edit", path)
            videoRepository.addVideoToEdit(video)
            // set resource button save ready
            binding.appBar.setButtonSave(true)
            isDiscard = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun turnOffPlayer(onSuccess: () -> Unit) {
        Glide.with(this).load(R.drawable.bt_play).into(binding.ibPlay)
        pause()
        handler.removeCallbacks(runnable)
        /*
        * phải kiêm tra xem STATE_READY ko, nếu ko thì tuyệt đối ko được gọi release or blockSendingMessage
        * */
//        release()
        onSuccess.invoke()
    }

    inner class PlayerImpl : PlayerListener {
        override fun onProgress(progress: Long) {
            binding.videoCutBar.setCenterProgress(progress)
            binding.tvSeekBar.text = CalculatorUtils.getDuration(progress.toInt())
        }
    }

    inner class ControllerImpl : VideoCutBar.ControllerListener {
        override fun onProcessToPixel(pixel: Float) {
            binding.tvSeekBar.x = pixel
        }

        override fun onTouchDown(touchProgress: Float, touchInBar: Boolean) {

        }

        override fun onTouchTouchRelease(touchProgress: Float, touchInBar: Boolean) {
            if (touchInBar) {
                if (touchProgress > 0 && touchProgress < getMedia()?.duration!!.toFloat()) {
                    binding.videoCutBar.setCenterProgress(touchProgress)
                    seekTo(touchProgress.toLong())
                }
            }
        }
    }

    inner class AppPlayerImpl : AppPlayer.IAppPlayerListener {
        override fun onLoadComplete() {
            super.onLoadComplete()
            pause()
        }
    }
}

class EditVideoPreviewListener {

    interface DataBindingListener : IBaseView {

        fun onClickPlay(v: View)

        fun onClickTrim(v: View)

        fun onClickSpeed(v: View)

        fun onClickCrop(v: View)
    }
}