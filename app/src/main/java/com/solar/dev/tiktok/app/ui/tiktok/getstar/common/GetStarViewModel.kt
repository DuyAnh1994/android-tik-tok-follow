package com.solar.dev.tiktok.app.ui.tiktok.getstar.common

import androidx.lifecycle.viewModelScope
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.model.api.body.DeliverAccount
import com.solar.dev.tiktok.app.model.api.body.DeliverVideo
import com.solar.dev.tiktok.app.model.api.body.RecommendedAccount
import com.solar.dev.tiktok.app.model.api.body.RecommendedVideo
import com.solar.dev.tiktok.app.repository.TikTokRepository
import kotlinx.coroutines.launch

class GetStarViewModel(private val tikTokRepository: TikTokRepository
) : BaseViewModel<IBaseView>() {

    var listRecommended = tikTokRepository.listRecommended

    fun callAPI01(deviceId: String,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.getListRecommended(deviceId, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

    fun callAPI02(video: RecommendedVideo,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.recommendedVideo(video, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

    fun callAPI03(user: RecommendedAccount,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.recommendedUser(user, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

    fun callAPI05(video: DeliverVideo,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.deliverVideo(video, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

    fun callAPI06(user: DeliverAccount,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit) {
        viewModelScope.launch {
            tikTokRepository.deliverUser(user, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }

}