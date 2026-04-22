package com.example.moodymusicforandroid.data.model

import com.example.moodymusicforandroid.common.network.ApiResponse
import com.google.gson.annotations.SerializedName

data class RosterItem(
    val id: Int,
    @SerializedName("real_name") val realName: String,
    @SerializedName("year_code") val yearCode: String,
    @SerializedName("seat_code") val seatCode: String,
    @SerializedName("sort_index") val sortIndex: Int? = null,
    @SerializedName("is_claimed") val isClaimed: Int,
    @SerializedName("status") val status: String = "available"
)

data class SecurityQuestion(
    val id: Int,
    val question: String
)

data class RosterResponse(
    @SerializedName("code") override val code: Int,
    @SerializedName("message") override val message: String? = null,
    @SerializedName("roster") val roster: List<RosterItem>,
    @SerializedName("security_questions") val securityQuestions: List<SecurityQuestion>? = null
) : ApiResponse<RosterResponse> {
    override val data: RosterResponse? get() = this
}

data class VerifyClaimRequest(
    @SerializedName("roster_id") val rosterId: Int,
    val answers: List<String>
)

data class VerifyClaimResponse(
    @SerializedName("code") override val code: Int,
    @SerializedName("message") override val message: String? = null,
    @SerializedName("claim_token") val claimToken: String = ""
) : ApiResponse<VerifyClaimResponse> {
    override val data: VerifyClaimResponse? get() = this
}

/**
 * 不带 email 字段的认领请求（email 为 null 时使用此类，避免后端校验 null 格式错误）
 */
data class FinalizeClaimRequest(
    @SerializedName("claim_token") val claimToken: String,
    @SerializedName("password_hash") val passwordHash: String
)

/**
 * 带 email 字段的认领请求（用户主动填写邮箱时使用）
 */
data class FinalizeClaimWithEmailRequest(
    @SerializedName("claim_token") val claimToken: String,
    @SerializedName("password_hash") val passwordHash: String,
    @SerializedName("email") val email: String
)

data class FinalizeClaimUser(
    @SerializedName("id") val id: Long? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class FinalizeClaimData(
    @SerializedName("user") val user: FinalizeClaimUser? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null
)

data class FinalizeClaimResponse(
    @SerializedName("code") override val code: Int,
    @SerializedName("message") override val message: String? = null,
    @SerializedName("user") val user: FinalizeClaimUser? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("data") private val nestedData: FinalizeClaimData? = null
) : ApiResponse<FinalizeClaimData> {
    override val data: FinalizeClaimData?
        get() = nestedData ?: FinalizeClaimData(
            user = user,
            token = token,
            refreshToken = refreshToken
        )

    fun resolvedUser(): FinalizeClaimUser? = data?.user
    fun resolvedToken(): String? = data?.token
    fun resolvedRefreshToken(): String? = data?.refreshToken
}
