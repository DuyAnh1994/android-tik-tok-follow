package com.solar.dev.tiktok.app.model.api.body

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class OrderLike(
    @SerializedName("video")
    val videoLink: String = "",
    @SerializedName("deviceId")
    val deviceId: String = "",
    @SerializedName("orderNum")
    val orderNum: Int = 0,
    @SerializedName("thumbnail")
    var thumbnail: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("playURL")
    val playURL: String = ""
) : Parcelable