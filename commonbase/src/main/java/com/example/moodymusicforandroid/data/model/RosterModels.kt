package com.example.moodymusicforandroid.data.model

import com.example.moodymusicforandroid.common.network.ApiResponse
import com.google.gson.annotations.SerializedName

/**
 * 座位项模型
 */
data class RosterItem(
    val id: Int,
    @SerializedName("real_name") val realName: String,
    @SerializedName("year_code") val yearCode: String,
    @SerializedName("seat_code") val seatCode: String,
    @SerializedName("is_claimed") val isClaimed: Int, // 0=可认领, 1=已认领
    val status: String
)

/**
 * 安全问题模型
 */
data class SecurityQuestion(
    val id: Int,
    val question: String
)

/**
 * 获取座位表响应
 */
data class RosterResponse(
    @SerializedName("code") override val code: Int,
    @SerializedName("message") override val message: String? = null,
    @SerializedName("roster") val roster: List<RosterItem>,
    @SerializedName("security_questions") val securityQuestions: List<SecurityQuestion>? = null
) : ApiResponse<RosterResponse> {
    override val data: RosterResponse? get() = this
}

/**
 * 验证认领请求 (第二步)
 */
data class VerifyClaimRequest(
    @SerializedName("roster_id") val rosterId: Int,
    val answers: List<String>
)

/**
 * 验证认领响应
 */
data class VerifyClaimResponse(
    @SerializedName("code") override val code: Int,
    @SerializedName("message") override val message: String? = null,
    @SerializedName("claim_token") val claimToken: String = ""
) : ApiResponse<VerifyClaimResponse> {
    override val data: VerifyClaimResponse? get() = this
}

/**
 * 完成认领请求 (第三步)
 */
data class FinalizeClaimRequest(
    @SerializedName("claim_token") val claimToken: String,
    @SerializedName("password_hash") val passwordHash: String,
    val email: String? = null
)
