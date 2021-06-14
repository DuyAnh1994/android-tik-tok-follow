package com.solar.dev.tiktok.app.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.dev.anhnd.mybaselibrary.utils.app.putPrefData
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.data.local.db.MyDatabase
import com.solar.dev.tiktok.app.data.remote.APIConstant
import com.solar.dev.tiktok.app.model.api.body.DeliverAccount
import com.solar.dev.tiktok.app.model.api.body.DeliverVideo
import com.solar.dev.tiktok.app.model.api.body.RecommendedAccount
import com.solar.dev.tiktok.app.model.api.body.RecommendedVideo
import com.solar.dev.tiktok.app.model.api.response.*
import com.solar.dev.tiktok.app.model.app.RecentUser
import com.solar.dev.tiktok.app.model.app.RecentVideo
import com.solar.dev.tiktok.app.utils.Constant
import com.solar.dev.tiktok.app.utils.getStarsOfApp
import com.solar.dev.tiktok.app.utils.waitResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback

class TikTokRepository {

    private val videoDao = MyDatabase.getDatabase(getApplication()).tikTokVideo()
    private val userDao = MyDatabase.getDatabase(getApplication()).tikTokUser()
    var videoRecent = MutableLiveData<List<RecentVideo>>()
    var userRecent = MutableLiveData<List<RecentUser>>()
    var stars = MutableLiveData<Int>()

    private val apiServiceVersion1 = APIConstant.getDataVersion1()!!
    var listRecommended = MutableLiveData<ResponseListRecommended>()
    var recommendedVideo = MutableLiveData<ResponseAccountAndVideo>()
    var recommendedUser = MutableLiveData<ResponseAccountAndVideo>()
    var listOrderStatus = MutableLiveData<ResponseOrderStatus>()
    var deliverVideo = MutableLiveData<ResponseOrderLike>()
    var deliverUser = MutableLiveData<ResponseOrderFollower>()
    var requestLike = MutableLiveData<ResponseOrderLike>()
    var requestFollow = MutableLiveData<ResponseOrderFollower>()

    /**
     * API 1 : get list recommended
     */
    suspend fun getListRecommended(deviceId: String,
                                   onLoading: () -> Unit,
                                   onError: (mess: Throwable) -> Unit,
                                   onHideLoading: () -> Unit
    ): ResponseListRecommended? {
        onLoading.invoke()
        val result = apiServiceVersion1.getListRecommended(deviceId).waitResponse(ResponseListRecommended::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        val res = apiServiceVersion1.getListRecommended(deviceId)
        listRecommended.postValue(result)
        return result
    }

    /**
     * API 2: recommended video
     */
    suspend fun recommendedVideo(video: RecommendedVideo,
                                 onLoading: () -> Unit,
                                 onError: (mess: Throwable) -> Unit,
                                 onHideLoading: () -> Unit): ResponseAccountAndVideo? {
        onLoading.invoke()
        val result = apiServiceVersion1.recommendedVideo(video).waitResponse(ResponseAccountAndVideo::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        recommendedVideo.postValue(result)
        return result
    }


    /**
     * API 3: recommended video
     */
    suspend fun recommendedUser(user: RecommendedAccount,
                                onLoading: () -> Unit,
                                onError: (mess: Throwable) -> Unit,
                                onHideLoading: () -> Unit): ResponseAccountAndVideo? {
        onLoading.invoke()
        val result = apiServiceVersion1.recommendedAccount(user).waitResponse(ResponseAccountAndVideo::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        recommendedUser.postValue(result)
        return result
    }


    /**
     * API 4 : get order status
     */
    suspend fun getOrderStatus(deviceId: String,
                               onLoading: () -> Unit,
                               onError: (mess: Throwable) -> Unit,
                               onHideLoading: () -> Unit): ResponseOrderStatus? {
        onLoading.invoke()
        val result = apiServiceVersion1.getOrderStatus(deviceId).waitResponse(ResponseOrderStatus::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        result?.let {
            val content = it.content
            if (content != null) {
                val list = content.orderStatus
                if (list.size > 0) {
                    list.forEachIndexed { _, orderStatus ->
                        if (orderStatus.type == Constant.TYPE_REQUEST_LIKES) {
                            orderStatus.cover = R.drawable.ic_like_small
                        } else if (orderStatus.type == Constant.TYPE_REQUEST_FOLLOWERS) {
                            orderStatus.cover = R.drawable.ic_follow_small
                        }
                    }
                }
            }
        }
        listOrderStatus.postValue(result)
        return result
    }

    /**
     * API 5 : deliver video
     */
    suspend fun deliverVideo(video: DeliverVideo,
                             onLoading: () -> Unit,
                             onError: (mess: Throwable) -> Unit,
                             onHideLoading: () -> Unit
    ): ResponseOrderLike? {
        onLoading.invoke()
        val result = apiServiceVersion1.deliverLike(video).waitResponse(ResponseOrderLike::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        deliverVideo.postValue(result)
        return result
    }

    /**
     * API 6 : deliver user
     */
    suspend fun deliverUser(user: DeliverAccount,
                            onLoading: () -> Unit,
                            onError: (mess: Throwable) -> Unit,
                            onHideLoading: () -> Unit
    ): ResponseOrderFollower? {
        onLoading.invoke()
        val result = apiServiceVersion1.deliverFollower(user).waitResponse(ResponseOrderFollower::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        deliverUser.postValue(result)
        return result
    }


    /**
     * API 7 : order like
     */
    suspend fun requestLike(file: MultipartBody.Part,
                            orderLike: RequestBody,
                            onLoading: () -> Unit,
                            onError: (mess: Throwable) -> Unit,
                            onHideLoading: () -> Unit
    ): ResponseOrderLike? {
        onLoading.invoke()
        var result: ResponseOrderLike? = null
        try {
            result = apiServiceVersion1.requestLike(file, orderLike).waitResponse(ResponseOrderLike::class.java, { throwable ->
                onError.invoke(throwable)
            }, {
                onHideLoading.invoke()
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
        requestLike.postValue(result)
        return result
    }

    /**
     * API 8 : order follower
     */
    suspend fun requestFollow(file: MultipartBody.Part,
                              orderFollow: RequestBody,
                              onLoading: () -> Unit,
                              onError: (mess: Throwable) -> Unit,
                              onHideLoading: () -> Unit
    ): ResponseOrderFollower? {
        onLoading.invoke()
        val result = apiServiceVersion1.requestFollower(file, orderFollow).waitResponse(ResponseOrderFollower::class.java, { throwable ->
            onError.invoke(throwable)
        }, {
            onHideLoading.invoke()
        })
        requestFollow.postValue(result)
        return result
    }

    @WorkerThread
    suspend fun insert(recentVideo: RecentVideo) {
        withContext(Dispatchers.IO) {
            videoDao.insert(recentVideo)
        }
    }

    @WorkerThread
    suspend fun insert(recentUser: RecentUser) {
        withContext(Dispatchers.IO) {
            userDao.insert(recentUser)
        }
    }

    fun getAllVideo(): LiveData<List<RecentVideo>> {
        val temp = videoDao.getAllVideo()
        videoRecent.value = temp.value
        return temp
    }

    fun getAllUser(): LiveData<List<RecentUser>> {
        val temp = userDao.getAllUser()
        userRecent.value = temp.value
        return temp
    }

    suspend fun putStars(value: Int, onSuccess: () -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val v = calculatorStars(value)
            putPrefData(Constant.KEY_STARS, v)
            stars.postValue(v)
            withContext(Dispatchers.Main) {
                onSuccess()
            }
        }
    }

    private fun calculatorStars(value: Int): Int {
//        var starsCurrent = IntClass.getPrefData(Constant.KEY_STARS, 0)
        var starsCurrent = getStarsOfApp()
        starsCurrent += value
        return starsCurrent
    }


    companion object {
        @Volatile
        private var instance: TikTokRepository? = null

        @Synchronized
        fun getInstance(): TikTokRepository {
            if (instance == null) {
                instance = TikTokRepository()
            }
            return instance!!
        }

        fun clear() {
            instance = null
        }
    }
}