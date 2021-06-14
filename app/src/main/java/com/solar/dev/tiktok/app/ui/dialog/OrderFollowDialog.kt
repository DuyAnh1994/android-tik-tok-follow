package com.solar.dev.tiktok.app.ui.dialog

import android.view.Gravity
import android.view.View
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.dialog.BaseDialog
import com.dev.anhnd.mybaselibrary.dialog.DialogInfo
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.DialogOrderFollowBinding
import com.solar.dev.tiktok.app.repository.TikTokRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class OrderFollowDialog(
    private val order: Int,
    private val amount: Int
) : BaseDialog<DialogOrderFollowBinding>() {

    private val tikTokRepository by lazy {
        TikTokRepository.getInstance()
    }

    var listener: OrderFollowListener? = null

    override fun getLayoutId() = R.layout.dialog_order_follow

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
        Glide.with(this).load(R.drawable.gif_order_follow).into(binding.ivCover)
        binding.tvContent.text = String.format("Do you want to order +$order followers?")
    }

    fun onClickCancel(v: View) {
        listener?.onClickCancel(v)
    }

    fun onClickConfirm(v: View) {
        CoroutineScope(Dispatchers.IO).launch {
            tikTokRepository.putStars(-amount) {
                listener?.onClickConfirm(v, order)
            }
        }
    }
}

interface OrderFollowListener {

    fun onClickCancel(v: View)

    fun onClickConfirm(v: View, order: Int)
}
