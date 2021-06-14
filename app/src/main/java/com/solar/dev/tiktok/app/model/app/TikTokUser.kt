package com.solar.dev.tiktok.app.model.app

data class TikTokUser(var name: String = "",
                      var thumbnail: String = "",
                      val following: Long = 0,
                      val follower: Long = 0,
                      val likes: Long = 0,
                      val urlFull: String = "",
                      val urlShort: String = "",
                      val key: String = "",
                      val verify: Boolean = false
)