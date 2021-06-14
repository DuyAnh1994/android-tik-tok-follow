package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.dev.anhnd.mybaselibrary.utils.onDeBoundClick
import com.solar.dev.tiktok.app.R

class AppBarPreview @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    private val ibBack: ImageButton
    private val ibSave: ImageButton
    var listener: AppBarEditVideoListener? = null

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_app_bar_preview, this)

        view.apply {
            ibBack = findViewById(R.id.ibBack)
            ibSave = findViewById(R.id.ibSave)
        }
        ibSave.isEnabled = false

        registerListener()
    }

    private fun setupAttributes(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.appBarTikTok, defStyle, 0)
        ta.recycle()
    }

    private fun registerListener() {
        ibBack.onDebouncedClick { v ->
            listener?.onClickBack(v)
        }
        ibSave.onDebouncedClick { v ->
            listener?.onClickSave(v)
        }
    }

    fun setButtonSave(enable: Boolean) {
        ibSave.isEnabled = enable
        if (enable) {
            Glide.with(context).load(R.drawable.bt_save_enable).into(ibSave)
        } else {
            Glide.with(context).load(R.drawable.bt_save_disable).into(ibSave)
        }
    }

    interface AppBarEditVideoListener {

        fun onClickBack(v: View) {}

        fun onClickSave(v: View) {}

    }
}

