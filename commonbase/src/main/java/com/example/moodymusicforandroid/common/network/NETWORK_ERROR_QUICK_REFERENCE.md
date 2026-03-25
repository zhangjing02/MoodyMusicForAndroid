# 网络请求错误处理 - 快速参考

## 新增文件

1. **ErrorCode.kt** - 业务错误码定义（40+个预定义错误码）
2. **BusinessException.kt** - 业务异常类和BusinessError类
3. **README_ERROR_HANDLING.md** - 完整使用指南

## 修改文件

1. **BaseViewModel.kt**
   - 新增 `businessError` LiveData
   - `request()` 方法新增 `forwardBusinessError` 参数
   - 统一处理业务错误码和非业务错误
   - 新增 `handleTokenExpired()` 方法

2. **BaseActivity.kt** / **BaseFragment.kt**
   - 观察 `businessError` LiveData
   - 新增 `handleBusinessError()` 方法供子类重写

## 两种使用模式

### 模式1：默认模式（推荐用于大多数情况）

```kotlin
// ViewModel
fun login(username: String, password: String) {
    request(isShowLoading = true) {
        apiService.login(username, password)
    }
}
```

**特点**：
- ✅ 基类统一处理所有错误
- ✅ 自动显示错误toast
- ✅ 无需额外代码

### 模式2：转发模式（用于需要特殊处理的错误）

```kotlin
// ViewModel
fun loadVipMusic() {
    request(isShowLoading = true, forwardBusinessError = true) {
        apiService.getVipMusicList()
    }
}

// Activity/Fragment
override fun handleBusinessError(businessError: BusinessError) {
    when (businessError.code) {
        ErrorCode.VIP_REQUIRED -> showVIPDialog()
        ErrorCode.VIP_EXPIRED -> showVIPRenewDialog()
        else -> showToast(businessError.message)
    }
}
```

**特点**：
- ✅ 前端可针对特定错误码自定义处理
- ✅ 非业务错误仍由基类统一处理
- ⚠️ 需要重写 `handleBusinessError()` 方法

## 错误处理覆盖

### 业务错误（后端返回的 code ≠ 0）

- ✅ Token过期 → 自动调用 `handleTokenExpired()`
- ✅ VIP权限错误 → 可在 `handleBusinessError()` 中处理
- ✅ 其他业务错误 → 根据模式显示toast或转发

### 非业务错误

- ✅ HTTP错误（4xx, 5xx）→ 自动显示友好提示
- ✅ 网络超时 → 显示"连接超时，请检查网络"
- ✅ 连接失败 → 显示"网络连接失败"
- ✅ DNS解析失败 → 显示"服务器地址错误"
- ✅ JSON解析错误 → 显示"数据解析失败"
- ✅ 其他异常 → 显示异常信息或"请求失败"

## 快速示例

### 示例1：普通接口

```kotlin
// 不需要特殊处理，使用默认模式
viewModel.loadUserInfo()
```

### 示例2：VIP接口

```kotlin
// 需要特殊处理VIP权限错误
viewModel.loadVipContent()  // forwardBusinessError = true

override fun handleBusinessError(error: BusinessError) {
    if (error.code == ErrorCode.VIP_REQUIRED) {
        showVIPDialog()
    }
}
```

### 示例3：上传接口

```kotlin
// 需要特殊处理文件大小错误
viewModel.uploadAvatar(file)  // forwardBusinessError = true

override fun handleBusinessError(error: BusinessError) {
    when (error.code) {
        ErrorCode.FILE_TOO_LARGE -> showToast("文件不能超过5MB")
        ErrorCode.FILE_TYPE_ERROR -> showToast("仅支持JPG、PNG格式")
        else -> showToast(error.message)
    }
}
```

## 常用错误码

| 错误码 | 说明 | 默认提示 |
|-------|------|---------|
| 1020 | 未授权 | 请先登录 |
| 1021 | Token过期 | 登录已过期，请重新登录 |
| 1022 | Token无效 | 登录信息无效，请重新登录 |
| 1023 | 登录失败 | 登录失败，请检查账号密码 |
| 1028 | 权限不足 | 权限不足 |
| 1300 | VIP已过期 | 会员已过期 |
| 1301 | 需要VIP权限 | 该功能需要会员权限 |
| 1400 | 文件过大 | 文件过大 |
| 1401 | 文件类型错误 | 文件类型不支持 |
| 1604 | 访问频率限制 | 操作过于频繁，请稍后再试 |

## 工具方法

```kotlin
// 获取错误码的默认提示
val message = ErrorCode.getDefaultMessage(code)

// 判断错误类型
ErrorCode.isAuthError(code)      // 是否为认证错误
ErrorCode.isTokenError(code)     // 是否为Token错误
ErrorCode.isVipError(code)       // 是否为VIP错误
ErrorCode.shouldRelogin(code)    // 是否需要重新登录
```

## 最佳实践

1. **90%的接口使用默认模式** - 只有需要特殊处理时才使用转发模式
2. **在 handleBusinessError 中使用 when 分支** - 清晰处理不同错误码
3. **利用 businessError.data** - API可以传递额外信息（如等待秒数）
4. **不要过度使用转发模式** - 会增加代码复杂度

## 完整文档

详细使用指南请查看：`commonbase/src/main/java/com/example/moodymusicforandroid/common/network/README_ERROR_HANDLING.md`
