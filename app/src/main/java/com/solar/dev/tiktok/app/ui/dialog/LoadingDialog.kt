package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogLoadingAppBinding

class LoadingDialog(private val activity: BaseActivity<*>) : BaseDialog<DialogLoadingAppBinding>() {

    override fun getLayoutId() = R.layout.dialog_loading_app

    override fun isCheckCancel() = false

    override fun getDialogInfo(): DialogInfo {
        return DialogInfo(
            resources.getDimension(R.dimen._200sdp).toInt(),
            resources.getDimension(R.dimen._100sdp).toInt(),
            Gravity.CENTER,
            android.R.color.transparent,
            -1
        )
    }

    override fun setUp() {
        binding.bindingListener = this
        Glide.with(activity).load(R.drawable.gif_loading).into(binding.ivGifView)
    }
}
