package com.solar.dev.tiktok.app.ui.video.common

import android.content.ContentUris
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ActivityEditVideoBinding
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.customview.AppBarEditVideo
import com.solar.dev.tiktok.app.ui.customview.VideoCutBar
import com.solar.dev.tiktok.app.ui.dialog.LoadingDialog
import com.solar.dev.tiktok.app.ui.video.gallery.EditVideoGalleryFragment
import com.solar.dev.tiktok.app.ui.video.preview.EditVideoPreviewActivity
import com.solar.dev.tiktok.app.utils.Constant
import com.solar.dev.tiktok.app.utils.InjectorUtils
import com.solar.dev.tiktok.app.utils.animNext
import com.solar.dev.tiktok.app.utils.animPrevious
import java.io.File
import java.io.IOException

class EditVideoActivity : EditVideoBaseActivity<ActivityEditVideoBinding>(),
    ItemListener.EditVideoListener, AppBarEditVideo.AppBarEditVideoListener {

    companion object {
        private val TAG = EditVideoActivity::class.java.simpleName
        const val REQUEST_VIDEO_CAPTURE = 101
        const val BUCKET_ID_ALL_VIDEO = 102
        const val VIDEO_ERROR = "Video error!"
        var FOLDER_EDIT_PATH: String? = null
        var FOLDER_TEMP_EDIT_PATH: String? = null
    }

    private lateinit var permission: Array<String>
    private val loadingDialog by lazy {
        LoadingDialog(this)
    }

    private val videoVM: EditVideoViewModel by viewModels {
        InjectorUtils.providerEditVideoViewModelFactory()
    }
    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }
    private val adapter by lazy {
        BaseAdapter<Video>(R.layout.item_video).apply {
            listener = this@EditVideoActivity
        }
    }
    private var videosTemp = mutableListOf<Video>()
    private var videos = mutableListOf<Video>()

    override fun getLayoutId() = R.layout.activity_edit_video

    override fun getContainer() = R.id.frameContainer

    override fun fixSingleTask() = false

    override fun setUp() {
        binding.bindingListener = this
        binding.appBar.apply {
            listener = this@EditVideoActivity
        }
        binding.rvVideo.adapter = adapter
        videoVM.fetchVideos(null, null, {}, {}, {})
        createFolderInternal()
        permission = arrayOf(
            android.Manifest.permission.CAMERA
        )
    }

    override fun observerViewModel() {
        observer(videoVM.videos) {
            it?.let { video ->
                if (video.size > 0) {
                    binding.tvMessage.visibility = View.INVISIBLE
                    binding.tvContent.apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.msg_edit_video_choose_item)
                    }
                    videos = video
                    adapter.data = videos
                } else {
                    binding.tvContent.visibility = View.INVISIBLE
                    binding.tvMessage.apply {
                        visibility = View.VISIBLE
                        text = getString(R.string.msg_edit_video_no_video)
                    }
                }
            }
        }
        observer(videoRepository.galleryId) {
            it?.let { id ->
                findByGalleryId(id)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
            val videoUri: Uri? = data?.data
            videoUri?.let { uri ->
                try {
                    val video = fetchVideoInfo(uri)
                    scanFile(video.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun createFolderInternal() {
        val internalPackage = Environment.getExternalStorageDirectory()
        val folderName = Constant.FOLDER_INTERNAL_AUDIO_EDIT
        val edit = File(internalPackage, folderName)
        if (!edit.exists()) {
            edit.mkdirs()
        }
        FOLDER_EDIT_PATH = edit.absolutePath

        val folderTemp = Constant.FOLDER_TEMP_EDIT
        val temp = File(internalPackage, folderTemp)
        if (!temp.exists()) {
            temp.mkdirs()
        }
        FOLDER_TEMP_EDIT_PATH = temp.absolutePath
    }

    private fun scanFile(savePath: String) {
        MediaScannerConnection.scanFile(this, arrayOf(savePath), null) { path, uri ->
            videos.add(0, fetchVideoInfo(uri))
            runOnUiThread {
                adapter.data = videos
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onClickItem(v: View, item: Video) {
        if (VideoCutBar.checkValidVideo(item.absolutePath)) {
            videoVM.addVideoToEdit(item)
            startActivity(Intent(this, EditVideoPreviewActivity::class.java))
            animNext()
        } else {
            Toast.makeText(this@EditVideoActivity, "Can't play this video", Toast.LENGTH_SHORT).show()
            return
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        animPrevious()
    }

    override fun onClickBack(v: View) {
        super.onClickBack(v)
        onBackPressed()
    }

    override fun onClickGallery(v: View) {
        super.onClickGallery(v)
        if (videos.size <= 0) {
            Toast.makeText(this, getString(R.string.msg_edit_video_no_video), Toast.LENGTH_SHORT).show()
            return
        }
        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(R.anim.enter_fade_in,
                R.anim.exit_fade_out,
                R.anim.enter_fade_in,
                R.anim.exit_fade_out
            )
            add(getContainer(), EditVideoGalleryFragment(), "EditVideoGalleryFragment")
            addToBackStack("EditVideoGalleryFragment")
            commit()
        }
    }

    override fun onClickCamera(v: View) {
        super.onClickCamera(v)
//        if (checkPermission(permission)) {
        try {
            Intent(MediaStore.ACTION_VIDEO_CAPTURE).also { takeVideoIntent ->
                takeVideoIntent.resolveActivity(packageManager)?.also {
                    startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*} else {
            doPermission(permission)
        }*/
    }

    private fun doPermission(permission: Array<String>) {
        doRequestPermission(permission, {}, {})
    }

    private fun findByGalleryId(id: Int) {
        if (videos.isNotEmpty()) {
            if (id == BUCKET_ID_ALL_VIDEO) {
                videosTemp = videos
                adapter.data = videos
                return
            } else {
                val videosFind = mutableListOf<Video>()
                videos.forEach {
                    if (it.bucketId == id) {
                        videosFind += it
                    }
                }
                adapter.data = videosFind
            }
        }
    }

    private fun fetchVideoInfo(uri: Uri): Video {
        var video = Video()
        try {
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA
            )
            val query = contentResolver.query(uri, projection, null, null, null)
            query?.use { cursor ->
                cursor.moveToFirst()
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                val bucketId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID))
                val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                video = Video(contentUri, name, duration, bucketId, bucketName, path)
                cursor.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return video
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
}