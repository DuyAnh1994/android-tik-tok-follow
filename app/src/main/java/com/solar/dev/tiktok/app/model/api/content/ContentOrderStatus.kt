package com.solar.dev.tiktok.app.model.api.content

import com.google.gson.annotations.SerializedName
import com.solar.dev.tiktok.app.model.api.response.OrderStatus

data class ContentOrderStatus(
    @SerializedName("orderStatus")
    val orderStatus: MutableList<OrderStatus> = mutableListOf()
)