package com.solar.dev.tiktok.app.utils

import android.net.Uri
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.dev.anhnd.mybaselibrary.utils.options
import com.solar.dev.tiktok.app.R

@BindingAdapter("ivLoadImageByUrl", "ivLoadImageType")
fun ImageView.loadBackgroundByUrl(url: String, type: Int) {
    if (type == 0) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.bg_user_error)
            .apply(options.skipMemoryCache(true))
            .circleCrop()
            .into(this)
    } else if (type == 1) {
        Glide.with(this)
            .load(url)
            .error(R.drawable.bg_video_error)
            .apply(options.skipMemoryCache(true))
            .centerCrop()
            .into(this)
    }
}

@BindingAdapter("ivLoadImageByUrl")
fun ImageView.loadBackgroundByUrl(url: String) {
    Glide.with(this)
        .load(url)
        .error(R.drawable.ic_error_img)
        .apply(options.skipMemoryCache(true))
        .centerCrop()
        .into(this)
}

@BindingAdapter("ivLoadImageByUri")
fun ImageView.loadBackgroundByUrl(uri: Uri) {
    Glide.with(this)
        .load(uri)
        .apply(options.skipMemoryCache(true))
        .centerCrop()
        .into(this)
}

@BindingAdapter("tvLoadImageByResLike", "tvLoadImageByResFollow", "tvLoadImageType")
fun TextView.loadBackgroundByRes(resIdLike: Int, resIdFollow: Int, type: Int) {
    if (type == 0) {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, resIdFollow, 0)
    } else if (type == 1) {
        this.setCompoundDrawablesWithIntrinsicBounds(0, 0, resIdLike, 0)
    }
}

@BindingAdapter("loadDuration")
fun TextView.loadDuration(duration : Int) {
    text = CalculatorUtils.getDuration(duration)
}