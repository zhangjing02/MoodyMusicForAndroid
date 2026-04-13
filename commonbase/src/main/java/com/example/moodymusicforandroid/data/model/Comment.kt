package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 评论
 */
data class Comment(
    @SerializedName("id")
    val id: Long,

    @SerializedName("userId")
    val userId: Long,

    @SerializedName("username")
    val username: String,

    @SerializedName("avatarUrl")
    val avatarUrl: String? = null,

    @SerializedName("targetType")
    val targetType: String,

    @SerializedName("targetId")
    val targetId: String,

    @SerializedName("content")
    val content: String,

    @SerializedName("createdAt")
    val createdAt: String? = null,

    @SerializedName("updatedAt")
    val updatedAt: String? = null
)

/**
 * 评论列表数据
 */
data class CommentsData(
    @SerializedName("comments")
    val comments: List<Comment>,

    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("page")
    val page: Int = 1,

    @SerializedName("limit")
    val limit: Int = 20
)

/**
 * 发表评论请求体
 */
data class CreateCommentRequest(
    @SerializedName("targetType")
    val targetType: String,

    @SerializedName("targetId")
    val targetId: String,

    @SerializedName("content")
    val content: String
)
