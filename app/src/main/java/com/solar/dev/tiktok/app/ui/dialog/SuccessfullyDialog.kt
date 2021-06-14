package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogSuccessfullyBinding

class SuccessfullyDialog(
    private val order: Int
) : BaseDialog<DialogSuccessfullyBinding>() {

    override fun getLayoutId() = R.layout.dialog_successfully

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
        Glide.with(this).load(R.drawable.gif_successfully).into(binding.ivCover)
        binding.tvContent.text = String.format("Order + $order Followers Success. We will try to complete your likes as soon as possible.")
    }

    fun onClickOk(v: View) {
        dismiss()
    }
}