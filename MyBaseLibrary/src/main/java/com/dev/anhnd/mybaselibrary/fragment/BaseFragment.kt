package com.dev.anhnd.mybaselibrary.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.model.IBaseView
import com.dev.anhnd.mybaselibrary.viewmodel.manual.Event

abstract class BaseFragment<DB : ViewDataBinding, A : BaseActivity<*>>(
) : Fragment(), Observer<Event>, IBaseView {

    protected lateinit var binding: DB
        private set

    @Suppress("UNCHECKED_CAST")
    val activity by lazy {
        context as A
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)
        beforeSetUp()
        setUp()
        observerViewModel()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    open fun beforeSetUp() {}

    abstract fun setUp()

    abstract fun observerViewModel()

    protected open fun styleFragment(): Int? = null

    protected open fun isFullScreen(): Boolean = false

    protected open fun isHandleBackPress() = true

    open fun onBackScreen() {
        navigateUp()
    }

    private fun navigateUp() {
        try {
            findNavController().navigateUp()
        } catch (e: IllegalArgumentException) {

        } catch (e: Exception) {

        }
    }

    fun navigateTo(actionId: Int, args: Bundle? = null) {
        try {
            with(findNavController()) {
                currentDestination?.getAction(actionId)?.let {
                    if (currentDestination?.id != currentDestination?.getAction(actionId)?.destinationId) {
                        navigate(actionId, args)
                    }
                }
            }
        } catch (e: IllegalArgumentException) {

        } catch (e: Exception) {

        }
    }

    fun popBackStack(tag: String) {
        val backTag = if (tag.isEmpty()) javaClass.simpleName else tag
        activity.supportFragmentManager.popBackStack(
            backTag,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

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

    override fun onShowLoading() {
        activity.onShowLoading()
    }

    override fun onHideLoading() {
        activity.onHideLoading()
    }

    override fun onShowMessage(message: String) {
        activity.onShowMessage(message)
    }

    override fun onShowMessage(resId: Int) {
        activity.onShowMessage(resId)
    }

    override fun hideKeyboard() {
        activity.hideKeyboard()
    }

    override fun showKeyboard(view: View?) {
        activity.showKeyboard(view)
    }

    override fun handleError(errorCode: Int?, throwable: Throwable?) {
        activity.handleError(errorCode, throwable)
    }
}