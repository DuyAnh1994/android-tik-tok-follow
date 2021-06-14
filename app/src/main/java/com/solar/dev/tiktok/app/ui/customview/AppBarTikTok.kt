package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.solar.dev.tiktok.app.R

class AppBarTikTok @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    private val ibBack: ImageButton
    private val ivTitle: ImageView
    private val tvTitle: TextView
    private val tvStars: TextView
    var listener: AppBarTikTokListener? = null

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_app_bar_tik_tok, this)

        view.apply {
            ibBack = findViewById(R.id.ibBack)
            ivTitle = findViewById(R.id.ivTitle)
            tvTitle = findViewById(R.id.tvTitle)
            tvStars = findViewById(R.id.tvStars)
        }


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
        tvStars.onDebouncedClick { v ->
            listener?.onClickGetStars(v)
        }
    }

    fun setTitle(resId: Int) {
        tvTitle.visibility = View.GONE
        ivTitle.setImageResource(resId)
        invalidate()
    }

    fun setTitle(value: String) {
        ivTitle.visibility = View.GONE
        tvTitle.text = value
        invalidate()
    }

    fun setStars(value: Int) {
        tvStars.text = value.toString()
    }

    fun visibilityStars(v : Int) {
        tvStars.visibility = v
    }

    interface AppBarTikTokListener {

        fun onClickBack(v: View) {}

        fun onClickGetStars(v: View) {}
    }
}

