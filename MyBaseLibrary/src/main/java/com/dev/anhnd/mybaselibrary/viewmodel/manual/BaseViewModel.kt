package com.dev.anhnd.mybaselibrary.viewmodel.manual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dev.anhnd.mybaselibrary.model.IBaseView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

abstract class BaseViewModel<V : IBaseView> : ViewModel(), CoroutineScope {

    var event = MutableLiveData<Event>()

    private lateinit var baseView: V

    fun onAttachView(view: V) {
        baseView = view
    }

    protected fun getView() : V = baseView

    private val parentJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() =  parentJob

    suspend fun Any.await(): Any {
        return suspendCoroutine {
            it.resume(this)
        }
    }

    suspend fun <T> Call<T>.waitResponses(clazz: Class<T>, onError: (Throwable) -> Unit): T? {
        event.postValue(Event(isLoading = true))
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                    onError.invoke(t)
                }

                override fun onResponse(call: Call<T>, response: Response<T>) {
                    if (response.isSuccessful) {
                        event.postValue(Event(isLoading = false))
                        continuation.resume(response.body())
                    } else {
                        continuation.resumeWithException(HttpException(response))
                    }
                }
            })
        }
    }

    suspend fun <T> T.waitResponses(): T? {
        event.postValue(Event(isLoading = true))
        return suspendCoroutine { continuation ->
            continuation.resume(this)
//            event.postValue(Event(isLoading = false))
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineContext.cancelChildren()
    }
}

data class Event(
    var isLoading: Boolean = false,
    var action: EventAction? = null,
    var error: Throwable? = null,
    var message: String? = null,
    var isDialog: Boolean = false
)

enum class EventAction {
    NONE,
    LOADING
}