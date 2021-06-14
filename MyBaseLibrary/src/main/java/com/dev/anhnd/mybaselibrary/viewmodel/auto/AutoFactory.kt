package com.dev.anhnd.mybaselibrary.viewmodel.auto

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AutoFactory : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.createInstanceWithAuto() as T
    }
}