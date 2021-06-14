package com.solar.dev.tiktok.app.ui.tiktok.common

import androidx.lifecycle.viewModelScope
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.repository.TikTokRepository
import kotlinx.coroutines.launch

class TikTokViewModel(
    private val tikTokRepository: TikTokRepository
) : BaseViewModel<IBaseView>() {

    var listOrderStatus = tikTokRepository.listOrderStatus

    var videoRecent = tikTokRepository.videoRecent
    var userRecent = tikTokRepository.userRecent

    fun callAPI04(
        deviceId: String,
        onLoading: () -> Unit,
        onError: (mess: Throwable) -> Unit,
        onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.getOrderStatus(deviceId, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

    fun callVideoRecent() {
        viewModelScope.launch {
            tikTokRepository.getAllVideo()
        }
    }

    fun callUserRecent() {
        viewModelScope.launch {
            tikTokRepository.getAllUser()
        }
    }
}