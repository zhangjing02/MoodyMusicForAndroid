package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 系统统计信息
 */
data class SystemStats(
    @SerializedName("artists")
    val artists: Int,

    @SerializedName("albums")
    val albums: Int,

    @SerializedName("tracks")
    val tracks: Int
)
