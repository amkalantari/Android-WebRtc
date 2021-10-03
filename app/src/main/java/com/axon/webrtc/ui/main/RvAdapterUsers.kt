package com.axon.webrtc.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.axon.webrtc.databinding.LayoutItemUserBinding
import com.core.base.BaseHolder
import me.axon.webrtc.dto.Agent

class RvAdapterUsers(var callListener: ((Agent) -> Unit), var chatListener: ((Agent) -> Unit)) :
    ListAdapter<Agent, BaseHolder<Agent>>(object :
        DiffUtil.ItemCallback<Agent>() {
        override fun areItemsTheSame(
            oldItem: Agent,
            newItem: Agent
        ): Boolean = oldItem.id() == newItem.id()

        override fun areContentsTheSame(
            oldItem: Agent,
            newItem: Agent
        ): Boolean = oldItem.id() == newItem.id()
    }) {

    inner class StepHolder(val binding: LayoutItemUserBinding) :
        BaseHolder<Agent>(binding) {
        override fun bind(value: Agent, position: Int) {
            binding.item = value
            binding.call.setOnClickListener {
                callListener(value)
            }
            binding.chat.setOnClickListener {
                chatListener(value)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepHolder {
        return StepHolder(
            LayoutItemUserBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseHolder<Agent>, position: Int) {
        holder.bind(getItem(position), position)
    }
}