package com.solar.dev.tiktok.app.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.MutableLiveData
import com.solar.dev.tiktok.app.model.app.TikTokUser
import com.solar.dev.tiktok.app.model.app.TikTokVideo
import com.solar.dev.tiktok.app.utils.TikTokUtils
import java.net.HttpURLConnection
import java.net.URL

class CrawlDataRepository {

    var video = MutableLiveData<TikTokVideo>()
    var user = MutableLiveData<TikTokUser>()

    suspend fun fetchVideoFromUrl(url: String): TikTokVideo {
        val result = TikTokUtils.crawlDataVideoFromUrl(url)
        video.postValue(result)
        return result
    }

    suspend fun fetchUserFromUrl(url: String): TikTokUser {
        val result = TikTokUtils.crawlDataUserFromUrl(url)
        user.postValue(result)
        return result
    }

    suspend fun downloadImageFromUrl(src: String): Bitmap? {
        return TikTokUtils.downloadImageFromUrl(src)
    }

    companion object {
        @Volatile
        private var instance: CrawlDataRepository? = null

        @Synchronized
        fun getInstance(): CrawlDataRepository {
            if (instance == null) {
                instance = CrawlDataRepository()
            }
            return instance!!
        }

        fun clear() {
            instance = null
        }
    }

}