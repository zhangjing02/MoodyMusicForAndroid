package com.example.moodymusicforandroid.ui.music.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.network.ApiServiceProvider
import com.example.moodymusicforandroid.common.network.MusicItem

/**
 * 音乐列表ViewModel
 */
class MusicListViewModel : BaseViewModel() {

    // 音乐列表
    val musicList = MutableLiveData<List<MusicItem>>()

    /**
     * 根据心情获取音乐列表
     */
    fun getMusicByMood(mood: String) {
        request(isShowLoading = true) {
            val response = ApiServiceProvider.apiService.getMusicByMood(mood)
            if (response.isSuccess()) {
                musicList.value = response.data ?: emptyList()
            }
            response
        }
    }

    /**
     * 获取推荐音乐
     */
    fun getRecommendMusic() {
        request(isShowLoading = true) {
            val response = ApiServiceProvider.apiService.getRecommendMusic()
            if (response.isSuccess()) {
                musicList.value = response.data ?: emptyList()
            }
            response
        }
    }
}
