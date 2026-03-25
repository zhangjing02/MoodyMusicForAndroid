package com.example.moodymusicforandroid.ui.home.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.network.ApiServiceProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 主页面ViewModel
 */
class MainViewModel : BaseViewModel() {

    // 欢迎消息
    val welcomeMessage = MutableLiveData<String>()

    /**
     * 加载欢迎消息
     */
    fun loadWelcomeMessage() {
        viewModelScope.launch {
            // 模拟网络请求
            delay(1000)
            welcomeMessage.value = "Welcome to Moody Music!"
        }
    }

    /**
     * 获取音乐列表（示例）
     */
    fun getMusicByMood(mood: String) {
        request(isShowLoading = true) {
            ApiServiceProvider.apiService.getMusicByMood(mood)
        }
    }
}
