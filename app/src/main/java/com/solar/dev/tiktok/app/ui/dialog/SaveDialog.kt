package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.activity.BaseActivity
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogLoadingSaveBinding

class SaveDialog(private val activity: BaseActivity<*>) : BaseDialog<DialogLoadingSaveBinding>() {

    var listener: SaveListener?=null

    override fun getLayoutId() = R.layout.dialog_loading_save

    override fun isCheckCancel() = true

    override fun getDialogInfo(): DialogInfo {
        return DialogInfo(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            Gravity.CENTER,
            android.R.color.transparent,
            -1
        )
    }

    override fun setUp() {
        binding.listener = this
        Glide.with(activity).load(R.drawable.gif_loading).into(binding.ivGifView)
    }

    fun onClickCancel(v: View) {
        listener?.onClickCancel(v)
    }
}
interface SaveListener {
    fun onClickCancel(v: View)
}