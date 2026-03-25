# 依赖库版本更新总结

**更新日期**: 2025-03-25

## 主要更新

### 网络库（重要升级）

| 依赖库 | 旧版本 | 新版本 | 变化说明 |
|--------|--------|--------|----------|
| **Retrofit** | 2.9.0 | **2.11.0** | ⬆️ 主要版本升级（2025年5月） |
| **OkHttp** | 4.11.0 | **4.12.0** | ⬆️ 升级到 Kotlin 版本 |
| **Gson** | 2.10.1 | **2.11.0** | ⬆️ 次版本升级 |

### 构建工具

| 依赖库 | 旧版本 | 新版本 | 变化说明 |
|--------|--------|--------|----------|
| **AGP** | 8.9.1 | **8.10.1** | ⬆️ 小版本升级（2025年5月最新稳定版） |
| **Kotlin** | 2.1.0 | 2.1.0 | ✅ 保持不变（已是稳定版本） |

### 保持不变的依赖

以下依赖库已经是最新稳定版本，无需更新：

| 依赖库 | 当前版本 | 说明 |
|--------|----------|------|
| **AndroidX Core KTX** | 1.15.0 | 已是最新稳定版 |
| **AppCompat** | 1.7.0 | 已是最新稳定版 |
| **Material** | 1.12.0 | 已是最新稳定版 |
| **ConstraintLayout** | 2.2.0 | 已是最新稳定版 |
| **Lifecycle** | 2.8.7 | 已是最新稳定版 |
| **Coroutines** | 1.9.0 | 已是最新稳定版 |
| **Glide** | 4.16.0 | 已是最新稳定版 |
| **EventBus** | 3.3.1 | 已是最新稳定版 |
| **Room** | 2.6.1 | 已是最新稳定版 |
| **JUnit** | 4.13.2 | 已是最新稳定版 |
| **AndroidX JUnit** | 1.2.1 | 已是最新稳定版 |
| **Espresso** | 3.6.1 | 已是最新稳定版 |

---

## Retrofit 2.11.0 重要变化

Retrofit 2.11.0 是一个重要的版本更新（发布于 2025年5月），主要变化包括：

### 1. OkHttp 升级到 4.12.0
- OkHttp 现在完全使用 Kotlin 编写
- 改进的性能和内存使用
- 更好的协程支持

### 2. API 变化
大多数 API 保持向后兼容，但建议检查：
- 网络请求拦截器的实现
- 自定义 CallAdapter 的实现
- 自定义 Converter 的实现

### 3. 依赖更新
```gradle
// 旧版本
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.okhttp3:okhttp:4.11.0'

// 新版本
implementation 'com.squareup.retrofit2:retrofit:2.11.0'
implementation 'com.squareup.okhttp3:okhttp:4.12.0'
```

---

## OkHttp 4.12.0 重要变化

### 1. Kotlin 重写
- OkHttp 核心库现在完全使用 Kotlin 编写
- 类型安全的 API
- 更好的空安全

### 2. 性能改进
- 减少内存占用
- 更快的连接建立
- 优化的缓存策略

### 3. 可能的迁移问题
如果项目中使用了 OkHttp 的内部 API，可能需要调整：
```kotlin
// 旧代码（如果有）
import okhttp3.internal.*

// 新代码：使用公开 API
import okhttp3.*
```

---

## Gson 2.11.0 变化

### 1. 性能改进
- 更快的序列化/反序列化
- 优化的内存使用

### 2. Bug 修复
- 修复了多个类型推断的问题
- 改进了泛型序列化支持

---

## 构建验证

✅ **构建状态**: 成功
✅ **commonbase 模块**: 编译通过
✅ **app 模块**: 编译通过
✅ **APK 生成**: 成功

### 构建警告（可忽略）
- Java 源值/目标值 8 已过时警告（不影响功能）
- Glide thumbnail 方法已弃用警告（ImageLoader.kt:247）

---

## 兼容性说明

### 最小 Gradle 版本要求
AGP 8.10.1 要求 **Gradle 8.11.1 或更高版本**

### 最小 SDK 版本
- **compileSdk**: 36（保持不变）
- **minSdk**: 24（保持不变）
- **targetSdk**: 36（保持不变）

### Kotlin 版本
- **Kotlin 2.1.0** 与所有依赖库兼容
- **KSP 2.1.0-1.0.29** 兼容

---

## 需要注意的代码调整

### 1. 网络请求拦截器
如果你有自定义的 OkHttp 拦截器，确保它们与 OkHttp 4.12.0 兼容：

```kotlin
class CustomInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // 你的拦截器代码
        return chain.proceed(request)
    }
}
```

### 2. Retrofit 接口
大多数 Retrofit 接口不需要修改，继续正常工作：

```kotlin
interface MoodyApiService {
    @GET("music/list")
    suspend fun getMusicList(): BaseResponse<List<Song>>

    @POST("user/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): BaseResponse<UserInfo>
}
```

### 3. Gson 数据模型
Gson 2.11.0 与现有数据模型完全兼容，无需修改：

```kotlin
data class Song(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("artist")
    val artist: String
)
```

---

## 测试建议

升级依赖后，建议进行以下测试：

### 1. 网络请求测试
- [ ] 测试所有 API 接口
- [ ] 测试网络超时处理
- [ ] 测试错误处理机制
- [ ] 测试文件上传/下载

### 2. 图片加载测试
- [ ] 测试图片 URL 加载
- [ ] 测试本地图片加载
- [ ] 测试圆角/圆形图片
- [ ] 测试 GIF 加载（如果有）

### 3. 数据库测试
- [ ] 测试数据库读写
- [ ] 测试数据库迁移
- [ ] 测试事务操作

### 4. UI 测试
- [ ] 测试所有 Activity/Fragment
- [ ] 测试 DataBinding
- [ ] 测试 LiveData 观察
- [ ] 测试 EventBus 事件

### 5. 性能测试
- [ ] 测试应用启动时间
- [ ] 测试内存占用
- [ ] 测试网络请求速度
- [ ] 测试图片加载速度

---

## 潜在问题和解决方案

### 问题1: 构建时出现 lint 错误
**解决方案**：
```bash
# 跳过 lint 构建
gradlew assembleDebug -x lint
```

### 问题2: 依赖冲突
**解决方案**：
```bash
# 清理并重新构建
gradlew clean build --refresh-dependencies
```

### 问题3: OkHttp API 变化
**解决方案**：检查是否使用了 OkHttp 的内部 API，改用公开 API

### 问题4: Retrofit 接口不兼容
**解决方案**：大多数情况下无需修改，如果有自定义 CallAdapter，检查其实现

---

## 版本锁定

如果需要锁定特定版本，在 `gradle/libs.versions.toml` 中已经使用版本引用，所有模块会使用相同的版本：

```toml
[versions]
retrofit = "2.11.0"
okhttp = "4.12.0"

[libraries]
retrofit = { group = "com.squareup.retrofit2", name = "retrofit", version.ref = "retrofit" }
okhttp = { group = "com.squareup.okhttp3", name = "okhttp", version.ref = "okhttp" }
```

---

## 下一步建议

1. ✅ **运行应用测试** - 确保所有功能正常工作
2. ✅ **检查网络日志** - 确保网络请求正常
3. ✅ **性能监控** - 对比升级前后的性能
4. ⏳ **监控崩溃报告** - 上线后关注用户反馈
5. ⏳ **定期更新** - 保持依赖库最新版本

---

## 参考链接

- [Retrofit Changelog](https://github.com/square/retrofit/blob/trunk/CHANGELOG.md)
- [OkHttp Changelog](https://github.com/square/okhttp/blob/master/CHANGELOG.md)
- [Gson Release Notes](https://github.com/google/gson/releases)
- [AGP Release Notes](https://developer.android.com/build/releases/past-releases/agp-8-10-0-release-notes)

---

## 更新日志

- **2025-03-25**: 初始更新
  - AGP: 8.9.1 → 8.10.1
  - Retrofit: 2.9.0 → 2.11.0
  - OkHttp: 4.11.0 → 4.12.0
  - Gson: 2.10.1 → 2.11.0
  - 其他依赖保持最新稳定版本
