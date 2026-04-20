package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────
// 专辑社交评论 — 数据模型
// 对应后端 Supabase album_comments 表
// ─────────────────────────────────────────────

/**
 * 作者信息（由 Worker 聚合自 D1 user_profiles）
 */
data class CommentAuthor(
    @SerializedName("username")  val username: String,
    @SerializedName("avatar_url") val avatarUrl: String?
)

/**
 * 一条回复
 */
data class CommentReply(
    @SerializedName("id")         val id: String,
    @SerializedName("album_id")   val albumId: String,
    @SerializedName("user_id")    val userId: String,
    @SerializedName("class_id")   val classId: String,
    @SerializedName("content")    val content: String,
    @SerializedName("parent_id")  val parentId: String?,
    @SerializedName("root_id")    val rootId: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("author")     val author: CommentAuthor
)

/**
 * 专辑社交内容（主贴 + 全部回复，树形，一次性返回）
 * 对应 GET /api/albums/:id/social_content
 */
data class AlbumSocialContent(
    @SerializedName("id")         val id: String,
    @SerializedName("album_id")   val albumId: String,
    @SerializedName("user_id")    val userId: String,
    @SerializedName("class_id")   val classId: String,
    @SerializedName("content")    val content: String,
    @SerializedName("parent_id")  val parentId: String?,
    @SerializedName("root_id")    val rootId: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("author")     val author: CommentAuthor,
    @SerializedName("replies")    val replies: List<CommentReply>
)

/**
 * 发帖 / 发评论的请求体
 */
data class PostContentRequest(
    @SerializedName("content") val content: String
)
