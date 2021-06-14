package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.content.ContentOrderStatus

data class ResponseOrderStatus(
    @SerializedName("content")
    val content: ContentOrderStatus?=null,
    @SerializedName("statusCode")
    val statusCode: Int,
    @SerializedName("message")
    val message: String
)