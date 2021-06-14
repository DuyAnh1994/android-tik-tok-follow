package com.solar.dev.tiktok.app.utils.ffmpeg

import android.util.Log
import android.util.Size
import com.arthenica.mobileffmpeg.Config.*
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFprobe
import java.io.File

object MyFFmpeg {
    var isExcuting = false
    var currentOutput = ""
    fun execute(
        //cmd: ArrayList<String>,
        command: String,
        output: String,
        callback: FFMpegCallback,
        duration: Long = -1
    ) {
        //val keyDelimiter = FFmpegUtils.REGEX
        //val command = cmd.joinToString(keyDelimiter)
        val isSuccess = false
        var isFinish = false
        isExcuting = true
        currentOutput = output
        callback.onStart()
        FFmpegUtils.executeAsync({ returnCode, executeOutput ->
            //loge("Return code: $returnCode")
            if (returnCode == RETURN_CODE_SUCCESS) {
                if (output.isNotEmpty())
                    callback.onSuccess(File(output))
                currentOutput = ""
                callback.onSuccess()
                if (!isFinish) {
                    isFinish = true
                    callback.onFinish()
                    isExcuting = false
                }
            } else {
                currentOutput = ""
                callback.onFailure()
                callback.onFinish()
                isExcuting = false
            }
        }, command)

        enableLogCallback { message ->
            callback.onProgressMessage(message)
            Log.e(TAG, "execute: ${message.text}")
        }

        resetStatistics()
        enableStatisticsCallback {
            if (duration != -1L) {
                val percent = (100f * it.time / duration).toInt()
                val frame = it.videoFrameNumber
                //loge("PecentWork: $percent, Time: ${it.time}, $duration")
                if (!isSuccess) {
                    callback.onProgressPercent(percent)
                }
            }
            callback.onProgressInfo(it)
        }
    }

    fun cancel() {
        if (isExcuting) {
            if (currentOutput.isNotEmpty()) {
                val currentFile = File(currentOutput)
                currentFile.delete()
            }
            isExcuting = false
            FFmpeg.cancel()
        }
    }

    fun getMediaInformation(input: File): MediaInfo {
        var size = Size(-1, -1)
        val info = FFprobe.getMediaInformation(input.absolutePath)
        for (streamInfo in info.streams) {
            if (streamInfo.type == "video") {
                size = Size(streamInfo.width.toInt(), streamInfo.height.toInt())
            }
        }
        return MediaInfo(size)
    }

    fun cut(input: File,
            output: String,
            start: String,
            end: String,
            duration: Long,
            callback: FFMpegCallback
    ) {
        val videoPath = input.absolutePath
        val command = StringBuilder("-i ").apply {
            append("'$videoPath' ")
            append("-ss ")
            append("$start ")
            append("-to ")
            append("$end ")
            append("-c copy ")
            append("'$output' -y")
        }
        Log.d(TAG, "cut: $command")
        execute(command.toString(), output, callback, duration)
    }

    fun cutOut(input: File,
               output: String,
               endFirst: Int,
               startSecond: Int,
               durationRoot: Long,
               callback: FFMpegCallback
    ) {
        val videoPath = input.absolutePath
        val first = endFirst / 1000
        val second = startSecond / 1000
        val duration = durationRoot / 1000
        val selectVideo = "between(t,0,$first)+between(t,$second, $duration)"
        val selectAudio = "between(t,0,$first)+between(t,$second, $duration)"

        val command = StringBuilder("-i ").apply {
            append("'$videoPath' ")
            append("-vf ")
            append("\"select = '$selectVideo', setpts=N/FRAME_RATE/TB\" ")
            append("-af ")
            append("\"aselect = '$selectAudio',  asetpts = N/SR/TB\" ")
            append("'$output' -y")
        }
        Log.d(TAG, "cut: $command")
        execute(command.toString(), output, callback, durationRoot)
    }

    fun crop(input: File,
             output: String,
             width: Int,
             height: Int,
             position_x: Int,
             position_y: Int,
             durationRoot: Long,
             callback: FFMpegCallback
    ) {
        val crop = "crop=$width:$height:$position_x:$position_y"
        val videoPath = input.absolutePath
        val command = StringBuilder("-i ").apply {
            append("'$videoPath' ")
            append("-filter:v ")
            append("$crop ")
            append("-c:a copy ")
            append("'$output' -y")
        }
        Log.d(TAG, "cut: $command")
        execute(command.toString(), output, callback, durationRoot)
    }

    fun speed(input: File,
              output: String,
              speed: Float,
              durationRoot: Long,
              callback: FFMpegCallback
    ) {
        val videoPath = input.absolutePath
        val command = StringBuilder("-i ").apply {
            append("'$videoPath' ")
            append("-filter_complex ")
            append("\"[0:v]setpts = PTS/$speed[v];")
            append("[0:a]atempo = $speed[a]\" ")
            append("-map \"[v]\" -map \"[a]\" ")
            append("'$output' -y")
        }
        Log.d(TAG, "cut: $command")
        execute(command.toString(), output, callback, durationRoot)
    }


    data class MediaInfo(var size: Size = Size(0, 0))
}