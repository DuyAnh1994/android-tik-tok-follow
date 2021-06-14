package com.solar.dev.tiktok.app.model.app

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Video(
    val uri: Uri? = null,
    val name: String = "",
    val duration: Int = 0,
    val bucketId: Int = 0,
    val bucketName: String = "",
    val absolutePath: String = ""
): Parcelable
