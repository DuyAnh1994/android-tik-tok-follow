package com.solar.dev.tiktok.app.utils

import android.app.Activity
import android.content.pm.PackageManager
import android.provider.Settings
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dev.anhnd.mybaselibrary.share_preference.IntClass
import com.dev.anhnd.mybaselibrary.utils.app.getApplication
import com.dev.anhnd.mybaselibrary.utils.app.getPrefData
import com.solar.dev.tiktok.app.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


suspend fun <T> Call<T>.waitResponse(clazz: Class<T>, onError: (Throwable) -> Unit, onHideLoading: () -> Unit): T? {
    return suspendCoroutine { continuation ->
        enqueue(object : Callback<T> {
            override fun onFailure(call: Call<T>, t: Throwable) {
                onError.invoke(t)
                onHideLoading.invoke()
            }

            override fun onResponse(call: Call<T>, response: Response<T>) {
                onHideLoading.invoke()
                if (response.isSuccessful) {
                    continuation.resume(response.body()!!)
                } else {

                }
            }
        })
    }
}

fun getDeiceId(): String {
//    return "device_1"
    return Settings.Secure.getString(getApplication().contentResolver, Settings.Secure.ANDROID_ID)
}

fun getStarsOfApp(): Int {
    return IntClass.getPrefData(Constant.KEY_STARS, 0)
}

fun ImageView.setThumbnailForView(url: String, share: Shape) {
    if (share == Shape.SQUARE) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.bg_video_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .centerCrop()
            .into(this)
    } else if (share == Shape.CIRCLE) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.bg_user_error)
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(this)
    }
}

enum class Shape {
    SQUARE, CIRCLE
}

fun Activity.animNext() {
    overridePendingTransition(R.anim.enter_slide_left_to_right, R.anim.enter_slide_right_to_left)
}

fun Activity.animPrevious() {
    overridePendingTransition(R.anim.exit_slide_right_to_left, R.anim.exit_slide_left_to_right)
}

fun appInstalledOrNot(uri: String): Boolean {
    val pm = getApplication().packageManager
    try {
        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}