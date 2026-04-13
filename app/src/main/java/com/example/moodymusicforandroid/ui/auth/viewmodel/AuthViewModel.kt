package com.example.moodymusicforandroid.ui.auth.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.LoginRequest
import com.example.moodymusicforandroid.data.model.RegisterRequest
import com.example.moodymusicforandroid.data.model.User

/**
 * 认证ViewModel
 * 处理登录、注册、退出逻辑
 */
class AuthViewModel : BaseViewModel() {

    val loginUser = MutableLiveData<User>()
    val registerUser = MutableLiveData<User>()
    val logoutSuccess = MutableLiveData<Boolean>()

    /**
     * 登录
     */
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            showToast("用户名和密码不能为空")
            return
        }

        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.login(LoginRequest(username, password))
            val user = response.data
            if (response.isSuccess() && user != null) {
                // 保存token和用户信息到本地
                user.token?.let { PreferencesManager.saveUserToken(it) }
                PreferencesManager.saveUserInfo(user.userId.toString(), user.username)
                loginUser.postValue(user)
                // 发送登录成功事件
                EventBusManager.post(EventType.USER_LOGIN, "登录成功", user)
                EventBusManager.post(EventType.AUTH_LOGIN_SUCCESS, "登录成功", user)
            }
            response
        }
    }

    /**
     * 注册
     */
    fun register(username: String, password: String, nickname: String? = null) {
        if (username.isBlank() || password.isBlank()) {
            showToast("用户名和密码不能为空")
            return
        }
        if (username.length < 3) {
            showToast("用户名至少3个字符")
            return
        }
        if (password.length < 6) {
            showToast("密码至少6位")
            return
        }

        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.register(
                RegisterRequest(username, password, nickname)
            )
            val user = response.data
            if (response.isSuccess() && user != null) {
                // 注册成功自动登录，保存token
                user.token?.let { PreferencesManager.saveUserToken(it) }
                PreferencesManager.saveUserInfo(user.userId.toString(), user.username)
                registerUser.postValue(user)
                EventBusManager.post(EventType.USER_LOGIN, "注册并登录成功", user)
                EventBusManager.post(EventType.AUTH_REGISTER_SUCCESS, "注册成功", user)
            }
            response
        }
    }

    /**
     * 退出登录
     */
    fun logout() {
        request {
            val response = MoodyApiProvider.apiService.logout()
            if (response.isSuccess()) {
                PreferencesManager.clearUserInfo()
                logoutSuccess.postValue(true)
                EventBusManager.post(EventType.USER_LOGOUT, "已退出登录")
            }
            response
        }
    }
}
