package com.example.moodymusicforandroid.data.model

import com.example.moodymusicforandroid.common.network.ApiResponse
import com.google.gson.annotations.SerializedName

/**
 * 座位整体数据
 */
data class ClassroomData(
    @SerializedName("code")
    override val code: Int = 200,
    
    @SerializedName("message")
    override val message: String? = "",

    @SerializedName("headerTitle")
    val headerTitle: String? = "",
    
    @SerializedName("mainTitle")
    val mainTitle: String? = "教室座位表",
    
    @SerializedName("subTitle")
    val subTitle: String? = "",
    
    @SerializedName("roster")
    val seats: List<SeatItem> = emptyList()
) : ApiResponse<ClassroomData> {
    override val data: ClassroomData?
        get() = this
}

/**
 * 座位信息
 */
data class SeatItem(
    @SerializedName("id")
    val id: String,
    
    @SerializedName("real_name")
    val name: String = "",
    
    @SerializedName("is_claimed")
    val isClaimed: Int = 0,
    
    @SerializedName("claimed_by")
    val claimedBy: String? = null
)

/**
 * 认领验证响应
 */
data class ClaimVerifyResponse(
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("questions")
    val questions: List<ClaimQuestion>
)

/**
 * 验证问题
 */
data class ClaimQuestion(
    @SerializedName("label")
    val label: String
)

/**
 * 认领完成请求
 */
data class ClaimFinalizeRequest(
    @SerializedName("userId")
    val userId: String,
    
    @SerializedName("verifyAnswer")
    val verifyAnswer: String,
    
    @SerializedName("password")
    val password: String
)
