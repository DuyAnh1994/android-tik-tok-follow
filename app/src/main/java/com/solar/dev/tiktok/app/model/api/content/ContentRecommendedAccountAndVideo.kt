package com.solar.dev.tiktok.app.model.api.content

import com.google.gson.annotations.SerializedName

data class ContentRecommendedAccountAndVideo(
    @SerializedName("id")
    val id: String,
    @SerializedName("deviceId")
    val deviceId: String,
    @SerializedName("listAccounts")
    val listAccounts: MutableList<String> = mutableListOf(),
    @SerializedName("listVideo")
    val listVideo: MutableList<String> = mutableListOf()
)