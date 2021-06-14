package com.dev.anhnd.mybaselibrary.utils.app

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.viewmodel.manual.BaseViewModel

const val TAG = "MyBaseLibrary"

fun <T : BaseViewModel<*>> BaseActivity<*>.createViewModel(clz: Class<T>): T {
    return ViewModelProvider(this)[clz]
}

fun <T : BaseViewModel<*>> BaseFragment<*, *>.createViewModel(clz: Class<T>): T {
    return ViewModelProvider(this)[clz]
}

fun <T> BaseActivity<*>.observer(liveData: LiveData<T>, onChange: (T?) -> Unit) {
    liveData.observe(this, Observer(onChange))
}

fun <T> BaseFragment<*, *>.observer(liveData: LiveData<T>, onChange: (T?) -> Unit) {
    liveData.observe(viewLifecycleOwner, Observer(onChange))
}


fun <T> MutableLiveData<T>.setAndPostValue(data: T) {
    value = data
    postValue(data)
}




