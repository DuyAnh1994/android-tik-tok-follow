package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.dev.anhnd.mybaselibrary.utils.onDeBoundClick
import com.solar.dev.tiktok.app.R

class ConfirmBar @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    private val ibClose: ImageButton
    private val tvTitle: TextView
    private val ibDone: ImageButton
    var listener: AppBarEditVideoListener? = null

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_confirm_bar, this)

        view.apply {
            ibClose = findViewById(R.id.ibClose)
            tvTitle = findViewById(R.id.tvTitle)
            ibDone = findViewById(R.id.ibDone)
        }
        ibDone.isEnabled = false

        registerListener()
    }

    private fun setupAttributes(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.appBarTikTok, defStyle, 0)
        ta.recycle()
    }

    private fun registerListener() {
        ibClose.onDebouncedClick { v ->
            listener?.onClickClose(v)
        }
        ibDone.onDebouncedClick { v ->
            listener?.onClickDone(v)
        }
    }

    fun setTitle(value : String) {
        tvTitle.text = value
    }

    fun buttonDone(resId: Int, enable : Boolean) {
        Glide.with(context).load(resId).into(ibDone)
        ibDone.isEnabled = enable
    }

    interface AppBarEditVideoListener {

        fun onClickClose(v: View) {}

        fun onClickDone(v: View) {}

    }
}

