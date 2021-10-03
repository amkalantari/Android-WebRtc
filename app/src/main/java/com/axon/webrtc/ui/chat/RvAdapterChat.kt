package com.axon.webrtc.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.axon.webrtc.R
import com.axon.webrtc.databinding.LayoutItemMemberChatBinding
import com.axon.webrtc.databinding.LayoutItemUserChatBinding

data class MsgDto(
    var msg: String,
    var isMine: Boolean
)

class RvAdapterChat : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var list = mutableListOf<MsgDto>()

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(viewGroup.context)
        when (i) {
            0 -> {
                val binding = DataBindingUtil.inflate<LayoutItemUserChatBinding>(
                    inflater,
                    R.layout.layout_item_user_chat,
                    null,
                    false
                )
                return UserHolder(binding)
            }
            else -> {
                val binding = DataBindingUtil.inflate<LayoutItemMemberChatBinding>(
                    inflater,
                    R.layout.layout_item_member_chat,
                    null,
                    false
                )

                return MemberHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, i: Int) {

        when (holder) {
            is UserHolder -> {
                holder.bind(list[i])
            }
            is MemberHolder -> {
                holder.bind(list[i])
            }
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].isMine) {
            0
        } else
            1
    }

    override fun getItemCount(): Int {
        return if (list.isNullOrEmpty()) 0 else list.size
    }

    fun setData(msg: MsgDto) {
        list.add(msg)
        notifyDataSetChanged()
    }


    class UserHolder(var binding: LayoutItemUserChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(model: MsgDto) {
            binding.text.text = model.msg
        }
    }

    class MemberHolder(var binding: LayoutItemMemberChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: MsgDto) {
            binding.text.text = model.msg
        }

    }

}