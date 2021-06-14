package com.dev.anhnd.mybaselibrary.viewmodel.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MultiParamsFactory constructor(protected vararg val params : Any) : ViewModelProvider.NewInstanceFactory()  {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val listClazz = params.map {clazz-> clazz.javaClass }.toTypedArray()
        val constructor = modelClass.getDeclaredConstructor(*listClazz)
        return constructor.newInstance(*params)
    }
}