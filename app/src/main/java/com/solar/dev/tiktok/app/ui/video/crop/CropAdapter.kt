package com.solar.dev.tiktok.app.ui.video.crop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.anhnd.mybaselibrary.utils.app.getAppColor
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ItemRatioCropBinding

class CropAdapter(
    private val context: Context,
    private val ratios: MutableList<String>,
    private val listener: CropListener
) : RecyclerView.Adapter<CropAdapter.CropRatioViewHolder>() {

    private var currentPos = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CropRatioViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<ItemRatioCropBinding>(inflater, R.layout.item_ratio_crop, parent, false)
        return CropRatioViewHolder(binding)
    }

    override fun getItemCount(): Int = ratios.size

    override fun onBindViewHolder(holder: CropRatioViewHolder, position: Int) {
        if (currentPos == holder.adapterPosition) {
            holder.binding.tvRatio.setBackgroundResource(R.drawable.custom_ratio_enable)
            holder.binding.tvRatio.setTextColor(getAppColor(R.color.colorBlack_100))
        } else {
            holder.binding.tvRatio.setBackgroundResource(R.drawable.custom_ratio_disable)
            holder.binding.tvRatio.setTextColor(getAppColor(R.color.colorHint))
        }
        holder.bindData(ratios[holder.adapterPosition])
    }

    inner class CropRatioViewHolder(val binding: ItemRatioCropBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(ratio: String) {
            binding.apply {
                tvRatio.text = ratio
                itemView.setOnClickListener { v ->
                    listener.onItemClick(v, adapterPosition)
                    currentPos = adapterPosition
                    notifyDataSetChanged()
                }
                when (adapterPosition) {
                    0 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._40sdp).toInt()
                    }
                    1 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._40sdp).toInt()
                    }
                    2 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._52sdp).toInt()
                    }
                    3 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._52sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._40sdp).toInt()
                    }
                    4 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._72sdp).toInt()
                    }
                    5 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._72sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._40sdp).toInt()
                    }
                    6 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._60sdp).toInt()
                    }
                    7 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._40sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._52sdp).toInt()
                    }
                    8 -> {
                        tvRatio.width = context.resources.getDimension(R.dimen._52sdp).toInt()
                        tvRatio.height = context.resources.getDimension(R.dimen._32sdp).toInt()
                    }
                }
            }
        }
    }

    interface CropListener {
        fun onItemClick(v: View, position: Int)
    }
}