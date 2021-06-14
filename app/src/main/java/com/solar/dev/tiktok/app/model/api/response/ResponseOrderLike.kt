package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.content.ContentOrderLike

data class ResponseOrderLike(
    @SerializedName("content")
    val content: ContentOrderLike,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String
)