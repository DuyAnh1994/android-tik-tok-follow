package com.solar.dev.tiktok.app.ui.video.gallery

import android.net.Uri
import android.view.View
import com.dev.anhnd.mybaselibrary.adapter.BaseAdapter
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.utils.app.observer
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.FragmentGalleryBinding
import com.solar.dev.tiktok.app.model.app.Gallery
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.ui.callback.ItemListener
import com.solar.dev.tiktok.app.ui.video.common.EditVideoActivity
import com.solar.dev.tiktok.app.ui.video.common.EditVideoActivity.Companion.BUCKET_ID_ALL_VIDEO

class EditVideoGalleryFragment : BaseFragment<FragmentGalleryBinding, EditVideoActivity>(), EditVideoGalleryListener.DataBindingListener, ItemListener.GalleryListener {

    private val videoRepository by lazy {
        EditVideoRepository.getInstance()
    }
    private val adapter by lazy {
        BaseAdapter<Gallery>(R.layout.item_gallery).apply {
            listener = this@EditVideoGalleryFragment
        }
    }
    private var gallery = mutableListOf<Gallery>()

    override fun getLayoutId() = R.layout.fragment_gallery

    override fun setUp() {
        binding.bindingListener = this
        binding.rvGallery.adapter = adapter
    }

    override fun observerViewModel() {
        observer(videoRepository.gallery) {
            it?.let { map ->
                var allSize = 0
                var uriAllVideo: Uri? = null
                gallery.clear()
                map.forEach { v ->
                    gallery.plusAssign(Gallery(id = v.value!![0].bucketId, name = v.key, uriFirstVideo = v.value!![0].uri, count = v.value!!.size))
                    allSize += v.value!!.size
                    uriAllVideo = v.value!![0].uri
                }
                gallery.add(0, Gallery(id = BUCKET_ID_ALL_VIDEO, name = "All Video", uriFirstVideo = uriAllVideo, count = allSize))
                adapter.data = gallery
            }
        }
    }

    override fun onClickItem(v: View, item: Gallery) {
        videoRepository.setGalleryId(item.id)
        activity.onBackPressed()
    }

    override fun onClickAllVideo(v: View) {
        activity.onBackPressed()
    }
}

class EditVideoGalleryListener  {

    interface DataBindingListener : IBaseView {

        fun onClickAllVideo(v: View)
    }
}