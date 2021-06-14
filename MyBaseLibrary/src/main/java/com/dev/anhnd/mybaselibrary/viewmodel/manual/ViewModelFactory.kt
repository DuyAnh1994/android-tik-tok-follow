package com.dev.anhnd.mybaselibrary.viewmodel.manual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory  constructor(protected val param: Any) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val constructor = modelClass.getDeclaredConstructor(param::class.java)
        return constructor.newInstance(param)
    }
}