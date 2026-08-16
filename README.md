# 音信 (TunePost)

一个基于情绪共鸣、原声手札与同学互动场景的原生 Android 应用，采用 `Kotlin + XML(DataBinding) + MVVM`。

## 项目概览

MoodyMusicForAndroid 目前包含三条核心业务线：

1. 音乐内容浏览（Home / Discover / Library）
2. 教室座位认领与登录流程（Classroom Claim）
3. 专辑社交互动与推送驱动刷新（Album Social + JPush）

## 最近代码已实现（与当前仓库一致）

### 1. 教室座位认领闭环（已打通）

- 支持座位表拉取与空位补齐展示（最多 64 座位）
- 支持 3 题安全问题校验：`/api/user/claim/verify`
- 支持认领完成：`/api/user/claim/finalize`
- 支持已认领用户密码登录：`/api/user/login`
- 成功后统一写入 `PreferencesManager` 并发送登录事件

关键代码：
- `app/src/main/java/com/example/moodymusicforandroid/ui/classroom/activity/ClassroomActivity.kt`
- `app/src/main/java/com/example/moodymusicforandroid/ui/classroom/viewmodel/ClassroomViewModel.kt`

### 2. 专辑社交模块 (V2 班级隔离版)

- **班级隔离**：仅限已认领座位的班级成员查看所属班级的讨论。
- **访客屏蔽**：未登录或访客身份访问将触发 403，UI 自动隐藏。
- **聚合接口**：`GET /api/albums/{albumId}/social_content` 返回“主贴+平铺回复”。
- **发布讨论**：`POST /api/albums/{albumId}/posts` (自动关联班级 ID)。
- **发布评论**：`POST /api/albums/posts/{postId}/comments`。

关键代码：
- `app/src/main/java/com/example/moodymusicforandroid/ui/music/viewmodel/AlbumSocialViewModel.kt`
- `app/src/main/java/com/example/moodymusicforandroid/ui/home/LibraryFragment.kt`

### 3. JPush 信号驱动刷新

- **Tag 策略**：动态绑定 `album_{albumId}_class_{classId}` 实现精准通知。
- **透传逻辑**：解析 `refresh_comments` 透传消息，Extras 携带 `action: "FETCH_NEW"`。
- **刷新机制**：前台即时 LocalBroadcast，后台打脏标记（Dirty Flag）待回前台刷新。
- 页面生命周期中动态绑定/清理 tag

关键代码：
- `app/src/main/java/com/example/moodymusicforandroid/MoodyMusicApplication.kt`
- `app/src/main/java/com/example/moodymusicforandroid/receiver/JPushReceiver.kt`

### 4. 最近新增的 UI 资源（进行中）

最近提交中增加了一批教室座位视觉资源（如 `bg_seat_desk_refined.xml`、`bg_seat_status_*.xml` 等），用于后续教室 UI 细化与质感升级。

## 接下来要做（Roadmap）

1. 教室 UI 第二轮接线：将新座位素材与 `item_seat.xml`/`SeatAdapter` 完整联动
2. 社交体验增强：回复输入态、失败重试、点赞与分页加载
3. 推送稳定性增强：补齐弱网/离线场景与重复消息去重
4. 音乐播放内核：接入 ExoPlayer，补齐前台服务与锁屏控制
5. 本地数据层：引入 Room 做缓存与离线兜底

## 技术栈

- Kotlin `2.1.0`
- AGP `8.10.1`
- Coroutines `1.9.0`
- Retrofit `2.11.0` + OkHttp `4.12.0`
- JPush `5.9.2`
- Glide `4.16.0`
- EventBus `3.3.1`
- BaseRecyclerViewAdapterHelper `4.1.4`

依赖版本集中管理：`gradle/libs.versions.toml`

## 构建与运行

```bash
# Debug 构建
./gradlew assembleDebug

# 全量构建
./gradlew build

# 单测
./gradlew testDebugUnitTest
```

## 环境注意事项

- 建议 JDK 17+
- AGP 8.10.x 推荐安装 NDK `27.0.12077973`
- 若出现 `Unable to strip ... libjutils.so`，通常是本机缺少 NDK strip 工具，不是业务代码错误

## 项目结构

```text
app/                # 应用层（Activity/Fragment/ViewModel/UI 资源）
commonbase/         # 基础层（Base 类、网络、数据模型、公共能力）
gradle/             # 版本目录（libs.versions.toml）
```

## 维护说明

- 本仓库使用 MVVM + BaseActivity/BaseFragment/BaseViewModel 模式
- 网络请求统一走 `BaseViewModel.request()`
- 新增依赖请优先修改 `libs.versions.toml`
