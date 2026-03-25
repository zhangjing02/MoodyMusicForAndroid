# 网络请求错误处理使用指南

本项目已经实现了完善的网络请求错误处理机制，包括业务错误码统一处理和非业务层错误（网络、解析等）的统一处理。

## 核心组件

### 1. ErrorCode - 业务错误码定义

所有后端返回的业务错误码都在 `ErrorCode.kt` 中定义，包括：
- **通用错误** (1000-1099): 参数错误、签名验证失败等
- **认证授权错误** (1020-1029): Token过期、登录失败、权限不足等
- **用户相关错误** (1100-1199): 用户不存在、用户已存在等
- **音乐资源错误** (1200-1299): 音乐不存在、版权限制等
- **VIP会员错误** (1300-1399): VIP过期、需要VIP权限等
- **文件上传错误** (1400-1499): 文件过大、类型错误等
- **数据操作错误** (1500-1599): 数据不存在、保存失败等
- **业务逻辑错误** (1600-1699): 操作失败、频率限制等
- **服务器错误** (2000-2099): 服务器繁忙、数据库错误等
- **网络错误** (3000-3099): 连接超时、DNS错误等

### 2. BusinessException - 业务异常类

封装业务错误码的异常类，包含：
- `code`: 业务错误码
- `message`: 错误消息（可选，为空时使用默认消息）
- `data`: 错误相关数据（可选）
- `displayMessage`: 显示给用户的错误消息

### 3. BaseViewModel 统一错误处理

BaseViewModel 提供两种错误处理模式：

#### 模式1：基类统一处理（默认）

```kotlin
// 默认模式：基类统一处理所有错误，显示toast
request(isShowLoading = true) {
    val response = apiService.login(username, password)
    response
}
```

- ✅ 业务错误码自动转换为友好提示
- ✅ 网络错误统一处理（超时、连接失败等）
- ✅ JSON解析错误自动捕获
- ✅ Token过期自动处理（可重写 `handleTokenExpired()`）
- ✅ 自动显示错误toast

#### 模式2：转发到前端处理

```kotlin
// 转发模式：业务错误传递到前端，由前端自定义处理
request(isShowLoading = true, forwardBusinessError = true) {
    val response = apiService.getVipMusicList()
    response
}
```

- 业务错误通过 `businessError` LiveData 传递到 Activity/Fragment
- 非业务错误（网络、解析）仍然由基类统一处理
- 前端可以针对特定错误码做特殊处理

### 4. BaseActivity/BaseFragment 自动观察

基类自动观察 `businessError` LiveData，并提供 `handleBusinessError()` 方法供子类重写。

## 使用示例

### 示例1：默认模式 - 基类统一处理

**ViewModel**:
```kotlin
class LoginViewModel : BaseViewModel() {

    fun login(username: String, password: String) {
        // 使用默认模式，基类统一处理所有错误
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.login(username, password)
            if (response.isSuccess()) {
                // 登录成功，保存用户信息
                saveUserInfo(response.data)
            }
            response
        }
    }
}
```

**Activity**:
```kotlin
class LoginActivity : BaseActivity<ActivityLoginBinding, LoginViewModel>() {

    override fun getViewModelClass() = LoginViewModel::class.java
    override fun getLayoutId() = R.layout.activity_login

    override fun initView() {
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()
            viewModel.login(username, password)
        }
    }

    // 基类会自动显示错误toast，无需额外处理
}
```

### 示例2：转发模式 - 前端自定义处理

**ViewModel**:
```kotlin
class MusicViewModel : BaseViewModel() {

    val musicList = MutableLiveData<List<Song>>()

    fun loadVipMusic() {
        // 设置 forwardBusinessError = true，将业务错误转发到前端
        request(isShowLoading = true, forwardBusinessError = true) {
            val response = MoodyApiProvider.apiService.getVipMusicList()
            if (response.isSuccess()) {
                musicList.value = response.data
            }
            response
        }
    }
}
```

**Activity**:
```kotlin
class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    override fun getViewModelClass() = MusicViewModel::class.java
    override fun getLayoutId() = R.layout.activity_music

    override fun initView() {
        binding.btnVipMusic.setOnClickListener {
            viewModel.loadVipMusic()
        }
    }

    // 重写此方法，处理特定的业务错误码
    override fun handleBusinessError(businessError: BusinessError) {
        when (businessError.code) {
            ErrorCode.VIP_REQUIRED -> {
                // 需要VIP权限，弹出VIP引导对话框
                showVIPDialog()
            }
            ErrorCode.VIP_EXPIRED -> {
                // VIP已过期，弹出续费对话框
                showVIPRenewDialog()
            }
            ErrorCode.MUSIC_NOT_AVAILABLE -> {
                // 音乐不可用，显示提示
                showMusicNotAvailableDialog()
            }
            else -> {
                // 其他错误码，显示默认消息
                showToast(businessError.message)
            }
        }
    }

    private fun showVIPDialog() {
        // 显示VIP购买对话框
        AlertDialog.Builder(this)
            .setTitle("开通VIP")
            .setMessage("该功能需要VIP权限，是否立即开通？")
            .setPositiveButton("去开通") { _, _ ->
                // 跳转到VIP页面
                navigateToVIPPage()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
```

### 示例3：混合使用 - 不同接口使用不同模式

**ViewModel**:
```kotlin
class UserViewModel : BaseViewModel() {

    val userInfo = MutableLiveData<User>()

    // 普通接口：使用默认模式
    fun loadUserInfo() {
        request(isShowLoading = true) {
            val response = apiService.getUserInfo()
            if (response.isSuccess()) {
                userInfo.value = response.data
            }
            response
        }
    }

    // 需要特殊处理的接口：使用转发模式
    fun updateAvatar(file: File) {
        request(isShowLoading = true, forwardBusinessError = true) {
            val response = apiService.uploadAvatar(file)
            if (response.isSuccess()) {
                userInfo.value = response.data
            }
            response
        }
    }
}
```

**Activity**:
```kotlin
class ProfileActivity : BaseActivity<ActivityProfileBinding, UserViewModel>() {

    override fun getViewModelClass() = UserViewModel::class.java
    override fun getLayoutId() = R.layout.activity_profile

    override fun initView() {
        // 加载用户信息（默认模式，基类统一处理错误）
        viewModel.loadUserInfo()

        binding.btnUpdateAvatar.setOnClickListener {
            val file = selectAvatarFile()
            // 上传头像（转发模式，前端处理特殊错误码）
            viewModel.updateAvatar(file)
        }
    }

    override fun handleBusinessError(businessError: BusinessError) {
        when (businessError.code) {
            ErrorCode.FILE_TOO_LARGE -> {
                // 文件过大，提示用户
                showToast("头像文件不能超过5MB")
            }
            ErrorCode.FILE_TYPE_ERROR -> {
                // 文件类型错误
                showToast("仅支持JPG、PNG格式的图片")
            }
            else -> {
                showToast(businessError.message)
            }
        }
    }
}
```

### 示例4：Token过期自动处理

**ViewModel**:
```kotlin
class LoginViewModel : BaseViewModel() {

    fun refreshToken() {
        request {
            val response = apiService.refreshToken()
            if (response.isSuccess()) {
                saveToken(response.data)
            }
            response
        }
    }

    // 重写Token过期处理方法
    override fun handleTokenExpired() {
        super.handleTokenExpired()
        // 清空本地登录信息
        clearLoginInfo()
        // 发送EventBus事件，通知所有页面跳转到登录页
        EventBusManager.postUserLogout("登录已过期")
    }
}
```

### 示例5：业务错误的特殊处理

**ViewModel**:
```kotlin
class MusicPlayerViewModel : BaseViewModel() {

    fun playMusic(musicId: String) {
        request(isShowLoading = false, forwardBusinessError = true) {
            val response = apiService.playMusic(musicId)
            if (response.isSuccess()) {
                startPlaying(response.data)
            }
            response
        }
    }
}
```

**Activity**:
```kotlin
class MusicPlayerActivity : BaseActivity<ActivityPlayerBinding, MusicPlayerViewModel>() {

    override fun handleBusinessError(businessError: BusinessError) {
        when (businessError.code) {
            ErrorCode.MUSIC_COPYRIGHTED -> {
                // 版权限制，显示特殊提示
                showCopyrightDialog()
            }
            ErrorCode.VIP_REQUIRED -> {
                // 需要VIP，显示引导对话框
                showVIPGuideDialog()
            }
            ErrorCode.FREQUENCY_LIMIT -> {
                // 访问频率限制，提示用户稍后再试
                showToast("操作过于频繁，请${businessError.data}秒后再试")
            }
            else -> {
                showToast(businessError.message)
            }
        }
    }

    private fun showCopyrightDialog() {
        AlertDialog.Builder(this)
            .setTitle("温馨提示")
            .setMessage("该歌曲受版权限制，暂时无法播放")
            .setPositiveButton("知道了", null)
            .show()
    }
}
```

## 错误处理流程

### 业务错误处理流程

```
API请求返回 → BaseViewModel.handleResponse()
    ↓
判断 response.isSuccess()
    ↓
如果为 false（业务错误）
    ↓
创建 BusinessException
    ↓
调用 handleSpecialBusinessError()
    ↓
判断是否需要特殊处理（如Token过期）
    ↓
如果 forwardBusinessError = true
    → 转发到 businessError LiveData
    → Activity/Fragment.handleBusinessError() 处理
否则
    → 基类统一处理
    → 显示错误toast
```

### 非业务错误处理流程

```
网络请求发生异常 → BaseViewModel.handleError()
    ↓
判断异常类型
    ↓
HTTP异常 (4xx, 5xx)
    → 获取友好提示信息
    → 显示toast
    ↓
网络异常 (超时、连接失败)
    → NetworkException.handleException()
    → 显示"网络连接失败"等提示
    ↓
JSON解析异常
    → 显示"数据解析失败"
    ↓
其他异常
    → 显示异常message或"请求失败"
```

## ErrorCode 工具方法

ErrorCode 提供了多个工具方法方便判断错误类型：

```kotlin
// 判断是否为认证错误
val isAuthError = ErrorCode.isAuthError(code)

// 判断是否为Token错误
val isTokenError = ErrorCode.isTokenError(code)

// 判断是否为VIP权限错误
val isVipError = ErrorCode.isVipError(code)

// 判断是否为网络错误
val isNetworkError = ErrorCode.isNetworkError(code)

// 判断是否需要重新登录
val shouldRelogin = ErrorCode.shouldRelogin(code)

// 判断错误是否可以重试
val canRetry = ErrorCode.canRetry(code)

// 获取错误码的默认提示信息
val message = ErrorCode.getDefaultMessage(code)
```

## BusinessError 工具方法

BusinessError 对象也提供了相同的工具方法：

```kotlin
businessError.isAuthError()
businessError.isTokenError()
businessError.isVipError()
businessError.shouldRelogin()
```

## 添加新的业务错误码

如果需要添加新的业务错误码，在 `ErrorCode.kt` 中添加：

```kotlin
object ErrorCode {
    // ... 已有错误码

    // 自定义错误码（建议按类别分配）
    const val MY_CUSTOM_ERROR = 1700  // 自定义错误

    fun getDefaultMessage(code: Int): String {
        return when (code) {
            // ...
            MY_CUSTOM_ERROR -> "自定义错误提示"
            else -> "未知错误"
        }
    }
}
```

## 最佳实践

### 1. 大多数情况使用默认模式

```kotlin
// ✅ 推荐：大多数接口使用默认模式
request(isShowLoading = true) {
    apiService.getData()
}
```

### 2. 只在需要特殊处理时使用转发模式

```kotlin
// ✅ 推荐：需要特殊处理时使用转发模式
request(forwardBusinessError = true) {
    apiService.vipOperation()
}
```

### 3. 在 handleBusinessError 中使用 when 分支

```kotlin
// ✅ 推荐：使用 when 分支处理不同错误码
override fun handleBusinessError(businessError: BusinessError) {
    when (businessError.code) {
        ErrorCode.VIP_REQUIRED -> showVIPDialog()
        ErrorCode.FILE_TOO_LARGE -> showFileSizeError()
        else -> showToast(businessError.message)
    }
}
```

### 4. 利用 businessError.data 传递额外信息

```kotlin
// API返回数据
{
    "code": 1604,
    "message": "访问频率限制",
    "data": 30  // 需要等待的秒数
}

// 处理时使用
when (businessError.code) {
    ErrorCode.FREQUENCY_LIMIT -> {
        val seconds = businessError.data as? Int ?: 60
        showToast("请${seconds}秒后再试")
    }
}
```

### 5. 不要过度使用转发模式

```kotlin
// ❌ 不推荐：所有接口都使用转发模式
request(forwardBusinessError = true) {  // 没必要
    apiService.getSimpleData()
}

// ✅ 推荐：只有需要特殊处理的才用转发模式
request(forwardBusinessError = true) {  // 有必要
    apiService.vipRequiredOperation()
}
```

## 总结

### 两种模式对比

| 特性 | 默认模式 | 转发模式 |
|------|---------|---------|
| 参数 | `forwardBusinessError = false`（默认） | `forwardBusinessError = true` |
| 业务错误处理 | 基类统一处理，显示toast | 转发到前端，由 `handleBusinessError()` 处理 |
| 非业务错误处理 | 基类统一处理 | 基类统一处理 |
| 代码复杂度 | 低，无需额外代码 | 中，需重写 `handleBusinessError()` |
| 适用场景 | 大多数常规接口 | 需要特殊处理特定错误码的接口 |

### 核心优势

1. ✅ **统一处理** - 业务错误码和非业务错误都有统一处理流程
2. ✅ **灵活选择** - 可根据需要选择是否转发业务错误
3. ✅ **自动分类** - 自动识别业务错误、网络错误、解析错误等
4. ✅ **友好提示** - 所有错误都有默认的友好提示信息
5. ✅ **特殊处理** - 支持Token过期、VIP权限等特殊逻辑
6. ✅ **易于扩展** - 可轻松添加新的错误码和处理逻辑

记住：**默认模式适用于90%的场景，转发模式仅在需要特殊处理时使用**。
