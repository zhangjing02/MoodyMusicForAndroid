package com.example.moodymusicforandroid.data.api

import com.example.moodymusicforandroid.common.network.BaseResponse
import com.example.moodymusicforandroid.data.model.*
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Moody 音乐库 API 接口
 */
interface MoodyApiService {

    /**
     * 获取艺人列表（骨架数据）
     * GET /api/skeleton?group=A
     */
    @GET("api/skeleton")
    suspend fun getArtists(
        @Query("group") group: String? = null
    ): BaseResponse<ArtistsData>

    /**
     * 获取完整歌曲数据
     * GET /api/songs?artist=周杰伦&album=Jay
     */
    @GET("api/songs")
    suspend fun getSongs(
        @Query("artistId") artistId: String? = null,
        @Query("artist") artist: String? = null,
        @Query("album") album: String? = null
    ): BaseResponse<List<Artist>>

    /**
     * 全局搜索
     * GET /api/search?q=周杰伦
     */
    @GET("api/search")
    suspend fun search(
        @Query("q") keyword: String
    ): BaseResponse<SearchResult>

    /**
     * 获取欢迎页背景图
     * GET /api/welcome-images
     */
    @GET("api/welcome-images")
    suspend fun getWelcomeImages(): BaseResponse<List<String>>

    /**
     * 获取系统统计
     * GET /api/admin/stats
     */
    @GET("api/admin/stats")
    suspend fun getSystemStats(): BaseResponse<SystemStats>

    /**
     * 获取媒体文件（音乐、封面等）
     * GET /storage/{path}
     */
    @GET
    suspend fun getMediaFile(@Url url: String): retrofit2.Response<okhttp3.ResponseBody>
}

/**
 * 艺人数据包装
 */
data class ArtistsData(
    val artists: List<Artist>
)
