package com.dev.anhnd.mybaselibrary.utils.mediaprovider

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class MediaInfo(val getFieldName: String)
