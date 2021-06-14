package com.dev.anhnd.mybaselibrary.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.InflateException
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.utils.app.logD
import com.dev.anhnd.mybaselibrary.utils.app.logE
import com.dev.anhnd.mybaselibrary.utils.app.openAppSetting
import com.dev.anhnd.mybaselibrary.viewmodel.manual.Event

abstract class BaseActivity<DB : ViewDataBinding> : AppCompatActivity(), Observer<Event>, IBaseView {

    companion object {
        private const val REQUEST_PERMISSION = 1
    }

    protected lateinit var binding: DB

    private var onAllow: (() -> Unit)? = null
    private var onDenied: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            if (fixSingleTask()) {
                if (!isTaskRoot) {
                    finish()
                    return
                }
            }
            binding = DataBindingUtil.setContentView(this, getLayoutId())
            binding.lifecycleOwner = this
            setUp()
            observerViewModel()
            logD("called")
        } catch (e: InflateException) {
            e.printStackTrace()
            logE(e)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            logE(e)
        } catch (e: Exception) {
            e.printStackTrace()
            logE(e)
        }
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun setUp()

    abstract fun observerViewModel()

    protected open fun fixSingleTask(): Boolean = false

    protected open fun setFullScreenDisplayCutout(): Boolean = false

    protected open fun isOpenSettingIfCheckNotAskAgainPermission() = true

    override fun onChanged(event: Event?) {
        event?.apply {
            if (isLoading) {
                onShowLoading()
            } else {
                onHideLoading()
            }
            if (message != null) onShowMessage(message!!)
        }
    }

    fun checkPermission(permissions: Array<String>): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.forEach {
                if (checkSelfPermission(it) == PackageManager.PERMISSION_DENIED) {
                    return false
                }
            }
        }
        return true
    }

    @SuppressLint("ObsoleteSdkInt")
    protected fun doRequestPermission(
        permissions: Array<String>,
        onAllow: () -> Unit = {},
        onDenied: () -> Unit = {}
    ) {
        this.onAllow = onAllow
        this.onDenied = onDenied
        if (checkPermission(permissions)) {
            onAllow()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissions, REQUEST_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (checkPermission(permissions)) {
            onAllow?.invoke()
        } else {
            onDenied?.invoke()
            openAppSetting(this, requestCode)
        }
    }

    override fun onShowLoading() {
    }

    override fun onHideLoading() {
    }

    override fun onShowMessage(message: String) {
        try {
            var toast = Toast(this)
            runOnUiThread {
                toast.cancel()
                toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
                toast.show()
            }
        } catch (e: IllegalStateException) {
            logE(e)
        } catch (e: Exception) {
            logE(e)
        }
    }

    override fun onShowMessage(resId: Int) {
        try {
            runOnUiThread {
                Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show()
            }
        } catch (e: IllegalStateException) {
            logE(e)
        } catch (e: Exception) {
            logE(e)
        }
    }

    override fun hideKeyboard() {
        val inputMethodManager: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    override fun showKeyboard(view: View?) {
    }

    override fun handleError(errorCode: Int?, throwable: Throwable?) {
    }
}