package com.solar.dev.tiktok.app.model.api.response

import com.google.gson.annotations.SerializedName

data class RecommendUser(
    @SerializedName("account")
    val accURL: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("deviceId")
    val deviceId: String = ""
)