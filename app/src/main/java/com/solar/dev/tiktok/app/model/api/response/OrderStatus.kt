package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName

open class OrderStatus(
    @SerializedName("count")
    val count: Int = 0,
    @SerializedName("delivered")
    val delivered: Int = 0,
    @SerializedName("link")
    val link: String = "",
    @SerializedName("type")
    val type: Int = 0,
    @SerializedName("initDate")
    val initDate: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("name")
    val name: String = "",
    var cover: Int = 0
)