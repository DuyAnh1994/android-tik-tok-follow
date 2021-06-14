package com.solar.dev.tiktok.app.ui.tiktok.like.prepare

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel
import com.solar.dev.tiktok.app.model.app.RecentVideo
import com.solar.dev.tiktok.app.repository.CrawlDataRepository
import com.solar.dev.tiktok.app.repository.TikTokRepository
import com.solar.dev.tiktok.app.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody

class LikeViewModel(
    private val crawlDataRepository: CrawlDataRepository,
    private val tikTokRepository: TikTokRepository
) : BaseViewModel<IBaseView>() {

    private val TAG = LikeViewModel::class.java.simpleName
    var video = crawlDataRepository.video

    fun fetchVideo(url: String,
                   onLoading: () -> Unit,
                   onError: () -> Unit,
                   onSuccess: (recentVideo: RecentVideo) -> Unit) {
        onLoading.invoke()
        viewModelScope.launch {
            /*
            (1). Crawl data từ short link
            (2). Kiểm tra trong database xem url có chứa key vừa fetch đc ko ->
                nếu ko thì thực hiện (3)
                nếu có thì gán url thumbnail theo key đó
            (3). Tải ảnh từ url thumbnail và đặt tên ảnh là [key.extension] vừa fetch đc
            (4). Lưu vào thư mục -> /data/data/com.solar.dev.tiktok.app/TikLike_image_recent
            (5). Đường dẫn của thư mục đó giờ thành url của RecentVideo
            */
            val result = crawlDataRepository.fetchVideoFromUrl(url)

            var bitmap: Bitmap? = null
            val list = tikTokRepository.getAllVideo().value
            Log.d("listss", list?.size.toString())
            if (FileUtils.checkDuplicateKey(list, result.key)) {
                bitmap = crawlDataRepository.downloadImageFromUrl(result.thumbnail)
            }

//            val bitmap = crawlDataRepository.downloadImageFromUrl(result.thumbnail)
            val urlThumbnailInDB = FileUtils.getImageRecentOutputPath(result.key, "png")
            bitmap?.let { bm ->
                FileUtils.saveBitmapToInternal(bm, urlThumbnailInDB)
            }
            result.thumbnail = urlThumbnailInDB
            withContext(Dispatchers.Main) {
                if (result.verify) {
                    val recentVideo = RecentVideo(
                        name = result.name,
                        urlFull = result.urlFull,
                        urlShort = result.urlShort,
                        thumbnail = result.thumbnail,
                        key = result.key
                    )
                    onSuccess.invoke(recentVideo)
                } else {
                    onError.invoke()
                }
            }
        }
    }

    fun insert(recentVideo: RecentVideo) {
        viewModelScope.launch {
            tikTokRepository.insert(recentVideo)
        }
    }

    fun callAPI07(file: MultipartBody.Part,
                  orderLike: RequestBody,
                  onLoading: () -> Unit,
                  onError: (mess: Throwable) -> Unit,
                  onHideLoading: () -> Unit
    ) {
        viewModelScope.launch {
            tikTokRepository.requestLike(file, orderLike, {
                onLoading.invoke()
            }, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        }
    }
}