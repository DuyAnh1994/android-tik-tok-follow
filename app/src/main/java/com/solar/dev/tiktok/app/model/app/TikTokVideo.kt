package com.solar.dev.tiktok.app.model.app

data class TikTokVideo(
    var name: String = "",
    var thumbnail: String = "",
    val like: Long = 0,
    val urlFull: String = "",
    val urlShort: String = "",
    val playUrl: String = "",
    val key: String = "",
    val verify: Boolean = false
)