package com.example.moodymusicforandroid.common.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

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

        // TODO: 如果有 Token，可以在这里统一添加
        // val token = PreferencesManager.getUserToken()
        // if (token.isNotEmpty()) {
        //     requestBuilder.header("Authorization", "Bearer $token")
        // }

        chain.proceed(requestBuilder.build())
    }

    /**
     * 重试拦截器
     * 对失败的请求进行自动重试
     */
    private val retryInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        var response = chain.proceed(originalRequest)
        var retryCount = 0
        val maxRetryCount = 3

        // 如果响应失败，进行重试
        while (!response.isSuccessful && retryCount < maxRetryCount) {
            retryCount++
            response.close()
            response = chain.proceed(originalRequest)
        }

        response
    }

    // OkHttp客户端
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)           // 请求头拦截器
        .addInterceptor(retryInterceptor)            // 重试拦截器
        .addInterceptor(loggingInterceptor)          // 日志拦截器
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
