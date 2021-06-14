package com.dev.anhnd.mybaselibrary.viewmodel.auto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

open class AutoMultiParamFactory constructor(protected vararg val params: Any) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val listClass = params.map { clazz -> clazz.javaClass }.toTypedArray()
        val constructor = modelClass.getDeclaredConstructor(*listClass)
        return constructor.newInstance(*params)
    }
}