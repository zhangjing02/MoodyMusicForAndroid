package com.example.moodymusicforandroid.ui.home.model

import com.example.moodymusicforandroid.ui.home.adapter.DiscoverItemType

/**
 * Sealed class representing different types of items in discover list
 */
sealed class DiscoverItem(val itemType: Int) {

    /**
     * Search bar
     */
    data class SearchItem(
        val hint: String
    ) : DiscoverItem(DiscoverItemType.SEARCH)

    /**
     * Categories section
     */
    data class CategoriesItem(
        val categories: List<Category>
    ) : DiscoverItem(DiscoverItemType.CATEGORIES) {
        data class Category(
            val name: String,
            val isSelected: Boolean
        )
    }

    /**
     * Artist section header
     */
    data class ArtistHeaderItem(
        val title: String
    ) : DiscoverItem(DiscoverItemType.ARTIST_HEADER)

    /**
     * Artist item
     */
    data class ArtistItem(
        val name: String,
        val description: String,
        val imageUrl: String?,
        val avatarLetter: String? = null
    ) : DiscoverItem(DiscoverItemType.ARTIST_ITEM)
}
