package com.example.moodymusicforandroid.common.network

import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.api.MoodyApiService
import com.example.moodymusicforandroid.data.model.RefreshTokenRequest
import com.example.moodymusicforandroid.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Retrofit客户端
 * 单例模式，统一管理网络请求
 */
object RetrofitClient {

    // TODO: 根据环境选择 API 地址
    private const val BASE_URL_DEV = "http://127.0.0.1:8787/"
    private const val BASE_URL_PROD = "https://m-api.changgepd.top/"

    // 当前使用生产环境
    private const val BASE_URL = BASE_URL_PROD

    // 超时配置（单位：秒）
    private const val CONNECT_TIMEOUT = 15L      // 连接超时
    private const val READ_TIMEOUT = 30L         // 读取超时
    private const val WRITE_TIMEOUT = 30L        // 写入超时

    // 日志拦截器
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * 通用请求头拦截器
     * 添加统一的请求头，如 Content-Type、User-Agent 等
     */
    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        // 添加通用请求头
        requestBuilder
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "MoodyMusicAndroid/1.0")

        // 添加 Token（如果已登录）
        try {
            val token = com.example.moodymusicforandroid.common.preferences.PreferencesManager.getUserToken()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        } catch (e: Exception) {
            // PreferencesManager 未初始化时忽略
        }

        chain.proceed(requestBuilder.build())
    }

    /**
     * Token 自动刷新器
     * 当收到 401 响应时触发
     */
    private val tokenAuthenticator = Authenticator { _, response ->
        synchronized(this) {
            val refreshToken = PreferencesManager.getUserRefreshToken()
            if (refreshToken.isNullOrEmpty()) return@Authenticator null

            // 获取最新的 Token，防止多个并发请求重复触发刷新
            val currentToken = PreferencesManager.getUserToken()
            if (response.request.header("Authorization") != "Bearer $currentToken") {
                // 如果当前请求的 Token 已经不是最新的了，说明已经刷新过了，直接重试
                return@Authenticator response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            // 同步执行刷新请求
            try {
                // 这里使用一个新的 OkHttp 客户端来执行刷新请求，避免拦截器递归
                val refreshClient = OkHttpClient.Builder().build()
                val requestBody = Gson().toJson(RefreshTokenRequest(refreshToken))
                    .toRequestBody("application/json".toMediaTypeOrNull())
                val refreshRequest = Request.Builder()
                    .url("${BASE_URL}api/user/refresh")
                    .post(requestBody)
                    .build()

                val refreshResponse = refreshClient.newCall(refreshRequest).execute()
                if (refreshResponse.isSuccessful) {
                    val bodyString = refreshResponse.body?.string()
                    val type = object : TypeToken<BaseResponse<User>>() {}.type
                    val result: BaseResponse<User> = Gson().fromJson(bodyString, type)
                    val userData = result.data

                    if (userData?.token != null) {
                        PreferencesManager.saveUserToken(userData.token)
                        userData.refreshToken?.let { PreferencesManager.saveUserRefreshToken(it) }

                        return@Authenticator response.request.newBuilder()
                            .header("Authorization", "Bearer ${userData.token}")
                            .build()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            // 刷新失败，清除登录状态
            PreferencesManager.clearUserInfo()
            null
        }
    }

    /**
     * 重试拦截器
     * 对失败的网络连接进行重试（非业务逻辑错误）
     */
    private val retryInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        var response = try {
            chain.proceed(originalRequest)
        } catch (e: Exception) {
            null
        }

        var retryCount = 0
        val maxRetryCount = 2

        // 仅在网络异常（response == null）或 5xx 服务错误时重试
        while ((response == null || (response.code in 500..599)) && retryCount < maxRetryCount) {
            retryCount++
            response?.close()
            response = try {
                chain.proceed(originalRequest)
            } catch (e: Exception) {
                null
            }
        }

        response ?: throw java.io.IOException("Network request failed after retries")
    }

    // OkHttp客户端
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)           // 请求头拦截器
        .addInterceptor(retryInterceptor)            // 重试拦截器
        .addInterceptor(loggingInterceptor)          // 日志拦截器
        .authenticator(tokenAuthenticator)           // 自动刷新拦截器
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)              // 连接失败自动重试
        .build()

    // Retrofit实例
    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 创建API服务
     */
    fun <T> create(service: Class<T>): T {
        return retrofitInstance.create(service)
    }

    /**
     * 获取当前 API 基础地址
     */
    fun getBaseUrl(): String = BASE_URL
}
