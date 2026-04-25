package com.example.moodymusicforandroid.common.network

import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.data.model.RefreshTokenRequest
import com.example.moodymusicforandroid.data.model.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL_DEV = "http://127.0.0.1:8787/"
    private const val BASE_URL_PROD = "https://m-api.changgepd.top/"
    private const val BASE_URL = BASE_URL_PROD

    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val headerInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .header("User-Agent", "MoodyMusicAndroid/1.0")

        try {
            requestBuilder.header("X-Client-Type", "android")
            requestBuilder.header("X-App-Version", PreferencesManager.getAppVersion())
            requestBuilder.header("X-Device-Id", PreferencesManager.getDeviceId())

            val token = PreferencesManager.getUserToken()
            if (!token.isNullOrEmpty()) {
                requestBuilder.header("Authorization", "Bearer $token")
            }
        } catch (_: Exception) {
            // PreferencesManager may not be initialized yet.
        }

        chain.proceed(requestBuilder.build())
    }

    private val tokenAuthenticator = Authenticator { _: Route?, response: Response ->
        synchronized(this) {
            val refreshToken = PreferencesManager.getUserRefreshToken()
            if (refreshToken.isNullOrEmpty()) return@Authenticator null

            val currentToken = PreferencesManager.getUserToken()
            val currentAuthHeader = if (currentToken.isNullOrEmpty()) null else "Bearer $currentToken"
            if (response.request.header("Authorization") != currentAuthHeader) {
                return@Authenticator response.request.newBuilder().apply {
                    if (!currentToken.isNullOrEmpty()) {
                        header("Authorization", "Bearer $currentToken")
                    }
                }.build()
            }

            try {
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
            } catch (_: Exception) {
                // ignore and fallback to logout
            }

            PreferencesManager.clearUserInfo()
            null
        }
    }

    private val retryInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val canRetry = originalRequest.method.equals("GET", ignoreCase = true) ||
            originalRequest.method.equals("HEAD", ignoreCase = true)

        var response = try {
            chain.proceed(originalRequest)
        } catch (_: Exception) {
            null
        }

        var retryCount = 0
        val maxRetryCount = 2

        while (canRetry && (response == null || response.code in 500..599) && retryCount < maxRetryCount) {
            retryCount++
            response?.close()
            response = try {
                chain.proceed(originalRequest)
            } catch (_: Exception) {
                null
            }
        }

        response ?: throw java.io.IOException("Network request failed after retries")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(retryInterceptor)
        .addInterceptor(loggingInterceptor)
        .authenticator(tokenAuthenticator)
        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofitInstance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofitInstance.create(service)

    fun getBaseUrl(): String = BASE_URL
}