package com.example.moodymusicforandroid.common.network

/**
 * 业务异常类
 * 用于封装后端返回的业务错误码信息
 *
 * @param code 业务错误码
 * @param message 错误消息（如果为空，会使用 ErrorCode.getDefaultMessage()）
 * @param data 错误相关数据
 */
data class BusinessException(
    val code: Int,
    override val message: String? = null,
    val data: Any? = null
) : Exception(getDisplayMessage(code, message)) {

    /**
     * 获取显示给用户的错误消息
     */
    val displayMessage: String
        get() = getDisplayMessage(code, message)

    /**
     * 判断是否为认证错误
     */
    fun isAuthError(): Boolean {
        return ErrorCode.isAuthError(code)
    }

    /**
     * 判断是否为Token错误
     */
    fun isTokenError(): Boolean {
        return ErrorCode.isTokenError(code)
    }

    /**
     * 判断是否为VIP权限错误
     */
    fun isVipError(): Boolean {
        return ErrorCode.isVipError(code)
    }

    /**
     * 判断是否为网络错误
     */
    fun isNetworkError(): Boolean {
        return ErrorCode.isNetworkError(code)
    }

    /**
     * 判断是否需要重新登录
     */
    fun shouldRelogin(): Boolean {
        return ErrorCode.shouldRelogin(code)
    }

    /**
     * 判断错误是否可以重试
     */
    fun canRetry(): Boolean {
        return ErrorCode.canRetry(code)
    }

    companion object {
        /**
         * 获取显示消息（优先使用 message，如果为空则使用默认消息）
         */
        private fun getDisplayMessage(code: Int, message: String?): String {
            return if (!message.isNullOrEmpty()) {
                message
            } else {
                ErrorCode.getDefaultMessage(code)
            }
        }

        /**
         * 创建业务异常
         */
        fun create(code: Int, message: String? = null, data: Any? = null): BusinessException {
            return BusinessException(code, message, data)
        }
    }
}

/**
 * 业务错误结果
 * 用于将业务错误传递到调用处
 *
 * @param code 业务错误码
 * @param message 错误消息
 * @param data 错误相关数据
 */
data class BusinessError(
    val code: Int,
    val message: String,
    val data: Any? = null
) {
    /**
     * 是否为认证错误
     */
    fun isAuthError(): Boolean = ErrorCode.isAuthError(code)

    /**
     * 是否为Token错误
     */
    fun isTokenError(): Boolean = ErrorCode.isTokenError(code)

    /**
     * 是否为VIP权限错误
     */
    fun isVipError(): Boolean = ErrorCode.isVipError(code)

    /**
     * 是否需要重新登录
     */
    fun shouldRelogin(): Boolean = ErrorCode.shouldRelogin(code)

    /**
     * 从异常创建
     */
    companion object {
        fun from(exception: BusinessException): BusinessError {
            return BusinessError(
                code = exception.code,
                message = exception.displayMessage,
                data = exception.data
            )
        }
    }
}
