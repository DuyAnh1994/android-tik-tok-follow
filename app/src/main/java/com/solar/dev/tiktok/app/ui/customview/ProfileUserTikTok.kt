package com.solar.dev.tiktok.app.ui.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.solar.dev.tiktok.app.R
import de.hdodenhof.circleimageview.CircleImageView

class ProfileUserTikTok @JvmOverloads constructor(
    ctx: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(ctx, attrs, defStyle) {

    private val view: View
    private val civCover: CircleImageView
    private val tvFollowing: TextView
    private val tvFollowers: TextView
    private val tvLikes: TextView

    init {
        setupAttributes(attrs, defStyle)
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        view = inflater.inflate(R.layout.layout_profile_user, this)

        view.apply {
            civCover = findViewById(R.id.civCover)
            tvFollowing = findViewById(R.id.tvFollowing)
            tvFollowers = findViewById(R.id.tvFollowers)
            tvLikes = findViewById(R.id.tvLikes)
        }


    }

    private fun setupAttributes(attrs: AttributeSet?, defStyle: Int) {
        val ta = context.theme.obtainStyledAttributes(attrs, R.styleable.appBarTikTok, defStyle, 0)
        ta.recycle()
    }

    fun setCover(ctx: Context, url: String) {
        Glide.with(ctx).load(url).into(civCover)
    }

    fun setFollowing(value: String) {
        tvFollowing.text = value
    }

    fun setFollower(value: String) {
        tvFollowers.text = value
    }

    fun setLikes(value: String) {
        tvLikes.text = value
    }
}

