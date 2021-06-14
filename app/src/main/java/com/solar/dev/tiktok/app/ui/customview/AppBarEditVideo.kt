package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.dev.anhnd.mybaselibrary.utils.onDeBoundClick
import com.solar.dev.tiktok.app.R

class AppBarEditVideo @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    private val ibBack: ImageButton
    private val tvGallery: TextView
    private val ibCamera: ImageButton
    var listener: AppBarEditVideoListener? = null

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_app_bar_edit_video, this)

        view.apply {
            ibBack = findViewById(R.id.ibBack)
            tvGallery = findViewById(R.id.tvGallery)
            ibCamera = findViewById(R.id.ibCamera)
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
        tvGallery.onDeBoundClick { v ->
            listener?.onClickGallery(v)
        }
        ibCamera.onDebouncedClick { v ->
            listener?.onClickCamera(v)
        }
    }

    interface AppBarEditVideoListener {

        fun onClickBack(v: View) {}

        fun onClickGallery(v: View) {}

        fun onClickCamera(v: View) {}

    }
}

