package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 艺人信息
 */
data class Artist(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("group")
    val group: String? = null,

    @SerializedName("category")
    val category: String? = null,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("albumCount")
    val albumCount: Int = 0
)
