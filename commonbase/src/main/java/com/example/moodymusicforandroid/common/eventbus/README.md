# EventBus 使用指南

本项目中 EventBus 已经做了完整封装，提供了统一的事件对象和自动注册/注销机制。

## 核心组件

### 1. BaseEvent - 统一的事件对象

所有事件都使用这个类，包含三个属性：
- `eventType: Int` - 事件类型（使用 EventType 常量）
- `eventMessage: String` - 事件消息（可选）
- `eventData: Any?` - 事件数据（可选）

### 2. EventType - 事件类型常量

所有事件类型都在这里定义，避免硬编码。已定义的事件类型包括：

- **音乐播放**：`MUSIC_PLAY`、`MUSIC_PAUSE`、`MUSIC_RESUME`、`MUSIC_STOP`、`MUSIC_NEXT`、`MUSIC_PREVIOUS`
- **用户相关**：`USER_LOGIN`、`USER_LOGOUT`、`USER_INFO_UPDATE`
- **UI相关**：`UI_REFRESH`、`UI_RELOAD`、`UI_SHOW_TOAST`、`UI_CLOSE_PAGE`
- **网络相关**：`Network_CONNECTED`、`Network_DISCONNECTED`
- 更多事件类型请查看 `EventType.kt` 文件

### 3. EventBusManager - 事件管理器

提供简洁的 API 发送事件：
```kotlin
// 发送简单事件
EventBusManager.post(EventType.MUSIC_PLAY)

// 发送带数据的事件
EventBusManager.postWithData(EventType.MUSIC_PLAY, musicData)

// 发送带消息的事件
EventBusManager.postWithMessage(EventType.UI_SHOW_TOAST, "操作成功")

// 发送完整事件（消息 + 数据）
EventBusManager.post(EventType.MUSIC_PLAY, "开始播放", musicData)
```

## 自动注册机制

**重要**：Activity 和 Fragment 会自动注册和注销 EventBus，不需要手动调用！

在基类中已经处理好了：
- `BaseActivity` 在 `onCreate` 时自动注册，`onDestroy` 时自动注销
- `BaseFragment` 在 `onCreate` 时自动注册，`onDestroyView` 时自动注销

如果不希望某个页面接收事件，可以重写 `useEventBus()` 方法：
```kotlin
override fun useEventBus(): Boolean = false
```

## 使用方法

### 步骤 1：发送事件

在任何地方发送事件：

```kotlin
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType

// 方式1：发送简单事件
EventBusManager.post(EventType.MUSIC_PLAY)

// 方式2：发送带消息的事件
EventBusManager.postWithMessage(EventType.UI_SHOW_TOAST, "保存成功")

// 方式3：发送带数据的事件
EventBusManager.postWithData(EventType.MUSIC_CURRENT_CHANGED, currentSong)

// 方式4：发送完整事件
EventBusManager.post(EventType.MUSIC_PROGRESS_UPDATE, "进度更新", progressData)

// 方式5：使用便捷方法
EventBusManager.postMusicPlay(musicData)
EventBusManager.postMusicPause()
EventBusManager.postUserLogin(userData)
EventBusManager.postNetworkConnected()
```

### 步骤 2：接收事件

在 Activity 或 Fragment 中接收事件：

```kotlin
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventType
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MusicActivity : BaseActivity<ActivityMusicBinding, MusicViewModel>() {

    override fun getViewModelClass() = MusicViewModel::class.java
    override fun getLayoutId() = R.layout.activity_music

    // 添加 @Subscribe 注解接收事件
    @Subscribe(threadMode = ThreadMode.MAIN)
    override fun onEventReceived(event: BaseEvent) {
        when(event.eventType) {
            EventType.MUSIC_PLAY -> handleMusicPlay(event)
            EventType.MUSIC_PAUSE -> handleMusicPause(event)
            EventType.MUSIC_PROGRESS_UPDATE -> handleProgressUpdate(event)
            EventType.USER_LOGIN -> handleUserLogin(event)
        }
    }

    private fun handleMusicPlay(event: BaseEvent) {
        val musicData = event.getData<Song>()
        val message = event.eventMessage
        // 处理播放逻辑
    }

    private fun handleProgressUpdate(event: BaseEvent) {
        val progressData = event.getData<Map<String, Any>>()
        val progress = progressData?.get("progress") as? Int ?: 0
        // 更新进度条
    }

    private fun handleUserLogin(event: BaseEvent) {
        showToast("欢迎回来，${event.eventMessage}")
    }
}
```

### 步骤 3：提取数据

使用扩展方法提取事件数据：

```kotlin
// 方式1：安全获取（类型不匹配返回 null）
val song = event.getData<Song>()
if (song != null) {
    // 使用 song
}

// 方式2：获取默认值
val song = event.getDataOrDefault(Song())

// 方式3：直接访问（需要自己处理类型）
val data = event.eventData
if (data is Song) {
    // 使用 data
}
```

## 完整示例

### 示例 1：音乐播放控制

**发送事件**（在播放器服务或 ViewModel 中）：
```kotlin
// 开始播放
EventBusManager.postMusicPlay("开始播放", currentSong)

// 暂停
EventBusManager.postMusicPause()

// 更新进度
EventBusManager.postMusicProgress(
    progress = 50,
    currentPosition = 125000L,
    duration = 250000L
)
```

**接收事件**（在 Activity 或 Fragment 中）：
```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    when(event.eventType) {
        EventType.MUSIC_PLAY -> {
            val song = event.getData<Song>()
            binding.tvSongName.text = song?.title
            binding.tvArtist.text = song?.artist
        }
        EventType.MUSIC_PAUSE -> {
            binding.btnPlay.text = "播放"
        }
        EventType.MUSIC_PROGRESS_UPDATE -> {
            val data = event.getData<Map<String, Any>>()
            val progress = data?.get("progress") as? Int ?: 0
            binding.progressBar.progress = progress
        }
    }
}
```

### 示例 2：登录状态同步

**发送事件**（登录成功后）：
```kotlin
// 在登录 ViewModel 中
EventBusManager.postUserLogin(userInfo)

// 或在退出登录时
EventBusManager.postUserLogout("用户已登出")
```

**接收事件**（在多个页面中）：
```kotlin
// 在个人中心页面
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    if (event.isType(EventType.USER_LOGIN)) {
        val user = event.getData<User>()
        updateUserInfo(user)
    } else if (event.isType(EventType.USER_LOGOUT)) {
        navigateToLogin()
    }
}

// 在主页面
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    when(event.eventType) {
        EventType.USER_LOGIN -> {
            binding.btnLogin.visibility = View.GONE
            binding.btnProfile.visibility = View.VISIBLE
        }
        EventType.USER_LOGOUT -> {
            binding.btnLogin.visibility = View.VISIBLE
            binding.btnProfile.visibility = View.GONE
        }
    }
}
```

### 示例 3：网络状态变化

**发送事件**（在网络监听器中）：
```kotlin
override fun onAvailable(network: Network) {
    EventBusManager.postNetworkConnected()
}

override fun onLost(network: Network) {
    EventBusManager.postNetworkDisconnected()
}
```

**接收事件**（在需要网络状态的页面）：
```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    when(event.eventType) {
        EventType.Network_CONNECTED -> {
            binding.tvNetworkStatus.text = "网络已连接"
            binding.tvNetworkStatus.setTextColor(Color.GREEN)
            // 重新加载数据
            viewModel.loadData()
        }
        EventType.Network_DISCONNECTED -> {
            binding.tvNetworkStatus.text = "网络已断开"
            binding.tvNetworkStatus.setTextColor(Color.RED)
        }
    }
}
```

## 粘性事件

如果需要在事件发送后才注册的订阅者也能收到事件，可以使用粘性事件：

```kotlin
// 发送粘性事件
EventBusManager.postSticky(EventType.MUSIC_PLAY, "开始播放", musicData)

// 获取粘性事件
val stickyEvent = EventBusManager.getStickyEvent(BaseEvent::class.java)

// 移除粘性事件
EventBusManager.removeStickyEvent(stickyEvent)

// 移除所有粘性事件
EventBusManager.removeAllStickyEvents()
```

## 添加新事件类型

如果需要添加新的事件类型，在 `EventType.kt` 中添加：

```kotlin
object EventType {
    // ... 已有的事件类型

    // 自定义事件（从 20000 开始）
    const val MY_CUSTOM_EVENT = 20001
    const val ANOTHER_EVENT = 20002

    // 在 getEventTypeName 方法中添加名称映射
    fun getEventTypeName(type: Int): String {
        return when (type) {
            // ...
            MY_CUSTOM_EVENT -> "MY_CUSTOM_EVENT"
            ANOTHER_EVENT -> "ANOTHER_EVENT"
            else -> "UNKNOWN_EVENT_$type"
        }
    }
}
```

## 注意事项

### 1. 必须添加 @Subscribe 注解

接收事件时必须添加 `@Subscribe` 注解，否则无法接收事件：
```kotlin
// ✅ 正确
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    // 处理事件
}

// ❌ 错误 - 缺少注解，无法接收事件
override fun onEventReceived(event: BaseEvent) {
    // 处理事件
}
```

### 2. ThreadMode 选择

- `ThreadMode.MAIN` - 在主线程接收事件（默认，推荐用于UI更新）
- `ThreadMode.POSTING` - 在发送事件的线程接收
- `ThreadMode.BACKGROUND` - 在后台线程接收
- `ThreadMode.ASYNC` - 在独立线程接收

### 3. 避免内存泄漏

- **已自动处理**：基类会自动注销 EventBus，无需手动调用
- 如果在 `onEventReceived` 中持有 Activity/Fragment 引用，注意及时释放

### 4. 事件发送顺序

EventBus 默认按照注册顺序发送事件，如果需要控制优先级，可以使用 `priority`：
```kotlin
@Subscribe(threadMode = ThreadMode.MAIN, priority = 1)
override fun onEventReceived(event: BaseEvent) {
    // 高优先级
}
```

### 5. 阻止事件传递

如果需要阻止事件继续传递给其他订阅者：
```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    if (event.eventType == EventType.MUSIC_PLAY) {
        // 处理后阻止传递
        EventBusManager.cancelEventDelivery(event)
    }
}
```

## ThreadMode 详解

### ThreadMode.MAIN（推荐用于UI更新）

在主线程接收事件，可以直接更新UI：
```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    // 可以直接更新 UI
    binding.tvText.text = event.eventMessage
}
```

### ThreadMode.POSTING

在发送事件的同一线程接收：
```kotlin
@Subscribe(threadMode = ThreadMode.POSTING)
override fun onEventReceived(event: BaseEvent) {
    // 在发送事件的线程执行
}
```

### ThreadMode.BACKGROUND

在后台线程接收（如果发送线程是主线程，则使用单个后台线程）：
```kotlin
@Subscribe(threadMode = ThreadMode.BACKGROUND)
override fun onEventReceived(event: BaseEvent) {
    // 在后台线程执行，适合耗时操作
    processData(event.eventData)
}
```

### ThreadMode.ASYNC

在独立线程接收，总是使用新线程：
```kotlin
@Subscribe(threadMode = ThreadMode.ASYNC)
override fun onEventReceived(event: BaseEvent) {
    // 在独立线程执行，适合耗时操作
    heavyComputation(event.eventData)
}
```

## 调试技巧

### 打印事件信息

```kotlin
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    // 打印事件信息
    Log.d("EventBus", event.toString())
    // 输出：BaseEvent(type=1001, message='开始播放', data=Song(...))

    val typeName = EventType.getEventTypeName(event.eventType)
    Log.d("EventBus", "收到事件: $typeName")
    // 输出：收到事件: MUSIC_PLAY
}
```

### 检查是否注册

```kotlin
// 检查当前页面是否已注册
val isRegistered = EventBusManager.isRegistered(this)
Log.d("EventBus", "是否已注册: $isRegistered")
```

## 最佳实践

### 1. 使用 EventType 常量

```kotlin
// ✅ 推荐 - 使用常量
EventBusManager.post(EventType.MUSIC_PLAY)

// ❌ 不推荐 - 硬编码
EventBusManager.post(1001)
```

### 2. 事件数据使用数据类

```kotlin
// ✅ 推荐 - 使用数据类
data class SongProgress(
    val progress: Int,
    val currentPosition: Long,
    val duration: Long
)

EventBusManager.postWithData(EventType.MUSIC_PROGRESS_UPDATE, SongProgress(50, 125000, 250000))

// ❌ 不推荐 - 使用 Map
val data = mapOf("progress" to 50, "currentPosition" to 125000)
EventBusManager.postWithData(EventType.MUSIC_PROGRESS_UPDATE, data)
```

### 3. 在 when 中处理所有分支

```kotlin
// ✅ 推荐 - 使用 else 分支
@Subscribe(threadMode = ThreadMode.MAIN)
override fun onEventReceived(event: BaseEvent) {
    when(event.eventType) {
        EventType.MUSIC_PLAY -> handleMusicPlay(event)
        EventType.MUSIC_PAUSE -> handleMusicPause(event)
        else -> {
            Log.d("EventBus", "未处理的事件: ${event.eventType}")
        }
    }
}
```

### 4. 及时清理粘性事件

```kotlin
// 使用完粘性事件后及时移除
val stickyEvent = EventBusManager.getStickyEvent(BaseEvent::class.java)
if (stickyEvent != null) {
    // 处理事件
    processEvent(stickyEvent)
    // 移除事件
    EventBusManager.removeStickyEvent(stickyEvent)
}
```

## 常见问题

### Q: 为什么收不到事件？

检查以下几点：
1. 是否添加了 `@Subscribe` 注解
2. 是否重写了 `onEventReceived(event: BaseEvent)` 方法
3. `useEventBus()` 是否返回 true（默认为 true）
4. 事件类型是否匹配

### Q: 如何在 Service 中接收事件？

Service 不会自动注册，需要手动注册和注销：
```kotlin
class MyService : Service() {
    override fun onCreate() {
        super.onCreate()
        EventBusManager.register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: BaseEvent) {
        // 处理事件
    }
}
```

### Q: 可以跨模块发送事件吗？

可以，EventBus 在整个应用中都是通用的，只要在 commonbase 中定义的事件类型都可以使用。

## 总结

EventBus 封装的核心优势：

1. ✅ **自动注册/注销** - 基类自动处理，无需手动调用
2. ✅ **统一事件对象** - 所有事件使用 BaseEvent，结构清晰
3. ✅ **类型安全** - 使用 EventType 常量，避免硬编码
4. ✅ **简洁 API** - 提供便捷方法，发送事件一行代码搞定
5. ✅ **灵活扩展** - 可以轻松添加新的事件类型和数据

记住两个要点：
- **发送**：`EventBusManager.post(...)`
- **接收**：添加 `@Subscribe` 注解，重写 `onEventReceived(...)`
