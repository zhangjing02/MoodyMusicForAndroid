package com.example.moodymusicforandroid.ui.music.viewmodel

import androidx.lifecycle.MutableLiveData
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.AlbumSocialContent
import com.example.moodymusicforandroid.data.model.PostContentRequest
import com.example.moodymusicforandroid.common.preferences.PreferencesManager

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

    /**
     * 获取专辑社交内容
     * 对应 GET /api/albums/{albumId}/social_content
     */
    fun fetchSocialContent(albumId: String) {
        val token = PreferencesManager.getUserToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        request(isShowLoading = false) {
            val response = MoodyApiProvider.apiService.getAlbumSocialContent(albumId, bearerToken)
            if (response.isSuccess()) {
                socialContent.value = response.data
            }
            response
        }
    }

    /**
     * 发表主贴（每个班级在每个专辑下唯一）
     * 对应 POST /api/albums/{albumId}/posts
     */
    fun postMainPost(albumId: String, content: String) {
        val token = PreferencesManager.getUserToken() ?: return
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.postAlbumPost(albumId, bearerToken, PostContentRequest(content))
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
        val bearerToken = if (token.startsWith("Bearer ")) token else "Bearer $token"
        
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.postAlbumComment(postId, bearerToken, PostContentRequest(content))
            if (response.isSuccess()) {
                actionStatus.value = true
                fetchSocialContent(albumId) // 成功后刷新
            }
            response
        }
    }
}
