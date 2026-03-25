package com.example.moodymusicforandroid.common.network

/**
 * API响应基类
 * 所有API响应都应该继承此类
 */
interface ApiResponse<T> {
    /**
     * 响应码
     */
    val code: Int

    /**
     * 响应消息
     */
    val message: String?

    /**
     * 响应数据
     */
    val data: T?

    /**
     * 判断是否成功
     */
    fun isSuccess(): Boolean {
        return code == 0 || code == 200
    }

    /**
     * 判断是否失败
     */
    fun isError(): Boolean {
        return !isSuccess()
    }

    /**
     * 获取错误信息
     */
    fun getErrorMsg(): String {
        return if (message.isNullOrEmpty()) {
            "请求失败"
        } else {
            message!!
        }
    }
}
