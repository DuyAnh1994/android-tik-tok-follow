package com.solar.dev.tiktok.app.ui.tiktok.buystar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.dev.anhnd.mybaselibrary.utils.app.onDebouncedClick
import com.solar.dev.tiktok.app.R
import com.solar.dev.tiktok.app.databinding.ItemBuyStarsProBinding
import com.solar.dev.tiktok.app.databinding.ItemBuyStarsSaleBinding
import com.solar.dev.tiktok.app.model.app.BuyStar
import com.solar.dev.tiktok.app.model.app.Sale

class BuyStarAdapter(
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var listener: BuyStarListener? = null

    var data: List<BuyStar> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            SALE -> {
                val binding = DataBindingUtil.inflate<ItemBuyStarsSaleBinding>(inflater, R.layout.item_buy_stars_sale, parent, false)
                SaleViewHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<ItemBuyStarsProBinding>(inflater, R.layout.item_buy_stars_pro, parent, false)
                ProViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SaleViewHolder -> {
                holder.bindData(data[holder.adapterPosition])
            }
            is ProViewHolder -> {
                holder.bindData(data[holder.adapterPosition])
            }
        }

    }

    override fun getItemCount() = data.size

    override fun getItemViewType(position: Int): Int {
        return when (data[position].type) {
            SALE -> {
                SALE
            }
            else -> {
                PRO
            }
        }
    }

    inner class SaleViewHolder(val binding: ItemBuyStarsSaleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: BuyStar) {
            binding.apply {
                tvStar.text = item.star.toString()
                tvName.text = String.format("Get ${item.star} stars")
                tvPrice.text = item.price

                when (item.saleType) {
                    Sale.NORMAL -> {
                        tvDeal.visibility = View.GONE
                        tvSale.visibility = View.GONE
                    }
                    Sale.SAVE -> {
                        tvDeal.visibility = View.GONE
                        tvSale.apply {
                            visibility = View.VISIBLE
                            text = "Save ${item.sale}%"
                        }
                    }
                    Sale.BEST_DEAL -> {
                        tvSale.visibility = View.GONE
                        tvDeal.apply {
                            visibility = View.VISIBLE
                            text = String.format("Best Deal")
                        }
                    }
                }
                cvBuyStars.onDebouncedClick { v ->
                    listener?.onItemClick(v, item)
                }
            }
        }
    }

    inner class ProViewHolder(val binding: ItemBuyStarsProBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: BuyStar) {
            binding.cvTikTokPro.onDebouncedClick {v->
                listener?.onItemClick(v, item)
            }
        }
    }

    companion object {
        const val SALE = 2001
        const val PRO = 2002
    }

    interface BuyStarListener {
        fun onItemClick(v: View, item: BuyStar)
    }
}