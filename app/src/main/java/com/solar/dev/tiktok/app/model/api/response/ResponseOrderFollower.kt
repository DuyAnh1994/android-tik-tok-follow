package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.content.ContentOrderFollower

data class ResponseOrderFollower(
    @SerializedName("content")
    val content: ContentOrderFollower,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String
)