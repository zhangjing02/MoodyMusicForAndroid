package com.example.moodymusicforandroid.ui.home.adapter

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.databinding.*
import com.example.moodymusicforandroid.ui.home.model.DiscoverItem

/**
 * Multi-type adapter for DiscoverFragment
 */
class DiscoverAdapter(
    private val onItemClick: (DiscoverItem, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<DiscoverItem>()

    fun setItems(newItems: List<DiscoverItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            DiscoverItemType.SEARCH -> SearchViewHolder(
                ItemDiscoverSearchBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            DiscoverItemType.CATEGORIES -> CategoriesViewHolder(
                ItemDiscoverCategoriesBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            DiscoverItemType.ARTIST_HEADER -> ArtistHeaderViewHolder(
                ItemDiscoverArtistHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            DiscoverItemType.ARTIST_ITEM -> ArtistViewHolder(
                ItemDiscoverArtistBinding.inflate(
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
            is SearchViewHolder -> holder.bind(item as DiscoverItem.SearchItem)
            is CategoriesViewHolder -> holder.bind(item as DiscoverItem.CategoriesItem)
            is ArtistHeaderViewHolder -> holder.bind(item as DiscoverItem.ArtistHeaderItem)
            is ArtistViewHolder -> holder.bind(item as DiscoverItem.ArtistItem)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolders
    inner class SearchViewHolder(private val binding: ItemDiscoverSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiscoverItem.SearchItem) {
            binding.etSearch.apply {
                hint = item.hint
                // 搜索框文本样式：16sp
                setTextAppearance(R.style.TextAppearance_Moody_BodyLarge)
            }
        }
    }

    inner class CategoriesViewHolder(private val binding: ItemDiscoverCategoriesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiscoverItem.CategoriesItem) {
            val container = binding.llCategoriesContainer
            container.removeAllViews()

            item.categories.forEach { category ->
                val context = container.context
                val textView = TextView(context).apply {
                    text = category.name
                    // Category标签样式：14sp, 字间距0.1
                    setTextAppearance(R.style.TextAppearance_Moody_LabelLarge)
                    setPadding(48, 24, 48, 24)

                    // Setup rounded background
                    val drawable = GradientDrawable().apply {
                        cornerRadius = 20f
                        if (category.isSelected) {
                            setColor(context.getColor(R.color.primary))
                        } else {
                            setColor(context.getColor(R.color.surface_container_low))
                        }
                    }
                    background = drawable

                    setTextColor(if (category.isSelected) Color.WHITE else context.getColor(R.color.on_surface))

                    // Add margin between categories
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    layoutParams.marginEnd = 16
                    this.layoutParams = layoutParams

                    setOnClickListener {
                        // Handle category click - notify adapter to refresh
                        onItemClick(item, it)
                    }
                }
                container.addView(textView)
            }
        }
    }

    inner class ArtistHeaderViewHolder(private val binding: ItemDiscoverArtistHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiscoverItem.ArtistHeaderItem) {
            binding.tvArtistTitle.apply {
                text = item.title
                // 大标题样式：24sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_HeadlineSmall)
            }
        }
    }

    inner class ArtistViewHolder(private val binding: ItemDiscoverArtistBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiscoverItem.ArtistItem) {
            binding.tvArtistName.apply {
                text = item.name
                // 艺术家名称样式：18sp, serif, bold
                setTextAppearance(R.style.TextAppearance_Moody_TitleLarge)
            }

            binding.tvArtistDesc.apply {
                text = item.description
                // 描述文本样式：14sp, 优化字间距
                setTextAppearance(R.style.TextAppearance_Moody_BodyMedium)
            }

            binding.btnFollow.apply {
                text = "+ 关注"
                // 按钮文本样式：12sp, bold, 字间距0.1
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

            // Setup follow button background with rounded corners and stroke
            val drawable = GradientDrawable().apply {
                cornerRadius = 16f
                setColor(Color.TRANSPARENT)
                setStroke(1, binding.root.context.getColor(R.color.primary))
            }
            binding.btnFollow.background = drawable

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }

            binding.btnFollow.setOnClickListener {
                // Handle follow click
                onItemClick(item, it)
            }
        }
    }
}
