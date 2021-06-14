package com.solar.dev.tiktok.app.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.solar.dev.tiktok.app.model.app.RecentVideo
import kotlinx.coroutines.*
import java.io.*
import java.text.DecimalFormat

object FileUtils {

    private val TAG = FileUtils::class.java.simpleName

    var searchCharSequence: CharSequence = ""

    fun getAudioOutputPath(extension: String): String {
        val folderName = Constant.FOLDER_INTERNAL_AUDIO_EDIT
        val internalPackage = Environment.getExternalStorageDirectory()
        Log.d(TAG, "1: $internalPackage\t 2: $folderName")
        val parentFolder = "$internalPackage/$folderName"
        val pathWithoutExtension = "$parentFolder/Audio"
        var index = 1
        val decimalFormat = DecimalFormat("000")
        var path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        while (File(path).exists()) {
            index++
            path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        }
        return path
    }

    fun getAudioTempPath(extension: String): String {
        val folderName = Constant.FOLDER_TEMP_EDIT
        val internalPackage = Environment.getExternalStorageDirectory()
        Log.d(TAG, "1: $internalPackage\t 2: $folderName")
        val parentFolder = "$internalPackage/$folderName"
        val pathWithoutExtension = "$parentFolder/Video_temp"
        var index = 1
        val decimalFormat = DecimalFormat("000")
        var path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        while (File(path).exists()) {
            index++
            path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        }
        return path
    }

    /*fun getImageRecentOutputPath(extension: String): String {
        val folderName = Constant.FOLDER_INTERNAL_IMAGE_RECENT
        val internalPackage = getApplication().filesDir.parent
        Log.d(TAG, "1: $internalPackage\t 2: $folderName")
        val parentFolder = "$internalPackage/$folderName"
        val pathWithoutExtension = "$parentFolder/Image"
        var index = 1
        val decimalFormat = DecimalFormat("000")
        var path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        while (File(path).exists()) {
            index++
            path = "${pathWithoutExtension}_${decimalFormat.format(index)}.$extension"
        }
        Log.d(TAG, "path: $path")
        return path
    }*/

    fun checkDuplicateKey(video: List<RecentVideo>?, key: String): Boolean {
        Log.d(TAG, "RecentVideo: $video")
        video?.forEach { v ->
            if (v.key == key) {
                Log.d(TAG, "v.key: [$v] equals key=[$key]")
                return false
            }
        }
        return true
    }

    fun getImageRecentOutputPath(name: String, extension: String): String {
        val folderName = Constant.FOLDER_INTERNAL_IMAGE_RECENT
        val internalPackage = getApplication().filesDir.parent
        Log.d(TAG, "parent: $internalPackage\tfolder: $folderName\tname: $name.$extension")
        val absolutePath = "$internalPackage/$folderName/$name.$extension"
        Log.d(TAG, "absolutePath: $absolutePath")
        return absolutePath
    }

    fun moveFileTempToOutput(src: File, dst: File, onSuccess: () -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            src.copyTo(dst, true)
//            delay(2000)
            withContext(Dispatchers.Main) {
                onSuccess.invoke()
            }
        }
    }

    fun rename(context: Context, old: String, new: String, onSuccess: () -> Unit) {
        if (!File(new).exists()) {
            GlobalScope.launch(Dispatchers.IO) {
                val oldFile = File(old)
                val newFile = File(new)
                oldFile.renameTo(newFile)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newFile)))
                try {
                    insertFileToMedia(new)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                withContext(Dispatchers.Main) {
                    onSuccess.invoke()
                }
            }
        }
    }

    private fun insertFileToMedia(output: String): Uri? {
        val file = File(output)
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.TITLE, file.nameWithoutExtension)
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, file.nameWithoutExtension)
        values.put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
        values.put(MediaStore.MediaColumns.SIZE, File(output).length())
        val newUri: Uri?
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.DATA, output)
            newUri = getApplication().contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        } else {
            newUri = getApplication().contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            try {
                getApplication().contentResolver.openOutputStream(newUri!!).use { os ->
                    val file = File(output)
                    val size = file.length()
                    val bytes = ByteArray(size.toInt())
                    val buf = BufferedInputStream(FileInputStream(file))
                    buf.read(bytes, 0, bytes.size)
                    buf.close()

                    os?.write(bytes)
                    os?.close()
                    os?.flush()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return newUri
    }

    fun deleteFileTemp(src: File, onSuccess: () -> Unit) {
        if (src.exists()) {
            if (src.delete()) {
                onSuccess.invoke()
            }
        }
    }

    fun deleteAllFileTemp(dir: File) {
        if (dir.isDirectory) {
            val children = dir.list()
            children?.forEachIndexed { i, _ ->
                File(dir.absolutePath, children[i]).delete()
            }
        }
    }

    fun saveBitmapToInternal(bitmap: Bitmap, dst: String) {
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(dst)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

}