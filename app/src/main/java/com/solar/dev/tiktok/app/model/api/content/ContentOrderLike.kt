package com.solar.dev.tiktok.app.model.api.content

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.response.OrderLikeAndFollower

data class ContentOrderLike(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("orders")
    val orders: MutableList<OrderLikeAndFollower> = mutableListOf(),
    @SerializedName("deviceId")
    val deviceId: String = "",
    @SerializedName("video")
    val linkVideo: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("playUrl")
    val playUrl: String = ""
)