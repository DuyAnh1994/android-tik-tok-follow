package com.dev.anhnd.mybaselibrary.viewmodel.auto

@Target(AnnotationTarget.CONSTRUCTOR)
annotation class Auto(val singleton: Boolean = true)