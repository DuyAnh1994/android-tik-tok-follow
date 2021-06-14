package com.dev.anhnd.mybaselibrary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.dev.anhnd.mybaselibrary.BR

open class BaseAdapter<T : Any>(
    @LayoutRes private val resLayout: Int
) : RecyclerView.Adapter<BaseAdapter.BaseViewHolder>() {

    private lateinit var inflater: LayoutInflater

    var listener: ListItemListener? = null

    var data: List<T>? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        if (!::inflater.isInitialized) {
            inflater = LayoutInflater.from(parent.context)
        }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, resLayout, parent, false)
        return BaseViewHolder(binding)
    }

    override fun getItemCount() = data?.size ?: 0

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val item: T? = data?.get(holder.adapterPosition)
        holder.binding.setVariable(BR.item, item)
        holder.binding.setVariable(BR.itemListener, listener)
        holder.binding.executePendingBindings()
    }

    class BaseViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        fun clearAnimation() {
            binding.root.clearAnimation()
        }
    }

    override fun onViewAttachedToWindow(holder: BaseViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.clearAnimation()
    }

    interface ListItemListener
}