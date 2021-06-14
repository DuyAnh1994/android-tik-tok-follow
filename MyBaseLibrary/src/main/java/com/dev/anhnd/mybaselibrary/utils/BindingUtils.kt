package com.dev.anhnd.mybaselibrary.utils

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick

var options = RequestOptions()
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .centerCrop()

@BindingAdapter("deBounceClick")
fun View.onDeBoundClick(listener: View.OnClickListener?) {
    listener?.let {
        this.onDebouncedClick { _ ->
            it.onClick(this)
        }
    }
}

@BindingAdapter("rvSetFixSize")
fun RecyclerView.setFixSize(set: Boolean) {
    setHasFixedSize(set)
}

@BindingAdapter("loadBackground")
fun View.loadBackground(resId: Int) {
    setBackgroundResource(resId)
}