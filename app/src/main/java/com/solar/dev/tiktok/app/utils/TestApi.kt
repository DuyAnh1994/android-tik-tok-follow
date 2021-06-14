package com.solar.dev.tiktok.app.utils

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File


fun main() {

    /* val apiServiceVersion2 = APIConstant.getDataVersion2()!!

     val path = ""
     val obj = OrderLike(
         videoLink = "url video",
         deviceId = "device_3",
         orderNum = 12,
         thumbnail = "url thumbnail",
         name = "name",
     )


     CoroutineScope(Dispatchers.IO).launch {
         val body = getBody(path, obj)
         val res = apiServiceVersion2.requestLike(body).waitResponse(ResponseOrderLike::class.java, {
             print("${it.message}")
         }, {

         })
         res?.let {
             print(it.statusCode)
             print(it.message)
             print(it.content)
         }
     }*/
}


suspend fun getBody(path: String, obj: Any): RequestBody = withContext(Dispatchers.IO) {
    val mediaType = "application/json".toMediaTypeOrNull()
    return@withContext MultipartBody.Builder().setType(MultipartBody.FORM)
        .addFormDataPart("file", path, RequestBody.create(mediaType, File(path)))
        .addFormDataPart("orderJSON", setObjectToJson(obj))
        .build()
}

private suspend fun setObjectToJson(obj: Any): String = withContext(Dispatchers.IO) {
    return@withContext Gson().toJson(obj)
}