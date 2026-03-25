package com.example.moodymusicforandroid.common.network

import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 网络异常处理工具类
 * 将各种异常转换为用户友好的提示信息
 */
object NetworkException {

    /**
     * 处理异常并返回用户友好的错误信息
     */
    fun handleException(throwable: Throwable): String {
        return when (throwable) {
            is SocketTimeoutException -> {
                "连接超时，请检查网络后重试"
            }
            is UnknownHostException -> {
                "网络连接失败，请检查网络设置"
            }
            is IOException -> {
                "网络异常，请稍后重试"
            }
            else -> {
                throwable.message ?: "请求失败，请稍后重试"
            }
        }
    }

    /**
     * 判断异常是否为网络异常
     */
    fun isNetworkException(throwable: Throwable): Boolean {
        return throwable is SocketTimeoutException ||
                throwable is UnknownHostException ||
                throwable is IOException
    }

    /**
     * 判断异常是否可以重试
     */
    fun canRetry(throwable: Throwable): Boolean {
        return when (throwable) {
            is SocketTimeoutException, is UnknownHostException, is IOException -> true
            else -> false
        }
    }
}

/**
 * 网络响应码异常
 */
class HttpException(val code: Int, message: String?) : IOException(message) {

    companion object {
        const val BAD_REQUEST = 400
        const val UNAUTHORIZED = 401
        const val FORBIDDEN = 403
        const val NOT_FOUND = 404
        const val REQUEST_TIMEOUT = 408
        const val INTERNAL_SERVER_ERROR = 500
        const val BAD_GATEWAY = 502
        const val SERVICE_UNAVAILABLE = 503
        const val GATEWAY_TIMEOUT = 504
    }

    /**
     * 获取用户友好的错误信息
     */
    fun getErrorMessage(): String {
        return when (code) {
            BAD_REQUEST -> "请求参数错误"
            UNAUTHORIZED -> "未授权，请先登录"
            FORBIDDEN -> "无权访问"
            NOT_FOUND -> "请求的资源不存在"
            REQUEST_TIMEOUT -> "请求超时"
            INTERNAL_SERVER_ERROR -> "服务器内部错误"
            BAD_GATEWAY -> "网关错误"
            SERVICE_UNAVAILABLE -> "服务暂时不可用"
            GATEWAY_TIMEOUT -> "网关超时"
            else -> message ?: "请求失败"
        }
    }
}
