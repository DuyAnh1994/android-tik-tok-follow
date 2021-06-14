package com.solar.dev.tiktok.app.model.api.body

import com.google.gson.annotations.SerializedName

data class OrderFollower(
    @SerializedName("account")
    val accountLink: String = "",
    @SerializedName("deviceId")
    val deviceId: String = "",
    @SerializedName("orderNum")
    val orderNum: Int = 0,
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("name")
    val name: String = ""
)