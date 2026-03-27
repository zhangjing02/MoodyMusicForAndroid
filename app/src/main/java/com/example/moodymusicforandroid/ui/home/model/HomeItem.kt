package com.example.moodymusicforandroid.ui.home.model

import com.example.moodymusicforandroid.ui.home.adapter.HomeItemType

/**
 * Sealed class representing different types of items in home list
 */
sealed class HomeItem(val itemType: Int) {

    /**
     * Hero section with title and subtitle
     */
    data class HeroItem(
        val title: String,
        val subtitle: String
    ) : HomeItem(HomeItemType.HERO)

    /**
     * Featured section header
     */
    data class FeaturedHeaderItem(
        val title: String,
        val seeAllText: String
    ) : HomeItem(HomeItemType.FEATURED_HEADER)

    /**
     * Main featured card (large)
     */
    data class FeaturedMainItem(
        val tag: String,
        val title: String,
        val description: String,
        val imageUrl: String?
    ) : HomeItem(HomeItemType.FEATURED_MAIN)

    /**
     * Small featured card
     */
    data class FeaturedSmallItem(
        val title: String,
        val description: String,
        val imageUrl: String?,
        val iconResId: Int? = null,
        val tag: String? = null
    ) : HomeItem(HomeItemType.FEATURED_SMALL)

    /**
     * Articles section header
     */
    data class ArticlesHeaderItem(
        val title: String
    ) : HomeItem(HomeItemType.ARTICLES_HEADER)

    /**
     * Article card
     */
    data class ArticleItem(
        val tag: String,
        val title: String,
        val description: String,
        val imageUrl: String?
    ) : HomeItem(HomeItemType.ARTICLE_CARD)
}
