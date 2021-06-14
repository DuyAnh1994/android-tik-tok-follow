package com.solar.dev.tiktok.app.data.remote

import com.dev.anhnd.mybaselibrary.utils.app.getApplication

object APIConstant {

    const val BASE_URL_VERSION_1 = "http://134.122.21.189:8097/api/v1/"
    const val BASE_URL_VERSION_2 = "http://142.93.14.203:8097/api/v2/"

    /**
     * this method get instance APIService
     */
    fun getDataVersion1() = RetrofitClient.getClient(BASE_URL_VERSION_1)?.create(ApiService::class.java)
}