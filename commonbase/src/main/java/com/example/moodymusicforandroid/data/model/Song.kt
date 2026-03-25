package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 歌曲信息
 */
data class Song(
    @SerializedName("id")
    val id: Long,

    @SerializedName("album_id")
    val albumId: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("file_path")
    val filePath: String? = null,

    @SerializedName("lrc_path")
    val lrcPath: String? = null,

    @SerializedName("track_index")
    val trackIndex: Int? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("cover_url")
    val coverUrl: String? = null,

    // 扩展字段
    var artistName: String? = null,
    var albumTitle: String? = null,
    var audioUrl: String? = null
)
