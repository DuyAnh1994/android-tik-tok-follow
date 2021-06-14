package com.solar.dev.tiktok.app.repository

import androidx.lifecycle.MutableLiveData
import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.solar.dev.tiktok.app.model.app.Gallery
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.utils.EditVideoUtils

class EditVideoRepository {

    var videos = MutableLiveData<MutableList<Video>>()
    var gallery = MutableLiveData<MutableMap<String, MutableList<Video>?>>()
    var galleryId  = MutableLiveData<Int>()
    var currentVideo = MutableLiveData<Video>()
    var saveSuccessfully = MutableLiveData<Boolean>()

    fun fetchVideos(selection: String?, selectionArgs: Array<out String>?): MutableList<Video> {
        val result = EditVideoUtils.getAllVideo(selection, selectionArgs)
        this.videos.value = result
        getGallery(result)
        return result
    }

    private fun getGallery(videos: MutableList<Video>) {
        val map = mutableMapOf<String, MutableList<Video>?>()
        videos.forEach {
            if (map.containsKey(it.bucketName)) {
                val album = map[it.bucketName]
                album?.add(it)
                map[it.bucketName] = album
            } else {
                val album = mutableListOf<Video>()
                album += it
                map[it.bucketName] = album
            }
        }
        gallery.value = map
    }

    fun setGalleryId(id: Int) {
        galleryId.value = id
    }

    fun addVideoToEdit(video: Video) {
        currentVideo.value = video
    }

    companion object {
        @Volatile
        private var instance: EditVideoRepository? = null

        @Synchronized
        fun getInstance(): EditVideoRepository {
            if (instance == null) {
                instance = EditVideoRepository()
            }
            return instance!!
        }

        fun clear() {
            instance = null
        }
    }
}