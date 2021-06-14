package com.solar.dev.tiktok.app.utils

import com.dev.anhnd.mybaselibrary.viewmodel.manual.MultiParamsFactory
import com.dev.anhnd.mybaselibrary.viewmodel.manual.ViewModelFactory
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.EditVideoRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository

object InjectorUtils {

    private fun getCrawlDataRepository(): CrawlDataRepository = CrawlDataRepository.getInstance()

    private fun getTikTokRepository(): TikTokRepository = TikTokRepository.getInstance()

    private fun getEditVideoRepository(): EditVideoRepository = EditVideoRepository.getInstance()

    fun providerCrawlDataViewModelFactory(): ViewModelFactory =
        ViewModelFactory(getCrawlDataRepository())

    fun providerTikTokViewModelFactory(): ViewModelFactory =
        ViewModelFactory(getTikTokRepository())

    fun providerEditVideoViewModelFactory(): ViewModelFactory =
        ViewModelFactory(getEditVideoRepository())

    fun providerLikeAndFollowViewModelFactory(): MultiParamsFactory =
        MultiParamsFactory(getCrawlDataRepository(), getTikTokRepository())


}