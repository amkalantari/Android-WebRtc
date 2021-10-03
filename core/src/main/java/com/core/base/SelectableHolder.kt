package com.core.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView



abstract class SelectableHolder<T>(binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(value : T, isSelected : Boolean,position : Int)

    abstract fun onItemSelected(value: T)

}