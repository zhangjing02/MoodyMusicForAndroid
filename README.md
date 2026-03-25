# MoodyMusicForAndroid

一个基于情绪的音乐推荐 Android 原生应用，采用 MVVM 架构和 Kotlin 语言开发。

## 项目概述

MoodyMusicForAndroid 是一个现代化的 Android 音乐应用，通过分析用户的情绪状态来推荐合适的音乐。项目采用 MVVM 架构模式，使用 Kotlin + XML DataBinding 开发，并实现了自定义的基础类框架来减少代码冗余。

## 技术栈

### 核心技术
- **语言**: Kotlin 2.1.0
- **UI 框架**: XML Layouts + DataBinding
- **构建系统**: Gradle (Kotlin DSL)
- **架构模式**: MVVM (Model-View-ViewModel)
- **最小 SDK**: 24 (Android 7.0)
- **目标 SDK**: 36 (Android 14)

### 主要依赖库

#### UI 相关
- **Material Design Components** - Google Material Design 组件库
- **ConstraintLayout** - 灵活的布局管理器
- **RWidgetHelper (androidx.v0.0.14)** - UI 控件辅助库（圆角、边框、渐变、状态切换）

#### 架构组件
- **Lifecycle 2.8.7** - Android 生命周期组件
  - ViewModel
  - LiveData
  - Runtime

#### 异步处理
- **Kotlin Coroutines 1.9.0** - Kotlin 协程

#### 网络请求
- **Retrofit 2.11.0** - HTTP 客户端
- **OkHttp 4.12.0** - HTTP 连接池
- **Gson 2.11.0** - JSON 序列化/反序列化
- **Logging Interceptor** - 网络请求日志

#### 图片加载
- **Glide 4.16.0** - 图片加载和缓存库

#### 数据处理
- **RecyclerView 1.3.2** - 列表控件
- **BaseRecyclerViewAdapterHelper 4.1.4** - RecyclerView 适配器辅助库

#### 事件总线
- **EventBus 3.3.1** - 事件发布/订阅框架

#### 数据库
- **Room 2.6.1** - Android 本地数据库

## 项目结构

```
MoodyMusicForAndroid/
├── app/                                    # 应用模块
│   └── src/main/
│       ├── java/com/example/moodymusicforandroid/
│       │   ├── MoodyMusicApplication.kt    # 应用入口
│       │   └── ui/                         # UI 层
│       │       ├── home/                   # 主页面
│       │       │   ├── activity/
│       │       │   │   └── MainActivity.kt
│       │       │   └── viewmodel/
│       │       │       └── MainViewModel.kt
│       │       ├── music/                  # 音乐列表
│       │       │   ├── activity/
│       │       │   │   └── MusicListActivity.kt
│       │       │   └── viewmodel/
│       │       │       └── MusicListViewModel.kt
│       │       └── test/                   # 测试页面
│       │           ├── activity/
│       │           │   └── ApiTestActivity.kt
│       │           └── viewmodel/
│       │               └── ApiTestViewModel.kt
│       └── res/
│           ├── layout/                     # 布局文件
│           ├── values/                     # 资源值
│           └── drawable/                   # 图标和图片资源
│
├── commonbase/                             # 基础库模块
│   └── src/main/java/com/example/moodymusicforandroid/
│       ├── base/                           # 基类
│       │   ├── BaseActivity.kt             # Activity 基类（泛型 VB, VM）
│       │   ├── BaseFragment.kt             # Fragment 基类（泛型 VB, VM）
│       │   └── BaseViewModel.kt            # ViewModel 基类
│       │
│       ├── common/                         # 通用工具
│       │   ├── eventbus/                   # 事件总线
│       │   │   ├── BaseEvent.kt
│       │   │   ├── EventBusManager.kt
│       │   │   └── EventType.kt
│       │   ├── image/                      # 图片加载
│       │   │   ├── ImageLoader.kt
│       │   │   ├── GlideCircleTransform.kt
│       │   │   └── GlideRoundTransform.kt
│       │   ├── network/                    # 网络层
│       │   │   ├── ApiService.kt           # API 服务接口
│       │   │   ├── ApiResponse.kt          # API 响应接口
│       │   │   ├── BaseResponse.kt         # 基础响应包装类
│       │   │   ├── BusinessException.kt    # 业务异常
│       │   │   ├── ErrorCode.kt            # 错误码定义
│       │   │   ├── NetworkException.kt     # 网络异常
│       │   │   └── RetrofitClient.kt       # Retrofit 客户端
│       │   ├── preferences/                # SharedPreferences 封装
│       │   │   └── PreferencesManager.kt
│       │   └── recyclerview/               # RecyclerView 辅助
│       │       └── RecyclerViewHelper.kt
│       │
│       └── data/                           # 数据层
│           ├── api/                        # API 服务
│           │   ├── MoodyApiProvider.kt     # API 提供者（单例）
│           │   └── MoodyApiService.kt      # Moody API 接口定义
│           └── model/                      # 数据模型
│               ├── Album.kt                # 专辑模型
│               ├── Artist.kt               # 艺术家模型
│               └── Song.kt                 # 歌曲模型
│
├── gradle/                                 # Gradle 配置
│   └── libs.versions.toml                  # 版本目录（依赖管理）
├── build.gradle.kts                        # 项目级构建文件
├── settings.gradle.kts                     # 设置文件
├── CLAUDE.md                               # Claude Code 项目指南
└── README.md                               # 项目说明文档
```

## 架构设计

### MVVM 架构

项目采用标准的 MVVM 架构模式：

```
┌─────────────────┐
│     View        │  (Activity/Fragment + DataBinding)
├─────────────────┤
│   ViewModel     │  (业务逻辑、数据处理)
├─────────────────┤
│     Model       │  (数据层、API、数据库)
└─────────────────┘
```

### 泛型基类框架

项目实现了自定义的泛型基类来减少样板代码：

#### BaseViewModel
- 提供统一的网络请求包装方法 `request()`
- 自动管理加载状态（LoadingState）
- 内置错误处理（errorMessage、toastMessage LiveData）
- 使用 viewModelScope 管理协程生命周期

#### BaseActivity<VB : ViewDataBinding, VM : BaseViewModel>
- 泛型类，接受 ViewBinding 和 ViewModel 类型
- 自动初始化 ViewModel
- 自动设置 DataBinding
- 自动观察 ViewModel 的 LiveData
- 提供模板方法：`initView()`, `initData()`, `setupBindingVariables()`

#### BaseFragment<VB : ViewDataBinding, VM : BaseViewModel>
- 与 BaseActivity 相同的泛型模式
- 提供 Fragment 的基类实现

### 网络层架构

```
ApiService (接口定义)
    ↓
MoodyApiProvider (单例提供者)
    ↓
RetrofitClient (Retrofit 客户端)
    ↓
OkHttp + LoggingInterceptor (HTTP 层)
```

所有 API 响应必须实现 `ApiResponse` 接口，以支持统一的错误处理。

## 构建命令

### 编译应用
```bash
# 编译 Debug APK
gradlew assembleDebug

# 编译 Release APK
gradlew assembleRelease

# 清理构建
gradlew clean

# 安装 Debug 版本到连接的设备/模拟器
gradlew installDebug
```

### 运行测试
```bash
# 运行所有单元测试
gradlew testDebugUnitTest

# 运行指定的测试类
gradlew test --tests com.example.moodymusicforandroid.ExampleUnitTest

# 运行仪器测试（需要连接的设备/模拟器）
gradlew connectedAndroidTest
```

## 开发规范

### 创建新功能模块

1. **创建 ViewModel**（继承 BaseViewModel）：

```kotlin
class MusicViewModel : BaseViewModel() {

    val musicList = MutableLiveData<List<Song>>()

    fun loadMusic() {
        request(isShowLoading = true) {
            val response = MoodyApiProvider.apiService.getMusicList()
            if (response.isSuccess()) {
                musicList.value = response.data
            }
            response
        }
    }
}
```

2. **创建 Activity**（继承 BaseActivity）：

```kotlin
class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    override fun getViewModelClass() = MusicViewModel::class.java

    override fun getLayoutId() = R.layout.activity_music

    override fun setupBindingVariables() {
        binding.viewModel = viewModel
    }

    override fun initView() {
        // 初始化 UI 组件
    }

    override fun initData() {
        viewModel.musicList.observe(this) { list ->
            // 更新 UI
        }
        viewModel.loadMusic()
    }
}
```

3. **创建 Layout**（使用 DataBinding）：

```xml
<layout>
    <data>
        <variable
            name="viewModel"
            type="com.example.moodymusicforandroid.ui.music.MusicViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout>
        <!-- UI 组件绑定到 viewModel -->
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

### 网络请求规范

所有网络请求必须使用 `BaseViewModel` 的 `request()` 方法：

```kotlin
protected fun <T : ApiResponse<*>> request(
    isShowLoading: Boolean = false,
    showErrorToast: Boolean = true,
    requestBlock: suspend CoroutineScope.() -> T
)
```

- 自动处理加载状态
- 捕获异常并显示 Toast
- 处理 API 响应
- 使用 viewModelScope 管理生命周期

### 依赖管理

更新依赖请在 `gradle/libs.versions.toml` 中进行：

1. 在 `[versions]` 部分添加版本号
2. 在 `[libraries]` 部分添加库定义
3. 在 `build.gradle.kts` 中引用：`implementation(libs.library-name)`

## UI 控件库

项目集成了 **RWidgetHelper** 库，提供强大的 UI 辅助功能：

### 主要功能
- 圆角背景（四周统一或单独方向设置）
- 边框（实线/虚线，支持多种状态）
- 背景颜色/渐变/Drawable（支持多状态切换）
- 水波纹点击效果
- 阴影效果

### 可用控件
- `RTextView` - 支持图标状态、文字颜色状态
- `REditText`
- `RImageView` - 圆形/圆角图片
- `RCheckBox`、`RRadioButton`
- `RLinearLayout`、`RRelativeLayout`、`RConstraintLayout`
- `RView`

### 使用示例

```xml
<com.ruffian.library.widget.RTextView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="Hello World"
    android:padding="16dp"

    app:corner_radius="20dp"
    app:background_normal="#3F51B5"
    app:background_pressed="#2196F3"
    app:border_width_normal="2dp"
    app:border_color_normal="#FF4081" />
```

## 代码规范

- **命名规范**：遵循 Kotlin 官方命名规范
- **注释**：公共 API 必须添加 KDoc 注释
- **异常处理**：使用 BaseViewModel 的统一异常处理机制
- **日志**：使用 Timber 或统一封装的日志工具
- **代码格式化**：使用 ktlint 进行代码格式化

## 版本历史

### v1.0.0 (当前版本)
- 初始版本发布
- 实现 MVVM 架构
- 集成基础类框架
- 添加网络层和数据层
- 集成 RWidgetHelper UI 库
- 实现图片加载功能（Glide）
- 实现 RecyclerView 辅助功能
- 添加 EventBus 事件总线

## 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

## 联系方式

- 项目主页: https://github.com/zhangjing02/MoodyMusicForAndroid
- 问题反馈: https://github.com/zhangjing02/MoodyMusicForAndroid/issues

## 相关文档

- [CLAUDE.md](CLAUDE.md) - Claude Code 项目指南
- [ARCHITECTURE.md](ARCHITECTURE.md) - 架构设计文档
- [DEPENDENCY_UPDATE_SUMMARY.md](DEPENDENCY_UPDATE_SUMMARY.md) - 依赖更新说明
- [RECYCLERVIEW_SETUP_SUMMARY.md](RECYCLERVIEW_SETUP_SUMMARY.md) - RecyclerView 设置说明
- [JAVA_VERSION_GUIDE.md](JAVA_VERSION_GUIDE.md) - Java 版本指南

---

**注意**: 本项目仍在积极开发中，API 可能会发生变化。欢迎提出建议和反馈！
