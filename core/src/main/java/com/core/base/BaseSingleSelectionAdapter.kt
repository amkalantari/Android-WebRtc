package com.core.base

import android.os.Handler
import android.os.Looper
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class BaseSingleSelectionAdapter<T> : RecyclerView.Adapter<SelectableHolder<T>>() {

    private var mCheckedIndex: Int = -1

    private var arrayItems  : MutableList<T> = mutableListOf()

    private var showItems   : MutableList<T> = mutableListOf()

    fun check(position: Int) {
        if (position == mCheckedIndex)
            return
        val previousIndex = mCheckedIndex
        mCheckedIndex = position
        notifyItemChanged(position)
        notifyItemChanged(previousIndex)
    }

    open fun submitList(list: Collection<T>?, defaultIndex : Int = -1) {
        mCheckedIndex = defaultIndex
        this.showItems.clear()
        this.arrayItems.clear()
        this.notifyDataSetChanged()
        for ((index, item) in (list?.withIndex() ?: listOf())){
            this.showItems.add(item)
            this.arrayItems.add(item)
            notifyItemInserted(index)
        }
    }

    open fun submitList(list: Collection<T>?, default: (T) -> Boolean) {
        this.showItems.clear()
        for ((index, item) in (list ?: listOf()).withIndex()){
            if (default.invoke(item)){
                mCheckedIndex = index
            }
            this.showItems.add(item)
        }
        this.notifyDataSetChanged()
        this.arrayItems.clear()
        this.arrayItems.addAll(list ?: listOf())
    }

    open fun getSelectedItem(): T? {
        return if (mCheckedIndex > 0) { showItems[mCheckedIndex] } else { null }
    }

    open fun clearSelection() {
        val checked = mCheckedIndex
        mCheckedIndex = -1
        notifyItemChanged(checked)
    }

    open fun filter(condition : (T) -> Boolean) {
        showItems.clear()
        showItems.addAll(arrayItems.filter { condition(it) })
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SelectableHolder<T>, position: Int) {
        holder.bind(showItems[position], position == mCheckedIndex,position)
        holder.itemView.setOnClickListener {
            check(position)
            Handler(Looper.getMainLooper()).postDelayed({
                holder.onItemSelected(showItems[position])
            }, 250)
        }
    }

    override fun getItemCount(): Int = showItems.size
}