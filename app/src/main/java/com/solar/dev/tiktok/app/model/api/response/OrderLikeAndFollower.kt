package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName

data class OrderLikeAndFollower(
    @SerializedName("orderAmount")
    val orderAmount: Int = 0,
    @SerializedName("delivered")
    val delivered: Int = 0,
    @SerializedName("initDate")
    val initDate: String = ""
)