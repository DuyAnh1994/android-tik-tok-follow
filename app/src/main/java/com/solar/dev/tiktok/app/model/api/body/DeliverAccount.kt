package com.solar.dev.tiktok.app.model.api.body

import com.google.gson.annotations.SerializedName

data class DeliverAccount(
    @SerializedName("account")
    val account: String = "",
    @SerializedName("deviceId")
    val deviceId: String = "",
    @SerializedName("deliveredAmount")
    val deliveredAmount: Int = 0
)