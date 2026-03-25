package com.example.moodymusicforandroid.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodymusicforandroid.common.network.ApiResponse
import com.example.moodymusicforandroid.common.network.BusinessError
import com.example.moodymusicforandroid.common.network.BusinessException
import com.example.moodymusicforandroid.common.network.ErrorCode
import com.example.moodymusicforandroid.common.network.HttpException
import com.example.moodymusicforandroid.common.network.NetworkException
import com.google.gson.JsonParseException
import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.net.ssl.SSLException

/**
 * ViewModel基类
 * 提供统一的网络请求封装和状态管理
 * 支持业务错误码统一处理和转发
 */
abstract class BaseViewModel : ViewModel() {

    // 加载状态
    private val _loadingState = MutableLiveData<LoadingState>()
    val loadingState: LiveData<LoadingState> = _loadingState

    // 错误消息
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // Toast消息
    private val _toastMessage = MutableLiveData<String>()
    val toastMessage: LiveData<String> = _toastMessage

    // 业务错误（可选转发到前端）
    private val _businessError = MutableLiveData<BusinessError>()
    val businessError: LiveData<BusinessError> = _businessError

    /**
     * 通用的网络请求方法
     * @param isShowLoading 是否显示加载框
     * @param showErrorToast 是否显示错误提示
     * @param forwardBusinessError 是否将业务错误转发到前端（默认false）
     *                            false：基类统一处理所有业务错误，显示toast
     *                            true：将业务错误通过 businessError LiveData 传递到前端，由前端处理
     * @param requestBlock 请求块
     */
    protected fun <T : ApiResponse<*>> request(
        isShowLoading: Boolean = false,
        showErrorToast: Boolean = true,
        forwardBusinessError: Boolean = false,
        requestBlock: suspend CoroutineScope.() -> T
    ) {
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                handleError(throwable, showErrorToast, forwardBusinessError)
            }
        ) {
            if (isShowLoading) {
                _loadingState.value = LoadingState.ShowLoading
            }

            val response = requestBlock()

            if (isShowLoading) {
                _loadingState.value = LoadingState.HideLoading
            }

            // 处理响应
            handleResponse(response, showErrorToast, forwardBusinessError)
        }
    }

    /**
     * 不使用协程作用域的请求方法（用于已经在协程中的情况）
     * @param isShowLoading 是否显示加载框
     * @param showErrorToast 是否显示错误提示
     * @param forwardBusinessError 是否将业务错误转发到前端
     */
    protected suspend fun <T : ApiResponse<*>> suspendRequest(
        isShowLoading: Boolean = false,
        showErrorToast: Boolean = true,
        forwardBusinessError: Boolean = false,
        requestBlock: suspend () -> T
    ): T {
        if (isShowLoading) {
            _loadingState.value = LoadingState.ShowLoading
        }

        return try {
            val response = requestBlock()
            if (isShowLoading) {
                _loadingState.value = LoadingState.HideLoading
            }
            handleResponse(response, showErrorToast, forwardBusinessError)
            response
        } catch (e: Exception) {
            if (isShowLoading) {
                _loadingState.value = LoadingState.HideLoading
            }
            handleError(e, showErrorToast, forwardBusinessError)
            throw e
        }
    }

    /**
     * 处理API响应
     * @param response API响应对象
     * @param showErrorToast 是否显示错误toast
     * @param forwardBusinessError 是否转发业务错误到前端
     */
    private fun <T : ApiResponse<*>> handleResponse(
        response: T,
        showErrorToast: Boolean,
        forwardBusinessError: Boolean
    ) {
        if (response.isError()) {
            // 业务错误处理
            val businessException = BusinessException(
                code = response.code,
                message = response.message,
                data = response.data
            )

            // 处理特殊业务错误（如需要重新登录）
            handleSpecialBusinessError(businessException)

            if (forwardBusinessError) {
                // 转发到前端处理
                _businessError.value = BusinessError.from(businessException)
            } else {
                // 基类统一处理，显示toast
                val errorMsg = response.getErrorMsg()
                if (showErrorToast && errorMsg.isNotEmpty()) {
                    _toastMessage.value = errorMsg
                }
            }

            _errorMessage.value = businessException.displayMessage
        }
    }

    /**
     * 处理错误
     * @param throwable 异常对象
     * @param showErrorToast 是否显示错误toast
     * @param forwardBusinessError 是否转发业务错误到前端
     */
    private fun handleError(
        throwable: Throwable,
        showErrorToast: Boolean,
        forwardBusinessError: Boolean
    ) {
        _loadingState.value = LoadingState.HideLoading

        when (throwable) {
            // 业务异常（直接抛出的 BusinessException）
            is BusinessException -> {
                handleSpecialBusinessError(throwable)

                if (forwardBusinessError) {
                    _businessError.value = BusinessError.from(throwable)
                } else {
                    if (showErrorToast) {
                        _toastMessage.value = throwable.displayMessage
                    }
                }
                _errorMessage.value = throwable.displayMessage
            }

            // HTTP异常（4xx, 5xx）
            is HttpException -> {
                val errorMsg = throwable.getErrorMessage()
                if (showErrorToast) {
                    _toastMessage.value = errorMsg
                }
                _errorMessage.value = errorMsg
            }

            // 网络异常
            is SocketTimeoutException, is UnknownHostException, is SSLException -> {
                val errorMsg = NetworkException.handleException(throwable)
                if (showErrorToast) {
                    _toastMessage.value = errorMsg
                }
                _errorMessage.value = errorMsg
            }

            // JSON解析异常
            is JsonParseException, is JsonSyntaxException -> {
                val errorMsg = "数据解析失败"
                if (showErrorToast) {
                    _toastMessage.value = errorMsg
                }
                _errorMessage.value = errorMsg
            }

            // 其他异常
            else -> {
                val errorMsg = throwable.message ?: "请求失败，请稍后重试"
                if (showErrorToast) {
                    _toastMessage.value = errorMsg
                }
                _errorMessage.value = errorMsg
            }
        }
    }

    /**
     * 处理特殊业务错误
     * @param exception 业务异常
     */
    private fun handleSpecialBusinessError(exception: BusinessException) {
        when {
            // Token过期或无效，需要重新登录
            exception.shouldRelogin() -> {
                // 发送退出登录事件
                handleTokenExpired()
            }

            // VIP权限错误
            exception.isVipError() -> {
                // 可以在此处理VIP相关逻辑，如跳转到VIP页面
            }
        }
    }

    /**
     * 处理Token过期
     * 子类可以重写此方法来自定义处理逻辑
     */
    protected open fun handleTokenExpired() {
        // 默认处理：清空登录信息，跳转到登录页
        // 可以发送EventBus事件通知所有页面
        _toastMessage.value = "登录已过期，请重新登录"
    }

    /**
     * 显示Toast消息
     */
    protected fun showToast(message: String) {
        _toastMessage.value = message
    }

    /**
     * 显示加载框
     */
    protected fun showLoading() {
        _loadingState.value = LoadingState.ShowLoading
    }

    /**
     * 隐藏加载框
     */
    protected fun hideLoading() {
        _loadingState.value = LoadingState.HideLoading
    }
}

/**
 * 加载状态
 */
sealed class LoadingState {
    object ShowLoading : LoadingState()
    object HideLoading : LoadingState()
}
