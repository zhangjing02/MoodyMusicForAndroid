# MoodyMusicForAndroid 架构说明

## 项目架构

本项目采用 MVVM 架构模式，结合 DataBinding 实现数据与视图的双向绑定。

## 架构层次

```
┌─────────────────────────────────────┐
│          Presentation Layer        │
│   (Activity / Fragment)             │
│   - UI 显示                         │
│   - 用户交互                         │
│   - 使用泛型基类                     │
└──────────────┬──────────────────────┘
               │ 观察 LiveData
               ↓
┌─────────────────────────────────────┐
│          ViewModel Layer           │
│   - 业务逻辑                         │
│   - 数据管理                         │
│   - 继承 BaseViewModel              │
└──────────────┬──────────────────────┘
               │ 网络请求
               ↓
┌─────────────────────────────────────┐
│            Data Layer              │
│   - ApiService                      │
│   - RetrofitClient                  │
│   - 数据模型                         │
└─────────────────────────────────────┘
```

## 基类设计

### 1. BaseViewModel

所有 ViewModel 的基类，提供：

- **统一的网络请求封装**：`request()` 方法
- **状态管理**：loadingState、errorMessage、toastMessage
- **协程支持**：使用 viewModelScope
- **自动错误处理**：统一的异常捕获和提示

```kotlin
abstract class BaseViewModel : ViewModel() {
    // 网络请求
    protected fun <T : ApiResponse<*>> request(
        isShowLoading: Boolean = false,
        showErrorToast: Boolean = true,
        requestBlock: suspend CoroutineScope.() -> T
    )

    // 状态管理
    val loadingState: LiveData<LoadingState>
    val errorMessage: LiveData<String>
    val toastMessage: LiveData<String>
}
```

### 2. BaseActivity<VB : ViewDataBinding, VM : BaseViewModel>

所有 Activity 的基类，支持泛型：

**泛型参数：**
- `VB`：DataBinding 类型
- `VM`：ViewModel 类型

**提供的功能：**
- 自动初始化 ViewModel 和 DataBinding
- 自动观察 ViewModel 的 LiveData
- 统一的 Toast、加载框、错误处理
- 生命周期管理

**使用示例：**

```kotlin
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override fun getViewModelClass() = MainViewModel::class.java

    override fun getLayoutId() = R.layout.activity_main

    override fun initView() {
        // 初始化View
    }

    override fun initData() {
        // 初始化数据
        viewModel.loadData()
    }
}
```

### 3. BaseFragment<VB : ViewDataBinding, VM : BaseViewModel>

所有 Fragment 的基类，与 BaseActivity 类似的泛型设计。

**使用示例：**

```kotlin
class MusicListFragment : BaseFragment<FragmentMusicListBinding, MusicListViewModel>() {

    override fun getViewModelClass() = MusicListViewModel::class.java

    override fun getLayoutId() = R.layout.fragment_music_list

    override fun initView() {
        // 初始化View
    }

    override fun initData() {
        // 初始化数据
    }
}
```

## 代码分包结构

```
com.example.moodymusicforandroid/
├── base/                           # 基类
│   ├── BaseActivity.kt            # Activity基类
│   ├── BaseFragment.kt            # Fragment基类
│   └── BaseViewModel.kt           # ViewModel基类
│
├── common/                         # 通用模块
│   └── network/                   # 网络层
│       ├── RetrofitClient.kt      # Retrofit客户端
│       ├── ApiService.kt          # API接口定义
│       ├── ApiResponse.kt         # API响应接口
│       └── BaseResponse.kt        # 基础响应实体
│
└── ui/                            # UI层
    ├── home/                      # 首页模块
    │   ├── activity/
    │   │   └── MainActivity.kt
    │   └── viewmodel/
    │       └── MainViewModel.kt
    │
    ├── music/                     # 音乐模块
    │   ├── activity/
    │   │   └── MusicListActivity.kt
    │   ├── fragment/
    │   │   └── MusicDetailFragment.kt
    │   └── viewmodel/
    │       ├── MusicListViewModel.kt
    │       └── MusicDetailViewModel.kt
    │
    └── player/                    # 播放器模块
        ├── activity/
        │   └── PlayerActivity.kt
        └── viewmodel/
            └── PlayerViewModel.kt
```

## 网络请求使用

### 1. 定义 API 接口

在 `ApiService.kt` 中定义接口：

```kotlin
interface ApiService {
    @GET("music/byMood")
    suspend fun getMusicByMood(@Query("mood") mood: String): BaseResponse<List<MusicItem>>
}
```

### 2. 在 ViewModel 中调用

```kotlin
class MusicViewModel : BaseViewModel() {

    val musicList = MutableLiveData<List<MusicItem>>()

    fun getMusicByMood(mood: String) {
        request(isShowLoading = true) {
            val response = ApiServiceProvider.apiService.getMusicByMood(mood)
            if (response.isSuccess()) {
                musicList.value = response.data
            }
            response
        }
    }
}
```

### 3. 在 Activity/Fragment 中观察数据

```kotlin
class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    override fun initData() {
        // 观察数据
        viewModel.musicList.observe(this) { list ->
            // 更新UI
        }
    }
}
```

## 开发规范

### 1. 添加新功能模块

1. 在 `ui/` 下创建新的模块目录（如 `ui/player/`）
2. 在模块目录下创建 `activity/` 和 `viewmodel/` 子目录
3. 创建 Activity 继承 `BaseActivity`
4. 创建 ViewModel 继承 `BaseViewModel`

### 2. 添加网络接口

1. 在 `ApiService.kt` 中定义接口方法
2. 如果需要新的数据模型，创建对应的数据类
3. 在 ViewModel 中使用 `request()` 方法调用接口

### 3. DataBinding 使用

布局文件使用 `<layout>` 作为根标签：

```xml
<layout>
    <data>
        <variable
            name="viewModel"
            type="com.example.moodymusicforandroid.ui.module.ViewModel" />
    </data>

    <androidx.constraintlayout.widget.ConstraintLayout>
        <!-- UI components -->
    </androidx.constraintlayout.widget.ConstraintLayout>
</layout>
```

## 优势

1. **代码复用**：基类封装了大量通用功能
2. **类型安全**：使用泛型确保类型安全
3. **统一管理**：统一的网络请求、错误处理、加载状态
4. **清晰的分包**：按功能模块分包，易于维护
5. **协程支持**：使用 Kotlin 协程处理异步操作
6. **DataBinding**：减少样板代码，提高开发效率

## 注意事项

1. 所有 Activity 必须继承 `BaseActivity<VB, VM>`
2. 所有 Fragment 必须继承 `BaseFragment<VB, VM>`
3. 所有 ViewModel 必须继承 `BaseViewModel`
4. 网络请求统一使用 `request()` 方法
5. 数据模型必须实现 `ApiResponse` 接口或使用 `BaseResponse`
