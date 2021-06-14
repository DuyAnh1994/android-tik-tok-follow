package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.content.ContentRecommendedAccountAndVideo

data class ResponseAccountAndVideo(
    @SerializedName("content")
    val content: ContentRecommendedAccountAndVideo,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String
)