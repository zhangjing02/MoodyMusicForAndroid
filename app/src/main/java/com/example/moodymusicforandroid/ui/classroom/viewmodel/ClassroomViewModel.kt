package com.example.moodymusicforandroid.ui.classroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.FinalizeClaimRequest
import com.example.moodymusicforandroid.data.model.FinalizeClaimResponse
import com.example.moodymusicforandroid.data.model.LoginRequest
import com.example.moodymusicforandroid.data.model.RosterItem
import com.example.moodymusicforandroid.data.model.RosterResponse
import com.example.moodymusicforandroid.data.model.User
import com.example.moodymusicforandroid.data.model.VerifyClaimRequest
import com.example.moodymusicforandroid.data.model.VerifyClaimResponse
import java.security.MessageDigest

class ClassroomViewModel : BaseViewModel() {

    val classroomData = MutableLiveData<RosterResponse>()
    val verifyResult = MutableLiveData<VerifyClaimResponse>()
    val claimResult = MutableLiveData<User>()
    val loginResult = MutableLiveData<User>()

    fun fetchRoster() {
        request {
            val response = MoodyApiProvider.apiService.getRoster()
            if (response.isSuccess()) {
                val fullRoster = response.roster.toMutableList()

                val targetCount = 64
                if (fullRoster.size < targetCount) {
                    for (i in (fullRoster.size + 1)..targetCount) {
                        fullRoster.add(
                            RosterItem(
                                id = -i,
                                realName = "空位",
                                yearCode = "2024",
                                seatCode = i.toString().padStart(2, '0'),
                                isClaimed = 0,
                                status = "available"
                            )
                        )
                    }
                }

                classroomData.postValue(response.copy(roster = fullRoster))
            }
            response
        }
    }

    fun verifyClaim(rosterId: Int, answers: List<String>) {
        request(isShowLoading = true, showErrorToast = false) {
            val response = MoodyApiProvider.apiService.verifyClaim(
                VerifyClaimRequest(rosterId, answers)
            )
            if (response.isSuccess()) {
                verifyResult.postValue(response)
            }
            response
        }
    }

    fun finalizeClaim(claimToken: String, passwordRaw: String, email: String? = null) {
        val passwordHash = hashPassword(passwordRaw)
        request(isShowLoading = true, showErrorToast = false) {
            val response = MoodyApiProvider.apiService.finalizeClaim(
                FinalizeClaimRequest(claimToken, passwordHash, email)
            )
            if (response.isSuccess()) {
                val mappedUser = buildClaimUser(response)
                saveLoginState(mappedUser)
                claimResult.postValue(mappedUser)
            }
            response
        }
    }

    fun login(username: String, passwordRaw: String) {
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.login(
                LoginRequest(username, passwordRaw)
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
        user.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }

        val safeUserName = user.username.ifBlank { "同学" }
        val safeUserId = if (user.userId > 0L) user.userId.toString() else "0"
        PreferencesManager.saveUserInfo(safeUserId, safeUserName)

        EventBusManager.post(EventType.USER_LOGIN, "认证成功", user)
    }

    private fun buildClaimUser(response: FinalizeClaimResponse): User {
        val finalizedUser = response.resolvedUser()
        val safeUserName = finalizedUser?.username?.takeIf { it.isNotBlank() } ?: "同学"

        return User(
            userId = finalizedUser?.id ?: 0L,
            username = safeUserName,
            nickname = safeUserName,
            avatarUrl = finalizedUser?.avatarUrl,
            token = response.resolvedToken(),
            refreshToken = response.resolvedRefreshToken(),
            createdAt = finalizedUser?.createdAt
        )
    }

    private fun hashPassword(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
