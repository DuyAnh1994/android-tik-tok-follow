package com.solar.dev.tiktok.app.ui.tiktok.getstar.common

import androidx.databinding.ViewDataBinding
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.fragment.BaseFragment
import com.dev.anhnd.mybaselibrary.utils.app.logD
import com.solar.dev.tiktok.app.utils.Constant.STATUS_CODE_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Connection.Response
import org.jsoup.Jsoup

abstract class GetStarCommonFragment<DB : ViewDataBinding, A : BaseActivity<*>> : BaseFragment<DB, A>(
), GetStarListener.DataBindingListener {


    fun verifyUrl(url: String, onError: (message: String) -> Unit, onSuccess: (urlFull: String?) -> Unit) {
        if (url.isEmpty()) {
            onError.invoke("Not found link!")
        } else {
            onSuccess.invoke(url)
            /*CoroutineScope(Dispatchers.IO).launch {
                logD("1   ${crawl(url)}")
                val temp = crawl(url)
                if (temp != null) {
                    if (!temp.second) {
                        crawl(temp.first)
                    } else {
                        onSuccess.invoke(temp.first)
                    }
                }
            }*/
        }
    }

    private suspend fun crawl(url: String): Pair<String, Boolean>? = withContext(Dispatchers.IO) {
        try {
            val response = Jsoup.connect(url).followRedirects(false).execute() as Response
            logD("${response.statusCode()}    $url")
            if (response.statusCode() == STATUS_CODE_SUCCESS) {
                return@withContext Pair(url, true)
            }
            if (response.hasHeader("location")) {
                val redirectUrl = response.header("location")
                crawl(redirectUrl)
                return@withContext Pair(redirectUrl, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext null
    }

}