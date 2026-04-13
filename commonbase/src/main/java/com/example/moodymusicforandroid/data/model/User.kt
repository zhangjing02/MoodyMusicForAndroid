package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户信息
 */
data class User(
    @SerializedName("userId")
    val userId: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("nickname")
    val nickname: String,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("token")
    val token: String? = null,

    @SerializedName("refreshToken")
    val refreshToken: String? = null,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("favoriteCount")
    val favoriteCount: Int = 0,

    @SerializedName("followCount")
    val followCount: Int = 0
)
