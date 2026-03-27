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
import com.example.moodymusicforandroid.ui.home.model.HomeItem

/**
 * Multi-type adapter for HomeFragment
 */
class HomeAdapter(
    private val onItemClick: (HomeItem, View) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<HomeItem>()

    fun setItems(newItems: List<HomeItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].itemType
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            HomeItemType.HERO -> HeroViewHolder(
                ItemHomeHeroBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeItemType.FEATURED_HEADER -> FeaturedHeaderViewHolder(
                ItemHomeFeaturedHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeItemType.FEATURED_MAIN -> FeaturedMainViewHolder(
                ItemHomeFeaturedMainBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeItemType.FEATURED_SMALL -> FeaturedSmallViewHolder(
                ItemHomeFeaturedSmallBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeItemType.ARTICLES_HEADER -> ArticlesHeaderViewHolder(
                ItemHomeArticlesHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
            HomeItemType.ARTICLE_CARD -> ArticleViewHolder(
                ItemHomeArticleBinding.inflate(
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
            is HeroViewHolder -> holder.bind(item as HomeItem.HeroItem)
            is FeaturedHeaderViewHolder -> holder.bind(item as HomeItem.FeaturedHeaderItem)
            is FeaturedMainViewHolder -> holder.bind(item as HomeItem.FeaturedMainItem)
            is FeaturedSmallViewHolder -> holder.bind(item as HomeItem.FeaturedSmallItem)
            is ArticlesHeaderViewHolder -> holder.bind(item as HomeItem.ArticlesHeaderItem)
            is ArticleViewHolder -> holder.bind(item as HomeItem.ArticleItem)
        }
    }

    override fun getItemCount(): Int = items.size

    // ViewHolders
    inner class HeroViewHolder(private val binding: ItemHomeHeroBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.HeroItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvHeroTitle.text = item.title
            binding.tvHeroSubtitle.text = item.subtitle
        }
    }

    inner class FeaturedHeaderViewHolder(private val binding: ItemHomeFeaturedHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.FeaturedHeaderItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvFeaturedTitle.text = item.title
            binding.tvSeeAllFeatured.text = item.seeAllText

            // Setup accent line with rounded corners
            val drawable = GradientDrawable().apply {
                cornerRadius = 8f
                setColor(binding.root.context.getColor(R.color.tertiary))
            }
            binding.vAccentLine.background = drawable

            binding.tvSeeAllFeatured.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class FeaturedMainViewHolder(private val binding: ItemHomeFeaturedMainBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.FeaturedMainItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvFeaturedTag.text = item.tag
            binding.tvFeaturedCardTitle.text = item.title
            binding.tvFeaturedDesc.text = item.description

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivFeaturedMain)
            }

            // Setup gradient overlay
            val gradientDrawable = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(0x00000000.toInt(), 0x99000000.toInt())
            )
            binding.vGradientOverlay.background = gradientDrawable

            // Setup tag background with rounded corners
            val tagDrawable = GradientDrawable().apply {
                cornerRadius = 16f
                setColor(binding.root.context.getColor(R.color.tertiary))
            }
            binding.tvFeaturedTag.background = tagDrawable

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class FeaturedSmallViewHolder(private val binding: ItemHomeFeaturedSmallBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.FeaturedSmallItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvFeaturedSmallTitle.text = item.title
            binding.tvFeaturedSmallDesc.text = item.description

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivFeaturedSmall)
            }

            // Load icon if available
            if (item.iconResId != null) {
                binding.ivFeaturedSmallPlay.setImageResource(item.iconResId)
            }

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }

    inner class ArticlesHeaderViewHolder(private val binding: ItemHomeArticlesHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.ArticlesHeaderItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvArticlesTitle.text = item.title
        }
    }

    inner class ArticleViewHolder(private val binding: ItemHomeArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: HomeItem.ArticleItem) {
            // 只设置文字，样式已在XML中通过textAppearance定义
            binding.tvArticleTag.text = item.tag
            binding.tvArticleTitle.text = item.title
            binding.tvArticleDesc.text = item.description

            // Load image with Glide
            if (!item.imageUrl.isNullOrEmpty()) {
                Glide.with(binding.root.context)
                    .load(item.imageUrl)
                    .placeholder(R.drawable.ic_home)
                    .into(binding.ivArticle)
            }

            binding.root.setOnClickListener {
                onItemClick(item, it)
            }
        }
    }
}
