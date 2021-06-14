package com.solar.dev.tiktok.app.ui.video.common

import androidx.lifecycle.viewModelScope
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.model.app.Video
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditVideoViewModel(
    private val editVideoRepository: EditVideoRepository
) : BaseViewModel<IBaseView>() {

    var videos = editVideoRepository.videos
    var currentVideo = editVideoRepository.currentVideo

    fun fetchVideos(selection: String?,
                    selectionArgs: Array<out String>?,
                    onLoading: () -> Unit,
                    onError: () -> Unit,
                    onSuccess: (videos: MutableList<Video>) -> Unit = {}) {
        onLoading.invoke()
        viewModelScope.launch {
            val result = editVideoRepository.fetchVideos(selection, selectionArgs)
            delay(1000)
            withContext(Dispatchers.Main) {
                onSuccess.invoke(result)
            }
        }
    }

    fun addVideoToEdit(video: Video) {
        editVideoRepository.addVideoToEdit(video)
    }
}