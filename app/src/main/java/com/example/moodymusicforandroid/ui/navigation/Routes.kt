package com.example.moodymusicforandroid.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object RouteHome : NavKey

@Serializable
data object RouteDiscover : NavKey

@Serializable
data object RouteLibrary : NavKey

@Serializable
data class RouteArtistDetail(
    val artistId: String = "abigail_chen",
    val artistName: String = "阿比盖尔·陈"
) : NavKey

@Serializable
data class RouteAlbumDetail(
    val albumId: String = "forest_echo",
    val albumTitle: String = "林间碎影"
) : NavKey

@Serializable
data class RouteMusicDetail(
    val songId: String
) : NavKey
