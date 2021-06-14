package com.solar.dev.tiktok.app.utils

import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.solar.dev.tiktok.app.model.app.Video
import java.io.File

object EditVideoUtils {

    fun getAllVideo(selection: String?, selectionArgs: Array<out String>?): MutableList<Video> {
        val videos = mutableListOf<Video>()
        try {
            val contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.BUCKET_ID,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.DATA
            )
            val query = getApplication().contentResolver.query(
                contentUri,
                projection,
                selection,
                selectionArgs,
                MediaStore.Audio.Media.DATE_ADDED
            )
            query?.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID))
//                    val name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME))
                    val duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
                    val bucketId = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_ID))
                    val bucketName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME))
                    val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    val uri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                    val file = File(path)
                    if (file.exists() && duration > 1000 && !path.contains(Constant.FOLDER_MUSIC) && !path.contains(Constant.FOLDER_MUSIC_1)) {
//                    if (file.exists()) {
                        val name = file.name
                        videos += Video(uri, name, duration, bucketId, bucketName, path)
                    }
                }
                cursor.close()
            }
            query?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        videos.reverse()
        return videos
    }
}