package com.solar.dev.tiktok.app.model.api.body

import com.google.gson.annotations.SerializedName

data class RecommendedVideo(
    @SerializedName("video")
    val video: String,
    @SerializedName("deviceId")
    val deviceId: String
)