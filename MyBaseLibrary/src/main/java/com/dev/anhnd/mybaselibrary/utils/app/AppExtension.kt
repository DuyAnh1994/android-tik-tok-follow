package com.dev.anhnd.mybaselibrary.utils.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.Settings
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.share_preference.BasePreference


private var appInstance: Application? = null
private var basePreference: BasePreference? = null

fun Application.initBaseApplication() {
    appInstance = this
}

fun getApplication() = appInstance!!

fun Application.initPrefData(preferenceName: String) {
    basePreference = BasePreference(preferenceName, this)
}

fun <T> Class<T>.getPrefData(key: String, defaultValue: T): T =
    basePreference!!.get(key, defaultValue, this)

fun <T> putPrefData(key: String, value: T) = basePreference!!.put(key, value)

fun getAppDrawable(@DrawableRes drawableId: Int, context: Context? = appInstance): Drawable? {
    context?.let {
        return ContextCompat.getDrawable(context, drawableId)
    }
    return null
}

fun getAppColor(@ColorRes colorRes: Int): Int {
    return ResourcesCompat.getColor(getApplication().resources, colorRes, null)
}

fun BaseActivity<*>.openAppSetting(activity: AppCompatActivity, REQ: Int) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    activity.startActivityForResult(intent, REQ)
}