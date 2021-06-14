package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName

data class RecommendVideo(
    @SerializedName("video")
    val videoURL: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("playURL")
    val playURL: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("deviceId")
    val deviceId: String = ""
)