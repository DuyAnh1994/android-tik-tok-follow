package com.solar.dev.tiktok.app.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.solar.dev.tiktok.app.model.app.TikTokUser
import com.solar.dev.tiktok.app.model.app.TikTokVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File
import java.net.HttpURLConnection
import java.net.SocketException
import java.net.URL
import javax.net.ssl.SSLException
import javax.net.ssl.SSLHandshakeException

object TikTokUtils {

    private val TAG = TikTokUtils::class.java.simpleName

    suspend fun crawlDataVideoFromUrl(urlShort: String) = withContext(Dispatchers.IO) {
        var video = TikTokVideo()
        val document: Document
        try {
            document = Jsoup.connect(remakeUrl(urlShort)).get()
        } catch (e: SSLHandshakeException) {
            e.printStackTrace()
            return@withContext video
        } catch (e: SSLException) {
            e.printStackTrace()
            return@withContext video
        } catch (e: SocketException) {
            e.printStackTrace()
            return@withContext video
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext video
        }
        if (document != null) {
            try {
                val html = document.getElementById("__NEXT_DATA__").html()

                /**
                 * get full link
                 */
                val jsonUrl = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("query")
                    .getJSONObject("\$initialProps")
                val host = jsonUrl.optString("\$host")
                val pageUrl = jsonUrl.optString("\$pageUrl").split("?")
                val urlFull = host + pageUrl[0]
                Log.d(TAG, "urlFull: $urlFull")

                /**
                 * get key
                 */
                val jsonKey = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                val key = jsonKey.optString("key")
                Log.d(TAG, "key: $key")

                // get data
                /*val jSONObject = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("videoData")
                    .getJSONObject("itemInfos")
                val name = jSONObject.optString("text")
                var thumbnail = jSONObject.optJSONArray("coversOrigin")?.getString(0)
                val like = jSONObject.optLong("diggCount")
                var playUrl = jSONObject.optJSONObject("video")?.optJSONArray("urls")?.getString(0)
                if (thumbnail == null) thumbnail = ""
                if (playUrl == null) playUrl = ""*/

                /**
                 * get name video
                 */
                val jSONObject = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("itemInfo")
                    .getJSONObject("itemStruct")
                val name = jSONObject.optString("desc")

                /**
                 * get thumbnail
                 */
                val jSONObjectThumbnail = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("itemInfo")
                    .getJSONObject("itemStruct")
                    .getJSONObject("video")
                val thumbnail = jSONObjectThumbnail.optString("cover")

                /**
                 * get current like
                 */
                val jSONObjectStats = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("itemInfo")
                    .getJSONObject("itemStruct")
                    .getJSONObject("stats")
                val like = jSONObjectStats.optLong("diggCount")

                /**
                 * set object
                 */
                video = TikTokVideo(
                    name = name,
                    thumbnail = thumbnail,
                    like = like,
                    urlFull = remakeUrl(urlFull),
                    urlShort = remakeUrl(urlShort),
                    key = key,
                    verify = true)
            } catch (e: Exception) {
                e.printStackTrace()
                return@withContext video
            }
        }
        return@withContext video
    }

    suspend fun crawlDataUserFromUrl(urlShort: String) = withContext(Dispatchers.IO) {
        var user = TikTokUser()
        val document: Document
        try {
            document = Jsoup.connect(remakeUrl(urlShort)).get()
        } catch (e: SSLHandshakeException) {
            e.printStackTrace()
            return@withContext user
        } catch (e: SSLException) {
            e.printStackTrace()
            return@withContext user
        } catch (e: SocketException) {
            e.printStackTrace()
            return@withContext user
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext user
        }
        if (document != null) {
            try {
                val html = document.getElementById("__NEXT_DATA__").html()

                /**
                 * get full link
                 */

                val jsonUrl = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("query")
                    .getJSONObject("\$initialProps")
                val host = jsonUrl.optString("\$host")
                val pageUrl = jsonUrl.optString("\$pageUrl").split("?")
                val urlFull = host + pageUrl[0]
                Log.d(TAG, "urlFull: $urlFull")

                /**
                 * get key
                 */
                val jsonKey = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("query")
                val key = jsonKey.optString("uniqueId")
                Log.d(TAG, "key: $key")

                // get data
                /*val jSONObject = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("userData")
                val nickname = jSONObject.optString("nickName")

                val following = jSONObject.optLong("following")
                val fans = jSONObject.optLong("fans")
                val heart = jSONObject.optLong("heart")*/

                /**
                 * get name
                 */
                val jSONObject = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("userInfo")
                    .getJSONObject("user")
                val nickname = jSONObject.optString("nickname")
                val coversMedium = jSONObject.optString("avatarMedium")

                /**
                 * get following, follower, like
                 */
                val jSONObjectStats = JSONObject(html.substring(html.indexOf("{")))
                    .getJSONObject("props")
                    .getJSONObject("pageProps")
                    .getJSONObject("userInfo")
                    .getJSONObject("stats")

                val following = jSONObjectStats.optLong("followingCount")
                val fans = jSONObjectStats.optLong("followerCount")
                val heart = jSONObjectStats.optLong("heartCount")

                user = TikTokUser(
                    name = nickname,
                    thumbnail = coversMedium,
                    following = following,
                    follower = fans,
                    likes = heart,
                    urlFull = remakeUrl(urlFull),
                    urlShort = remakeUrl(urlShort),
                    key = key,
                    verify = true
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return@withContext user
    }

    suspend fun downloadImageFromUrl(src: String) = withContext(Dispatchers.IO) {
        try {
            val url = URL(src)
            val connection = url.openConnection() as HttpURLConnection
            connection.apply {
                doInput = true
                connect()
            }
            val input = connection.inputStream
            return@withContext BitmapFactory.decodeStream(input)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    suspend fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap =
        withContext(Dispatchers.IO) {
            val width: Int = bitmap.width
            val height: Int = bitmap.height
            val scaleWidth = (newWidth / width).toFloat()
            val scaleHeight = (newHeight / height).toFloat()
            // CREATE A MATRIX FOR THE MANIPULATION
            val matrix = Matrix()
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight)
            // "RECREATE" THE NEW BITMAP
            return@withContext Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, false)
        }

    fun updateFiles(path: String): MultipartBody.Part? {
        return try {
            Log.d(TAG, "path: $path")
            val file = File(path)
            if (!file.exists()) {
                Log.e(TAG, "file not exists!")
                return null
            }
            val uri = Uri.fromFile(file)
//            val type = getApplication().contentResolver.getType(uri)
            val mediaType = "*/*".toMediaTypeOrNull()
            val requestBody = RequestBody.create(mediaType, file)
            MultipartBody.Part.createFormData("upload_file", file.name, requestBody)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getFile(path: String): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val mediaType = "multipart/form-data".toMediaTypeOrNull()
            val file = File(path)
            return@withContext MultipartBody.Part.createFormData("file", file.name, RequestBody.create(mediaType, file))
        }

    suspend fun getObject(obj: Any): RequestBody =
        withContext(Dispatchers.IO) {
            Log.d(TAG, "getObject: ${setObjectToJson(obj)}")
            val mediaType = "multipart/form-data".toMediaTypeOrNull()
            return@withContext RequestBody.create(mediaType, setObjectToJson(obj))
        }

    private suspend fun setObjectToJson(obj: Any): String = withContext(Dispatchers.IO) {
        val value = Gson().toJson(obj)
        return@withContext String.format(value)
    }

    fun remakeUrl(checkUrl: String): String {
        var link = checkUrl
        if (checkUrl.startsWith("www"))
            link = "https://$checkUrl"
        Log.d(TAG, "link: $link")
        return link
    }
}