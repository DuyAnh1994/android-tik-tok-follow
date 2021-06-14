package com.solar.dev.tiktok.app.utils.ffmpeg

import com.arthenica.mobileffmpeg.LogMessage
import com.arthenica.mobileffmpeg.Statistics
import java.io.File

interface FFMpegCallback {
    fun onStart() {}

    fun onProgressPercent(progress: Int) {}

    fun onProgress(progress: String, type: String) {}

    fun onProgressMessage(message: LogMessage) {}

    fun onProgressInfo(info: Statistics) {}

    fun onSuccess(output: File) {}

    fun onSuccess() {}

    fun onFailure(error: Exception? = null) {}

    fun onFinish() {}

}
