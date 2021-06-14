package com.solar.dev.tiktok.app.ui.tiktok.like.detail

import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.repository.CrawlDataRepository

class LikeDetailViewModel(
    private val crawlDataRepository: CrawlDataRepository
) : BaseViewModel<IBaseView>() {

    var video = crawlDataRepository.video

}