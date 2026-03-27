package com.example.moodymusicforandroid.ui.home.model

import com.example.moodymusicforandroid.ui.home.adapter.LibraryItemType

/**
 * Sealed class representing different types of items in library list
 */
sealed class LibraryItem(val itemType: Int) {

    /**
     * Albums section header
     */
    data class AlbumsHeaderItem(
        val title: String,
        val seeAllText: String
    ) : LibraryItem(LibraryItemType.ALBUMS_HEADER)

    /**
     * Featured album (large)
     */
    data class AlbumFeaturedItem(
        val title: String,
        val artist: String,
        val imageUrl: String?
    ) : LibraryItem(LibraryItemType.ALBUM_FEATURED)

    /**
     * Small album
     */
    data class AlbumSmallItem(
        val title: String,
        val artist: String,
        val imageUrl: String?
    ) : LibraryItem(LibraryItemType.ALBUM_SMALL)

    /**
     * Artists section header
     */
    data class ArtistsHeaderItem(
        val title: String
    ) : LibraryItem(LibraryItemType.ARTISTS_HEADER)

    /**
     * Following artist item
     */
    data class ArtistItem(
        val name: String,
        val description: String,
        val imageUrl: String?,
        val isFollowing: Boolean = true
    ) : LibraryItem(LibraryItemType.ARTIST_ITEM)
}
