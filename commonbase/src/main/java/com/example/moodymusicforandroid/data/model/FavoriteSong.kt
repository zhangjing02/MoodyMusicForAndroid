package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 收藏的歌曲（带详细信息）
 */
data class FavoriteSong(
    @SerializedName("songId")
    val songId: Long,

    @SerializedName("favoritedAt")
    val favoritedAt: String? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("filePath")
    val filePath: String? = null,

    @SerializedName("coverUrl")
    val coverUrl: String? = null,

    @SerializedName("duration")
    val duration: Int? = null,

    @SerializedName("albumTitle")
    val albumTitle: String? = null,

    @SerializedName("artistName")
    val artistName: String? = null,

    @SerializedName("artistId")
    val artistId: String? = null
)

/**
 * 收藏列表数据
 */
data class FavoritesData(
    @SerializedName("favorites")
    val favorites: List<FavoriteSong>,

    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("limit")
    val limit: Int = 20
)

/**
 * 收藏切换结果
 */
data class ToggleResult(
    @SerializedName("favorited")
    val favorited: Boolean,

    @SerializedName("songId")
    val songId: Long
)

/**
 * 关注切换结果
 */
data class ToggleFollowResult(
    @SerializedName("followed")
    val followed: Boolean,

    @SerializedName("artistId")
    val artistId: String
)

/**
 * 关注的歌手
 */
data class FollowedArtist(
    @SerializedName("artistId")
    val artistId: String,

    @SerializedName("followedAt")
    val followedAt: String? = null,

    @SerializedName("name")
    val name: String,

    @SerializedName("avatar")
    val avatar: String? = null,

    @SerializedName("region")
    val region: String? = null,

    @SerializedName("albumCount")
    val albumCount: Int = 0
)

/**
 * 关注列表数据
 */
data class FollowsData(
    @SerializedName("follows")
    val follows: List<FollowedArtist>
)

/**
 * 收藏检查结果
 */
data class FavoriteCheckResult(
    @SerializedName("favorited")
    val favorited: Boolean,

    @SerializedName("songId")
    val songId: Long
)
