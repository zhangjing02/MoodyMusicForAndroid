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
            val context = binding.root.context

            val displaySeatCode = item.seatCode.trim().uppercase()
            binding.tvSeatNumber.text = if (displaySeatCode.isNotBlank()) displaySeatCode else String.format("%02d", position + 1)
            binding.tvSeatName.text = if (item.realName.isBlank()) "\u5f85\u8ba4\u9886" else item.realName

            binding.tvColumnIndex.visibility = View.GONE

            // Seat card tone stays stable; seat-code text color indicates status.
            binding.viewDeskBody.setBackgroundResource(R.drawable.bg_seat_card_minimal)
            binding.viewOccupiedOverlay.visibility = View.GONE
            binding.tvSeatName.setTextColor(context.getColor(R.color.classroom_wood_shadow))
            binding.tvSeatNumber.setTextColor(context.getColor(R.color.classroom_status_unclaimed))

            val statusText = item.status.trim().lowercase()
            val statusColor = if (item.isClaimed != 1) {
                context.getColor(R.color.classroom_status_unclaimed)
            } else if (statusText.contains("online") && !statusText.contains("offline")) {
                context.getColor(R.color.classroom_status_claimed_online)
            } else {
                context.getColor(R.color.classroom_status_claimed_offline)
            }
            binding.tvSeatNumber.setTextColor(statusColor)

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
