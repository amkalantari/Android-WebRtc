package com.core.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


abstract class BaseHolder<T>(binding : ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(value : T, position : Int)

}