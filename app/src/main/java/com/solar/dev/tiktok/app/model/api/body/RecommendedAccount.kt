package com.solar.dev.tiktok.app.model.api.body

import com.google.gson.annotations.SerializedName

data class RecommendedAccount(
    @SerializedName("accountRecommend")
    val accountRecommend: String,
    @SerializedName("deviceId")
    val deviceId: String
)