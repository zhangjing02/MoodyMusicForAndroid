package com.example.moodymusicforandroid.data.api

import com.example.moodymusicforandroid.common.network.BaseResponse
import com.example.moodymusicforandroid.data.model.*
import retrofit2.http.*

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

    // ==================== 认证相关 ====================

    /**
     * 用户注册
     * POST /api/auth/register
     */
    @POST("api/user/register")
    suspend fun register(@Body request: RegisterRequest): BaseResponse<User>

    /**
     * 登录
     */
    @POST("api/user/login")
    suspend fun login(@Body request: LoginRequest): BaseResponse<User>

    /**
     * 刷新 Token
     */
    @POST("api/user/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): BaseResponse<User>

    /**
     * 退出登录
     * POST /api/auth/logout
     */
    @POST("api/auth/logout")
    suspend fun logout(): BaseResponse<Any>

    /**
     * 获取用户信息
     * GET /api/auth/profile
     */
    @GET("api/user/me")
    suspend fun getProfile(): BaseResponse<User>

    // ==================== 收藏相关 ====================

    /**
     * 收藏/取消收藏歌曲（toggle模式）
     * POST /api/user/favorites/toggle
     */
    @POST("api/user/favorites/toggle")
    suspend fun toggleFavorite(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<ToggleResult>

    /**
     * 获取收藏列表（分页）
     * GET /api/user/favorites?page=1&limit=20
     */
    @GET("api/user/favorites")
    suspend fun getFavorites(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): BaseResponse<FavoritesData>

    /**
     * 检查歌曲是否已收藏
     * GET /api/user/favorites/check?songId=123
     */
    @GET("api/user/favorites/check")
    suspend fun checkFavorite(@Query("songId") songId: Long): BaseResponse<FavoriteCheckResult>

    // ==================== 关注相关 ====================

    /**
     * 关注/取消关注歌手（toggle模式）
     * POST /api/user/follows/toggle
     */
    @POST("api/user/follows/toggle")
    suspend fun toggleFollow(@Body body: Map<String, @JvmSuppressWildcards Any>): BaseResponse<ToggleFollowResult>

    /**
     * 获取关注歌手列表
     * GET /api/user/follows
     */
    @GET("api/user/follows")
    suspend fun getFollows(): BaseResponse<FollowsData>

    // ==================== 评论相关 ====================

    /**
     * 发表评论
     * POST /api/comments
     */
    @POST("api/comments")
    suspend fun createComment(@Body request: CreateCommentRequest): BaseResponse<Comment>

    /**
     * 获取评论列表（公开）
     * GET /api/comments?targetType=song&targetId=123&page=1&limit=20
     */
    @GET("api/comments")
    suspend fun getComments(
        @Query("targetType") targetType: String,
        @Query("targetId") targetId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): BaseResponse<CommentsData>

    /**
     * 删除评论
     * DELETE /api/comments/:id
     */
    @DELETE("api/comments/{id}")
    suspend fun deleteComment(@Path("id") commentId: Long): BaseResponse<Any>

    // ==================== 教室座位表相关 ====================
    
    /**
     * 获取座位表
     * GET /api/roster
     */
    @GET("api/roster")
    suspend fun getRoster(): RosterResponse

    /**
     * 验证认领答案 (第二步)
     */
    @POST("api/user/claim/verify")
    suspend fun verifyClaim(@Body request: VerifyClaimRequest): VerifyClaimResponse

    /**
     * 完成认领 (第三步)
     */
    @POST("api/user/claim/finalize")
    suspend fun finalizeClaim(@Body request: FinalizeClaimRequest): FinalizeClaimResponse

    // ══════════════════════════════════════════
    // 专辑社交功能
    // 所有接口均需要 Authorization: Bearer {token}
    // ══════════════════════════════════════════

    /**
     * 获取专辑社交内容（主贴 + 全部回复）
     * GET /api/albums/{albumId}/social_content
     */
    @GET("api/albums/{albumId}/social_content")
    suspend fun getAlbumSocialContent(
        @Path("albumId") albumId: String
    ): BaseResponse<AlbumSocialContent>

    /**
     * 在专辑下发主贴（每班级唯一）
     * POST /api/albums/{albumId}/posts
     */
    @POST("api/albums/{albumId}/posts")
    suspend fun postAlbumPost(
        @Path("albumId") albumId: String,
        @Body body: PostContentRequest
    ): BaseResponse<Any>

    /**
     * 在主贴下发评论
     * POST /api/albums/posts/{postId}/comments
     */
    @POST("api/albums/posts/{postId}/comments")
    suspend fun postAlbumComment(
        @Path("postId") postId: String,
        @Body body: PostContentRequest
    ): BaseResponse<Any>
}


/**
 * 艺人数据包装
 */
data class ArtistsData(
    val artists: List<Artist>
)
