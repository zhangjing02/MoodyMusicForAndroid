package com.example.moodymusicforandroid.data.model

/**
 * 搜索结果
 */
data class SearchResult(
    val artists: List<Artist>? = null,
    val albums: List<Album>? = null,
    val songs: List<Song>? = null
)
