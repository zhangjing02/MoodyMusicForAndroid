# MoodyMusicForAndroid

一个基于情绪的音乐推荐 Android 原生应用，采用 MVVM 架构和 Kotlin 语言开发。

## 项目概述

MoodyMusicForAndroid 是一个现代化的 Android 音乐应用，通过分析用户的情绪状态来推荐合适的音乐。项目采用 MVVM 架构模式，使用 Kotlin + XML DataBinding 开发，并实现了自定义的基础类框架来减少代码冗余。

## 核心技术与特性 (v1.2.0)

### 1. 实时社交同步引擎 (JPush Powered)
- **Signal-over-Data 架构**: 推送系统仅传输“刷新信号”（如 `FETCH_NEW`），由客户端在接收到信号后通过安全的 API 接口获取最新内容，保障数据安全性与一致性。
- **生命周期感知同步**: 
  - **前台**: 实时接收信号并触发 UI 局部刷新。
  - **后台**: 自动标记“脏状态”，用户返回前台时自动增量刷新。
- **动态路由**: 基于 `album_{ID}` 标签精准分发，确保讨论刷新仅触达相关用户。

### 2. 交互式专辑社交模块
- **无感集成**: 社交评论区深度嵌入在专辑详情页面底部，支持滑入式交互。
- **实时交互**: 支持即时发布评论、多级回复预览及点赞（规划中）。

### 3. 高性能 UI 与 架构
- **RWidgetHelper**: 声明式 UI 增强，支持圆角、渐变、状态色等复杂效果，无需编写 XML Shape。
- **MVVM 泛型基类**: 内置协程管理、网络请求包装及统一错误处理。

## 未来路线图 (Roadmap)

- [ ] **音频播放核心 (ExoPlayer)**: 实现基于前台服务的全局播放、锁屏控制及蓝牙支持。
- [ ] **数据持久化 (Room + SQLCipher)**: 引入加密本地数据库，支持离线缓存。
- [ ] **全局搜索系统**: 实现带历史记录与搜索建议的智能入口。
- [ ] **主题引擎**: 支持随专辑封面主色调动态调整的应用配色。

## 技术栈

- **核心**: Kotlin 2.1.0, Coroutines, MVVM
- **UI**: DataBinding, Material Design, RWidgetHelper
- **网络**: Retrofit 2.11.0, OkHttp, JPush 5.4.0
- **图片**: Glide 4.16.0
- **其他**: EventBus 3.3.1, BaseRecyclerViewAdapterHelper 4.1.4

## 项目结构

```
MoodyMusicForAndroid/
├── app/                                    # 应用模块
│   └── src/main/java/com/example/moodymusicforandroid/
│       ├── receiver/                       # 推送信号处理
│       ├── ui/                             # 各业务模块 (Home, Social, Music)
│       └── MoodyMusicApplication.kt        # 初始化入口
├── commonbase/                             # 基础库模块 (Base, Network, Data)
└── gradle/                                 # 依赖版本目录 (libs.versions.toml)
```

## 维护说明

- **Git 优化**: 移除了所有构建缓存（.gradle, .gradle-user-home），保持仓库整洁。
- **构建指南**: 建议在资源冲突时运行 `gradlew clean assembleDebug`。

---
**Maintainer**: zhangjing
**License**: MIT
