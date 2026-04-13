package com.example.moodymusicforandroid.ui.classroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.data.model.RosterItem
import com.example.moodymusicforandroid.databinding.ItemSeatBinding

class SeatAdapter(
    private val onSeatClick: (RosterItem) -> Unit
) : ListAdapter<RosterItem, SeatAdapter.SeatViewHolder>(SeatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        val binding = ItemSeatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SeatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SeatViewHolder(private val binding: ItemSeatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RosterItem) {
            val position = adapterPosition
            val seatNumberDisplay = String.format("%02d号", position + 1)
            binding.tvSeatNumber.text = seatNumberDisplay
            
            // 使用实名展示，如果没有则显示座号
            val displayName = if (item.realName.isBlank()) "待认领" else item.realName
            binding.tvSeatName.text = displayName
            
            if (item.isClaimed == 1) {
                binding.viewOccupiedOverlay.visibility = View.VISIBLE
                binding.ivClaimed.visibility = View.VISIBLE
                binding.tvSeatName.setTextColor(binding.root.context.getColor(R.color.white))
                binding.tvSeatNumber.setTextColor(binding.root.context.getColor(R.color.white))
            } else {
                binding.viewOccupiedOverlay.visibility = View.GONE
                binding.ivClaimed.visibility = View.GONE
                binding.tvSeatName.setTextColor(binding.root.context.getColor(R.color.classroom_wood_shadow))
                binding.tvSeatNumber.setTextColor(binding.root.context.getColor(R.color.classroom_wood_shadow))
            }

            binding.root.setOnClickListener {
                onSeatClick(item)
            }
        }
    }

    class SeatDiffCallback : DiffUtil.ItemCallback<RosterItem>() {
        override fun areItemsTheSame(oldItem: RosterItem, newItem: RosterItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RosterItem, newItem: RosterItem): Boolean {
            return oldItem == newItem
        }
    }
}
