package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.content.ContentListRecommended

data class ResponseListRecommended(
    @SerializedName("content")
    val content: ContentListRecommended,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String
)