package com.example.moodymusicforandroid.ui.classroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.*
import java.security.MessageDigest

class ClassroomViewModel : BaseViewModel() {

    val classroomData = MutableLiveData<RosterResponse>()
    val verifyResult = MutableLiveData<VerifyClaimResponse>()
    val claimResult = MutableLiveData<User>()
    val loginResult = MutableLiveData<User>()

    /**
     * 获取座位表
     */
    fun fetchRoster() {
        request {
            val response = MoodyApiProvider.apiService.getRoster()
            if (response.isSuccess()) {
                val fullRoster = response.roster.toMutableList()
                
                // Ensure at least 64 seats (8 columns * 8 rows) for a standard large classroom
                val targetCount = 64
                if (fullRoster.size < targetCount) {
                    for (i in (fullRoster.size + 1)..targetCount) {
                        fullRoster.add(
                                RosterItem(
                                    id = -i, // Use negative IDs for placeholders
                                    realName = "空位",
                                    yearCode = "2024", // Default placeholder year
                                    seatCode = i.toString().padStart(2, '0'),
                                    isClaimed = 0,
                                    status = "available"
                                )
                        )
                    }
                }
                
                val modifiedResponse = response.copy(roster = fullRoster)
                classroomData.postValue(modifiedResponse)
            }
            response
        }
    }

    /**
     * 验证认领（新三步 - 第一步在获取座位时自带问题，这里是提交答案）
     */
    fun verifyClaim(rosterId: Int, answers: List<String>) {
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.verifyClaim(VerifyClaimRequest(rosterId, answers))
            if (response.isSuccess()) {
                verifyResult.postValue(response.data)
            }
            response
        }
    }

    /**
     * 完成认领（新三步 - 第三步提交 Token 和哈希密码）
     */
    fun finalizeClaim(claimToken: String, passwordRaw: String, email: String? = null) {
        val passwordHash = hashPassword(passwordRaw)
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.finalizeClaim(
                FinalizeClaimRequest(claimToken, passwordHash, email)
            )
            val user = response.data
            if (response.isSuccess() && user != null) {
                saveLoginState(user)
                claimResult.postValue(user)
            }
            response
        }
    }

    /**
     * 座位已认领时的登录
     */
    fun login(username: String, passwordRaw: String) {
        val passwordHash = hashPassword(passwordRaw)
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.login(
                LoginRequest(username, passwordHash)
            )
            val user = response.data
            if (response.isSuccess() && user != null) {
                saveLoginState(user)
                loginResult.postValue(user)
            }
            response
        }
    }

    private fun saveLoginState(user: User) {
        user.token?.let { PreferencesManager.saveUserToken(it) }
        PreferencesManager.saveUserInfo(user.userId.toString(), user.username)
        // 发送通用登录成功事件，触发其他界面更新
        EventBusManager.post(EventType.USER_LOGIN, "认证成功", user)
    }

    /**
     * SHA-256 哈希
     */
    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
