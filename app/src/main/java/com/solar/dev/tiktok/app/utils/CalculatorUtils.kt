package com.solar.dev.tiktok.app.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import okhttp3.internal.format
import java.io.File
import java.text.DecimalFormat
import java.util.*
import java.util.concurrent.*
import kotlin.math.ln
import kotlin.math.pow

object CalculatorUtils {

    private val suffixes by lazy {
        TreeMap<Long, String>().apply {
            put(1_000L, "k");
            put(1_000_000L, "M");
            put(1_000_000_000L, "G");
            put(1_000_000_000_000L, "T");
            put(1_000_000_000_000_000L, "P");
            put(1_000_000_000_000_000_000L, "E");
        }
    }

    fun withSuffix(count: Long): String {
        if (count < 1000) return count.toString()
        val exp = ln(count.toDouble()) / ln(1000.0).toInt()
        val format = DecimalFormat("0.#")
        val value = format.format(count / 1000.0.pow(exp))
        return String.format("%s%c", value, "kMGTPE"[(exp - 1).toInt()])
    }

    fun abbreviateNumber(value: Long): String {
        if (value == Long.MIN_VALUE) return format((Long.MIN_VALUE + 1).toString())
        if (value < 0) return "-" + format("${(-value)}")
        if (value < 1000) return value.toString()

        val e = suffixes.floorEntry(value)
        val divideBy = e.key
        val suffix = e.value

        val truncated = value / (divideBy / 10)
        val hasDecimal = truncated < 100 && (truncated / 10.0) != (truncated / 10.0)
        return if (hasDecimal) {
            (truncated / 10.0).toString() + suffix
        } else {
            (truncated / 10).toString() + suffix
        }
    }

    fun getDuration(duration: Int): String = String.format(
        "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
        TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration.toLong()))
    )

    fun getDuration(context: Context, uriFile: String): String {
        val mediaPlayer = MediaPlayer.create(context, Uri.parse(uriFile))
        val duration = mediaPlayer.duration
        mediaPlayer.release()
        return String.format(
            "%02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(duration.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(duration.toLong()) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(
                    duration.toLong()
                )
            )
        )
    }

    fun getDuration(duration: Long): String {
        val hr: Long = (duration / (1_000 * 60 * 60)) % 24
        val min: Long = (duration / (1_000 * 60))
        val sec: Long = (duration / 1_000) % 60
//        val ms: Long = duration % 10
        return String.format("%02d:%02d:%02d", hr, min, sec)
    }

    fun getDurationLong(context: Context, uriFile: String): Int {
        val retriever = MediaMetadataRetriever()
        retriever.setDataSource(context, Uri.fromFile(File(uriFile)))
        val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return duration!!.toInt()
    }
}