package com.solar.dev.tiktok.app.ui.tiktok.follow.detail

import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.repository.CrawlDataRepository

class FollowDetailViewModel(
    private val crawlDataRepository: CrawlDataRepository
) : BaseViewModel<IBaseView>() {

    var video = crawlDataRepository.video

}