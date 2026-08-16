package com.example.moodymusicforandroid.ui.music.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.AlbumSocialContent
import com.example.moodymusicforandroid.data.model.PostContentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 专辑社交功能 ViewModel
 * 
 * 核心功能：
 * 1. 拉取专辑下的社交内容（主贴 + 全部回复）
 * 2. 提交新主贴（针对班级唯一）
 * 3. 提交回复/评论
 */
class AlbumSocialViewModel : BaseViewModel() {

    // 专辑社交内容（包含主贴和回复）
    val socialContent = MutableLiveData<AlbumSocialContent?>()
    
    // 操作状态（发表成功等）
    val actionStatus = MutableLiveData<Boolean>()

    // 下拉刷新状态
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    /**
     * 获取专辑社交内容
     * 对应 GET /api/albums/{albumId}/social_content
     */
    fun fetchSocialContent(albumId: String) {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val token = PreferencesManager.getUserToken()
                if (token != null) {
                    val response = MoodyApiProvider.apiService.getAlbumSocialContent(albumId)
                    if (response.isSuccess()) {
                        socialContent.value = response.data
                    }
                } else {
                    // 未登录或游客模式，依然提供平滑的刷新反馈
                    delay(400)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 发表主贴（每个班级在每个专辑下唯一）
     * 对应 POST /api/albums/{albumId}/posts
     */
    fun postMainPost(albumId: String, content: String) {
        val token = PreferencesManager.getUserToken() ?: return
        
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.postAlbumPost(albumId, PostContentRequest(content))
            if (response.isSuccess()) {
                actionStatus.value = true
                fetchSocialContent(albumId) // 成功后刷新
            }
            response
        }
    }

    /**
     * 发表回复
     * 对应 POST /api/albums/posts/{postId}/comments
     */
    fun postReply(postId: String, albumId: String, content: String) {
        val token = PreferencesManager.getUserToken() ?: return
        
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.postAlbumComment(postId, PostContentRequest(content))
            if (response.isSuccess()) {
                actionStatus.value = true
                fetchSocialContent(albumId) // 成功后刷新
            }
            response
        }
    }
}
