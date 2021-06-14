package com.solar.dev.tiktok.app.model.app

import android.net.Uri

data class Gallery(val id: Int = 0,
                   val name: String = "",
                   val uriFirstVideo: Uri? = null,
                   val count: Int = 0
)