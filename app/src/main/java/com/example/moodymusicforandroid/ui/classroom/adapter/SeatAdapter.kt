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
    private val columnCount: Int = 8,
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
            val position = bindingAdapterPosition
            if (position == RecyclerView.NO_POSITION) return

            binding.tvSeatNumber.text = String.format("%02d", position + 1)
            binding.tvSeatName.text = if (item.realName.isBlank()) "\u5f85\u8ba4\u9886" else item.realName

            if (position < columnCount) {
                binding.tvColumnIndex.visibility = View.VISIBLE
                binding.tvColumnIndex.text = (position + 1).toString()
            } else {
                binding.tvColumnIndex.visibility = View.GONE
            }

            if (item.isClaimed == 1) {
                binding.viewDeskBody.setBackgroundResource(R.drawable.bg_seat_card_minimal_claimed)
                binding.viewOccupiedOverlay.visibility = View.VISIBLE
                binding.tvSeatName.setTextColor(binding.root.context.getColor(R.color.classroom_wood_shadow))
                binding.tvSeatNumber.setTextColor(binding.root.context.getColor(R.color.classroom_wood_shadow))
            } else {
                binding.viewDeskBody.setBackgroundResource(R.drawable.bg_seat_card_minimal)
                binding.viewOccupiedOverlay.visibility = View.GONE
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
