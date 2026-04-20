package com.example.moodymusicforandroid.ui.music.activity

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moodymusicforandroid.data.model.CommentReply
import com.example.moodymusicforandroid.databinding.ItemAlbumSocialReplyBinding

class AlbumSocialAdapter(private val replies: List<CommentReply>) :
    RecyclerView.Adapter<AlbumSocialAdapter.ViewHolder>() {

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
        
        // TODO: 加载头像 (Glide/Coil)
    }

    override fun getItemCount() = replies.size
}
