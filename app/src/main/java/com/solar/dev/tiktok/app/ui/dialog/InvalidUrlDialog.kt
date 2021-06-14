package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogInvalidUrlBinding

class InvalidUrlDialog : BaseDialog<DialogInvalidUrlBinding>() {

    override fun getLayoutId() = R.layout.dialog_invalid_url

    override fun isCheckCancel() = true

    override fun getDialogInfo(): DialogInfo {
        return DialogInfo(
            resources.getDimension(R.dimen._280sdp).toInt(),
            resources.getDimension(R.dimen._216sdp).toInt(),
            Gravity.CENTER,
            android.R.color.transparent,
            -1
        )
    }

    override fun setUp() {
        binding.bindingListener = this
        Glide.with(this).load(R.drawable.gif_404).into(binding.ivCover)
    }

    fun onClickOK(v: View) {
        dismiss()
    }
}