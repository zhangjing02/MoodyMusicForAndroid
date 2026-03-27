package com.example.moodymusicforandroid.ui.home.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.databinding.*
import com.example.moodymusicforandroid.ui.home.model.LibraryItem

/**
 * Multi-type adapter for LibraryFragment
 */
class LibraryAdapter(
    private val onItemClick: (LibraryItem, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<LibraryItem>()

    fun setItems(newItems: List<LibraryItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            LibraryItemType.ALBUMS_HEADER -> AlbumsHeaderViewHolder(
                ItemLibraryAlbumsHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            LibraryItemType.ALBUM_FEATURED -> AlbumFeaturedViewHolder(
                ItemLibraryAlbumFeaturedBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            LibraryItemType.ALBUM_SMALL -> AlbumSmallViewHolder(
                ItemLibraryAlbumSmallBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            LibraryItemType.ARTISTS_HEADER -> ArtistsHeaderViewHolder(
                ItemLibraryArtistsHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            LibraryItemType.ARTIST_ITEM -> ArtistViewHolder(
                ItemLibraryArtistBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is AlbumsHeaderViewHolder -> holder.bind(item as LibraryItem.AlbumsHeaderItem)
            is AlbumFeaturedViewHolder -> holder.bind(item as LibraryItem.AlbumFeaturedItem)
            is AlbumSmallViewHolder -> holder.bind(item as LibraryItem.AlbumSmallItem)
            is ArtistsHeaderViewHolder -> holder.bind(item as LibraryItem.ArtistsHeaderItem)
            is ArtistViewHolder -> holder.bind(item as LibraryItem.ArtistItem)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolders
    inner class AlbumsHeaderViewHolder(private val binding: ItemLibraryAlbumsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem.AlbumsHeaderItem) {
            binding.tvAlbumsTitle.apply {
                text = item.title
                // 章节标题样式：20sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_HeadlineSmall)
            }

            binding.tvSeeAllAlbums.apply {
                text = item.seeAllText
                // 按钮文本样式：12sp, bold, 字间距0.1
                setTextAppearance(R.style.TextAppearance_Moody_LabelMedium)
            }

            binding.tvSeeAllAlbums.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class AlbumFeaturedViewHolder(private val binding: ItemLibraryAlbumFeaturedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem.AlbumFeaturedItem) {
            binding.tvAlbumFeaturedTitle.apply {
                text = item.title
                // 专辑标题样式：18sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_TitleLarge)
            }

            binding.tvAlbumFeaturedArtist.apply {
                text = item.artist
                // 艺术家名称样式：12sp
                setTextAppearance(R.style.TextAppearance_Moody_BodySmall)
            }

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivAlbumFeatured)
            }

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class AlbumSmallViewHolder(private val binding: ItemLibraryAlbumSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem.AlbumSmallItem) {
            binding.tvAlbumSmallTitle.apply {
                text = item.title
                // 专辑标题样式：12sp, bold
                setTextAppearance(R.style.TextAppearance_Moody_TitleSmall)
            }

            binding.tvAlbumSmallArtist.apply {
                text = item.artist
                // 艺术家名称样式：10sp
                setTextAppearance(R.style.TextAppearance_Moody_BodySmall)
            }

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivAlbumSmall)
            }

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class ArtistsHeaderViewHolder(private val binding: ItemLibraryArtistsHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem.ArtistsHeaderItem) {
            binding.tvArtistsTitle.apply {
                text = item.title
                // 章节标题样式：20sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_HeadlineSmall)
            }
        }
    }

    inner class ArtistViewHolder(private val binding: ItemLibraryArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LibraryItem.ArtistItem) {
            binding.tvArtistName.apply {
                text = item.name
                // 艺术家名称样式：16sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_TitleMedium)
            }

            binding.tvArtistInfo.apply {
                text = item.description
                // 描述文本样式：12sp, 优化字间距
                setTextAppearance(R.style.TextAppearance_Moody_BodySmall)
            }

            binding.btnFollowing.apply {
                text = if (item.isFollowing) "正在关注" else "关注"
                // 按钮文本样式：10sp, bold, 字间距0.15
                setTextAppearance(R.style.TextAppearance_Moody_LabelMedium)
                setTextColor(binding.root.context.getColor(R.color.primary))
            }

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivArtistAvatar)
            }

            // Setup following button background with rounded corners and stroke
            val drawable = GradientDrawable().apply {
                cornerRadius = 16f
                setColor(Color.TRANSPARENT)
                setStroke(1, binding.root.context.getColor(R.color.primary))
            }
            binding.btnFollowing.background = drawable

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }

            binding.btnFollowing.setOnClickListener {
                // Handle follow/unfollow click
                val updatedItem = item.copy(isFollowing = !item.isFollowing)
                // Update the item in the list and notify
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    items[position] = updatedItem
                    notifyItemChanged(position)
                }
                onItemClick(updatedItem, it)
            }
        }
    }
}
