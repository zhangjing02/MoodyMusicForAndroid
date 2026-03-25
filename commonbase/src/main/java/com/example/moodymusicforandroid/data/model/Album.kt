package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 专辑信息
 */
data class Album(
    @SerializedName("id")
    val id: Long,

    @SerializedName("artist_id")
    val artistId: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("release_date")
    val releaseDate: String? = null,

    @SerializedName("genre")
    val genre: String? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    @SerializedName("storage_id")
    val storageId: String? = null,

    // 扩展字段
    var artistName: String? = null,
    var songs: List<Song>? = null
)
