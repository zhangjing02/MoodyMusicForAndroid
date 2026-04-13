package com.example.moodymusicforandroid.data.model

import com.google.gson.annotations.SerializedName

/**
 * 登录请求体
 */
data class LoginRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

/**
 * 注册请求体
 */
data class RegisterRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String,

    @SerializedName("nickname")
    val nickname: String? = null
)

/**
 * 刷新 Token 请求体
 */
data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)
