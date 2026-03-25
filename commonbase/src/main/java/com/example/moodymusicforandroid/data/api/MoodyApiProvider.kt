package com.example.moodymusicforandroid.data.api

import com.example.moodymusicforandroid.common.network.RetrofitClient

/**
 * Moody API 服务提供者
 */
object MoodyApiProvider {
    val apiService: MoodyApiService by lazy {
        RetrofitClient.create(MoodyApiService::class.java)
    }

    /**
     * 获取完整的媒体文件 URL
     */
    fun getMediaUrl(path: String): String {
        return "${RetrofitClient.getBaseUrl()}storage/$path"
    }
}
