package com.example.moodymusicforandroid.ui.music.activity

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymusicforandroid.data.model.CommentReply
import com.example.moodymusicforandroid.databinding.ItemAlbumSocialReplyBinding

class AlbumSocialAdapter(
    private val replies: List<CommentReply>,
    private val onReplyClick: ((CommentReply) -> Unit)? = null
) : RecyclerView.Adapter<AlbumSocialAdapter.ViewHolder>() {

    private val likedMap = mutableMapOf<String, Boolean>()
    private val likeCountMap = mutableMapOf<String, Int>()

    class ViewHolder(val binding: ItemAlbumSocialReplyBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlbumSocialReplyBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reply = replies[position]
        holder.binding.tvUserName.text = reply.author.username
        holder.binding.tvContent.text = reply.content
        holder.binding.tvTime.text = reply.createdAt
        
        val isLiked = likedMap[reply.id] ?: false
        val currentLikes = likeCountMap.getOrPut(reply.id) { (0..5).random() }
        
        holder.binding.tvLikeCount.text = currentLikes.toString()
        if (isLiked) {
            holder.binding.ivLikeIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#E91E63"))
            holder.binding.tvLikeCount.setTextColor(Color.parseColor("#E91E63"))
        } else {
            holder.binding.ivLikeIcon.imageTintList = ColorStateList.valueOf(Color.parseColor("#999999"))
            holder.binding.tvLikeCount.setTextColor(Color.parseColor("#999999"))
        }

        holder.binding.btnLike.setOnClickListener {
            val newLikedState = !isLiked
            likedMap[reply.id] = newLikedState
            val newCount = if (newLikedState) currentLikes + 1 else currentLikes
            likeCountMap[reply.id] = newCount
            notifyItemChanged(holder.bindingAdapterPosition)
        }

        holder.binding.root.setOnClickListener {
            onReplyClick?.invoke(reply)
        }
    }

    override fun getItemCount() = replies.size
}
