package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import android.view.View
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogCancelBinding

class CancelDialog() : BaseDialog<DialogCancelBinding>() {

    var listener: CancelListener? = null

    override fun getLayoutId() = R.layout.dialog_cancel

    override fun isCheckCancel() = true

    override fun getDialogInfo(): DialogInfo {
        return DialogInfo(
            resources.getDimension(R.dimen._280sdp).toInt(),
            resources.getDimension(R.dimen._160sdp).toInt(),
            Gravity.CENTER,
            android.R.color.transparent,
            -1
        )
    }

    override fun setUp() {
        binding.bindingListener = this
    }

    fun onClickDiscard(v: View) {
        listener?.onClickDiscard(v)
    }

    fun onClickKeep(v: View) {
        listener?.onClickKeepEditing(v)
    }
}

interface CancelListener {

    fun onClickDiscard(v: View)

    fun onClickKeepEditing(v: View)
}