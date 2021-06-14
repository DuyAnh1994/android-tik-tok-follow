package com.dev.anhnd.mybaselibrary.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment

abstract class BaseDialog<DB : ViewDataBinding> : DialogFragment() {

    protected lateinit var binding: DB
        private set

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        isCancelable = isCheckCancel()
    }

    override fun onStart() {
        setupDialog(getDialogInfo())
        super.onStart()
    }

    @LayoutRes
    abstract fun getLayoutId(): Int

    abstract fun getDialogInfo(): DialogInfo

    abstract fun setUp()

    protected open fun isCheckCancel() : Boolean = false

    private fun setupDialog(dialogInfo: DialogInfo) {
        dialog?.window?.apply {
            setLayout(dialogInfo.width, dialogInfo.height)
            setGravity(dialogInfo.gravity)
            setBackgroundDrawableResource(dialogInfo.bgDrawableRes)
        }
    }
}

data class DialogInfo(
    val width: Int = -1,
    val height: Int = -1,
    val gravity: Int = -1,
    val bgDrawableRes: Int = -1,
    val animation: Int = -1
)