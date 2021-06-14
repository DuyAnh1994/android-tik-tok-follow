package com.dev.anhnd.mybaselibrary.utils.mediaprovider

import android.net.Uri
import java.io.Serializable

abstract class BaseMediaModel : Serializable {
    abstract fun getUri(): Uri
}