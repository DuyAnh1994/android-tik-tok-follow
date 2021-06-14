package com.solar.dev.tiktok.app.data.remote

import com.solar.dev.tiktok.app.model.api.body.*
import com.solar.dev.tiktok.app.model.api.response.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET(APIConstant.BASE_URL_VERSION_1 + "list/recommended?")
    fun getListRecommended(@Query("deviceId") deviceId: String): Call<ResponseListRecommended>

    @PUT(APIConstant.BASE_URL_VERSION_1 + "recommended/video")
    fun recommendedVideo(@Body recommendedVideo: RecommendedVideo): Call<ResponseAccountAndVideo>

    @PUT(APIConstant.BASE_URL_VERSION_1 + "recommended/account")
    fun recommendedAccount(@Body recommendedAccount: RecommendedAccount): Call<ResponseAccountAndVideo>

    @GET(APIConstant.BASE_URL_VERSION_1 + "orders/status?")
    fun getOrderStatus(@Query("deviceId") deviceId: String): Call<ResponseOrderStatus>

    @PUT(APIConstant.BASE_URL_VERSION_1 + "like/video/order/delivered")
    fun deliverLike(@Body deliverVideo: DeliverVideo): Call<ResponseOrderLike>

    @PUT(APIConstant.BASE_URL_VERSION_1 + "follower/order/delivered")
    fun deliverFollower(@Body deliverAccount: DeliverAccount): Call<ResponseOrderFollower>

    @Multipart
    @PUT(APIConstant.BASE_URL_VERSION_2 + "order/like/video")
    fun requestLike(@Part file: MultipartBody.Part,
                    @Part("orderJSON") orderLike: RequestBody): Call<ResponseOrderLike>

    @Multipart
    @PUT(APIConstant.BASE_URL_VERSION_2 + "order/follower")
    fun requestFollower(@Part file: MultipartBody.Part,
                        @Part("orderJSON") orderFollower: RequestBody): Call<ResponseOrderFollower>
}