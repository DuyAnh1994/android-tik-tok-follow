package com.solar.dev.tiktok.app.model.api.content

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.response.RecommendUser
import com.solar.dev.tiktok.app.model.api.response.RecommendVideo

data class ContentListRecommended(
    @SerializedName("followerOrders")
    val followerOrders: MutableList<RecommendUser> = mutableListOf(),
    @SerializedName("likeVideoOrders")
    val likeVideoOrders: MutableList<RecommendVideo> = mutableListOf()
)