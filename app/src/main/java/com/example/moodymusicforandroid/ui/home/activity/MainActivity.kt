package com.example.moodymusicforandroid.ui.home.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.moodymusicforandroid.MoodyMusicApplication
import com.example.moodymusicforandroid.common.eventbus.BaseEvent
import com.example.moodymusicforandroid.common.eventbus.EventBusManager
import com.example.moodymusicforandroid.common.eventbus.EventType
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.common.utils.AppFlags
import com.example.moodymusicforandroid.common.utils.FontManager
import com.example.moodymusicforandroid.common.utils.ThemeManager
import com.example.moodymusicforandroid.ui.album.AlbumDetailScreen
import com.example.moodymusicforandroid.ui.artist.ArtistDetailScreen
import com.example.moodymusicforandroid.ui.auth.activity.LoginActivity
import com.example.moodymusicforandroid.ui.classroom.activity.ClassroomActivity
import com.example.moodymusicforandroid.ui.home.DiscoverScreen
import com.example.moodymusicforandroid.ui.home.HomeScreen
import com.example.moodymusicforandroid.ui.home.LibraryScreen
import com.example.moodymusicforandroid.ui.home.components.AppDrawerContent
import com.example.moodymusicforandroid.ui.home.components.FloatingMiniPlayer
import com.example.moodymusicforandroid.ui.home.components.MainBottomBar
import com.example.moodymusicforandroid.ui.home.viewmodel.MainViewModel
import com.example.moodymusicforandroid.ui.navigation.*
import com.example.moodymusicforandroid.ui.theme.SongbookTheme
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import kotlin.math.roundToInt

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.FrameMetrics
import android.view.Window
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource

/**
 * 应用的主 Activity，承担单 Activity 架构的宿主角色。
 */
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(MoodyMusicApplication.currentThemeResId)
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        EventBusManager.register(this)
        ThemeManager.initTheme(this)
        setupJankMonitor()

        setContent {
            val owner = this@MainActivity as androidx.navigationevent.NavigationEventDispatcherOwner
            CompositionLocalProvider(
                androidx.navigationevent.compose.LocalNavigationEventDispatcherOwner provides owner
            ) {
                SongbookTheme {
                    MainScreen(
                        onAuthClick = {
                            startActivity(Intent(this, ClassroomActivity::class.java))
                        },
                        onThemeClick = { mode ->
                            ThemeManager.setTheme(this, mode)
                            (application as MoodyMusicApplication).updateTheme()
                            Toast.makeText(this, "已切换主题", Toast.LENGTH_SHORT).show()
                            recreate()
                        },
                        onFontClick = { style ->
                            FontManager.setFontStyle(this, style)
                            Toast.makeText(this, "已切换字体", Toast.LENGTH_SHORT).show()
                            recreate()
                        },
                        onLogoutClick = {
                            PreferencesManager.clearUserInfo()
                            EventBusManager.post(EventType.USER_LOGOUT, "用户退出登录")
                            Toast.makeText(this, "已退出当前认证", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (AppFlags.showKickOutDialog) {
            AppFlags.showKickOutDialog = false
            android.app.AlertDialog.Builder(this)
                .setTitle("下线通知")
                .setMessage("您的账号已在其他设备登录。当前设备已下线，您可以继续使用无需登录的功能。")
                .setPositiveButton("我知道了", null)
                .setNegativeButton("重新登录") { _, _ ->
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun setupJankMonitor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                display?.refreshRate ?: 60f
            } else {
                @Suppress("DEPRECATION")
                windowManager.defaultDisplay.refreshRate
            }
            val frameDeadlineMs = 1000f / refreshRate
            val jankThresholdMs = frameDeadlineMs * 1.5f

            // 使用独立后台线程处理掉帧监控日志，彻底避免监控代码本身在主线程进行字符串格式化和 Logcat IPC 引起掉帧
            val monitorThread = android.os.HandlerThread("JankMonitorThread").apply { start() }
            val handler = Handler(monitorThread.looper)
            window.addOnFrameMetricsAvailableListener(
                Window.OnFrameMetricsAvailableListener { _, frameMetrics, _ ->
                    val totalDurationNs = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
                    val durationMs = totalDurationNs / 1_000_000f
                    val layoutDurationMs = frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION) / 1_000_000f
                    val drawDurationMs = frameMetrics.getMetric(FrameMetrics.DRAW_DURATION) / 1_000_000f
                    val syncDurationMs = frameMetrics.getMetric(FrameMetrics.SYNC_DURATION) / 1_000_000f
                    val commandIssueMs = frameMetrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION) / 1_000_000f
                    val swapBuffersMs = frameMetrics.getMetric(FrameMetrics.SWAP_BUFFERS_DURATION) / 1_000_000f
                    val animDurationMs = frameMetrics.getMetric(FrameMetrics.ANIMATION_DURATION) / 1_000_000f
                    val inputDurationMs = frameMetrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION) / 1_000_000f
                    val gpuDurationMs = frameMetrics.getMetric(FrameMetrics.GPU_DURATION) / 1_000_000f

                    if (durationMs > jankThresholdMs) {
                        Log.w(
                            "JankMonitor",
                            "⚠️ [掉帧] 总耗时:${"%.1f".format(durationMs)}ms (基准:${"%.1f".format(frameDeadlineMs)}ms) | 排版:${"%.1f".format(layoutDurationMs)}ms | 绘制:${"%.1f".format(drawDurationMs)}ms | 同步(Sync):${"%.1f".format(syncDurationMs)}ms | 交换缓冲(Swap):${"%.1f".format(swapBuffersMs)}ms | 指令(Cmd):${"%.1f".format(commandIssueMs)}ms | 动画:${"%.1f".format(animDurationMs)}ms | 输入:${"%.1f".format(inputDurationMs)}ms | GPU:${"%.1f".format(gpuDurationMs)}ms"
                        )
                    }
                },
                handler
            )
            Log.i("JankMonitor", "🚀 [JankMonitor] 掉帧监控器已启动(后台线程监听)，当前屏幕刷新率: ${refreshRate.toInt()}Hz，单帧预算: ${"%.1f".format(frameDeadlineMs)}ms")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventReceived(event: BaseEvent) {
        if (event.eventType == EventType.AUTH_TOKEN_EXPIRED) {
            val isKickedOut = event.eventData == "KICKED_OUT"
            if (isKickedOut) {
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
                    AppFlags.showKickOutDialog = true
                    onResume()
                }
            } else {
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("KICKED_OUT", false)
                }
                startActivity(intent)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBusManager.unregister(this)
        Log.d(TAG, "MainActivity destroyed")
    }
}

/**
 * 整个应用的主屏幕 Compose 入口组件
 */
@Composable
fun MainScreen(
    onAuthClick: () -> Unit,
    onThemeClick: (ThemeManager.ThemeMode) -> Unit,
    onFontClick: (FontManager.FontStyle) -> Unit,
    onLogoutClick: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    var isLoggedIn by remember { mutableStateOf(PreferencesManager.isLoggedIn()) }
    var userName by remember { mutableStateOf(PreferencesManager.getUserName() ?: "同学") }

    var currentTrackTitle by remember { mutableStateOf("苔藓上的私语") }
    var currentTrackArtist by remember { mutableStateOf("周深处 & 森林合唱团") }
    var isPlaying by remember { mutableStateOf(true) }
    
    DisposableEffect(Unit) {
        val subscriber = object {
            @Subscribe(threadMode = ThreadMode.MAIN)
            fun onEvent(event: BaseEvent) {
                if (event.eventType == EventType.USER_LOGIN || event.eventType == EventType.USER_LOGOUT) {
                    isLoggedIn = PreferencesManager.isLoggedIn()
                    userName = PreferencesManager.getUserName() ?: "同学"
                }
            }
        }
        EventBusManager.register(subscriber)
        onDispose {
            EventBusManager.unregister(subscriber)
        }
    }

    val navigationState = rememberNavigationState(
        startRoute = RouteHome,
        topLevelRoutes = setOf(RouteHome, RouteDiscover, RouteLibrary)
    )
    val navigator = remember { Navigator(navigationState) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                AppDrawerContent(
                    isLoggedIn = isLoggedIn,
                    userName = userName,
                    onCloseClick = { coroutineScope.launch { drawerState.close() } },
                    onAuthClick = {
                        coroutineScope.launch { drawerState.close() }
                        onAuthClick()
                    },
                    onLogoutClick = {
                        coroutineScope.launch { drawerState.close() }
                        onLogoutClick()
                    },
                    onThemeClick = onThemeClick,
                    onFontClick = onFontClick
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            val entryProvider = remember {
                entryProvider {
                    entry<RouteHome> {
                        HomeScreen(
                            onMenuClick = { coroutineScope.launch { drawerState.open() } },
                            onAvatarClick = onAuthClick,
                            onAlbumClick = { id, title ->
                                navigator.navigate(RouteAlbumDetail(id, title))
                            },
                            onArtistClick = { id, name ->
                                navigator.navigate(RouteArtistDetail(id, name))
                            },
                            onArticleClick = { _ ->
                                navigator.navigate(RouteAlbumDetail("vinyl_soul", "回响：寻找消失的黑胶灵魂"))
                            }
                        )
                    }

                    entry<RouteDiscover> {
                        DiscoverScreen(
                            onMenuClick = { coroutineScope.launch { drawerState.open() } },
                            onArtistClick = { id, name ->
                                navigator.navigate(RouteArtistDetail(id, name))
                            }
                        )
                    }

                    entry<RouteLibrary> {
                        LibraryScreen()
                    }

                    entry<RouteArtistDetail> { key ->
                        ArtistDetailScreen(
                            artistId = key.artistId,
                            artistName = key.artistName,
                            onBackClick = { navigator.goBack() },
                            onAlbumClick = { id, title ->
                                navigator.navigate(RouteAlbumDetail(id, title))
                            },
                            onPlayAllClick = {
                                currentTrackTitle = "午后的回声"
                                currentTrackArtist = key.artistName
                                isPlaying = true
                            }
                        )
                    }

                    entry<RouteAlbumDetail> { key ->
                        AlbumDetailScreen(
                            albumId = key.albumId,
                            albumTitle = key.albumTitle,
                            onBackClick = { navigator.goBack() },
                            onTrackClick = { track ->
                                currentTrackTitle = track.title
                                currentTrackArtist = "周深处 & 森林合唱团"
                                isPlaying = true
                            },
                            onPlayAllClick = {
                                currentTrackTitle = "晨露中的第一道光"
                                currentTrackArtist = "周深处 & 森林合唱团"
                                isPlaying = true
                            }
                        )
                    }

                    entry<RouteMusicDetail> { key ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "🎵 音乐详情 (Navigation 3)\n\n当前歌曲 ID: ${key.songId}",
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                    }
                }
            }

            val hazeState = remember { HazeState() }

            // 类似 YouTube 的滑动自适应智能感知：上滑下潜隐藏，下滑弹性浮现
            var isBottomBarVisible by remember { mutableStateOf(true) }

            val nestedScrollConnection = remember {
                object : NestedScrollConnection {
                    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                        // available.y < -6f: 手指上滑（页面向下滚）-> 隐藏底栏
                        // available.y > 6f: 手指下滑（页面向上滚）-> 显示底栏
                        if (available.y < -6f && isBottomBarVisible) {
                            isBottomBarVisible = false
                        } else if (available.y > 6f && !isBottomBarVisible) {
                            isBottomBarVisible = true
                        }
                        return Offset.Zero
                    }
                }
            }

            // 切换 Tab 或页面时自动唤醒并升起底栏
            LaunchedEffect(navigationState.topLevelRoute) {
                isBottomBarVisible = true
            }

            val bottomBarOffsetY by androidx.compose.animation.core.animateDpAsState(
                targetValue = if (isBottomBarVisible) 0.dp else 110.dp,
                animationSpec = androidx.compose.animation.core.spring(
                    dampingRatio = androidx.compose.animation.core.Spring.DampingRatioLowBouncy,
                    stiffness = androidx.compose.animation.core.Spring.StiffnessMediumLow
                ),
                label = "BottomBarScrollAnimation"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(nestedScrollConnection)
            ) {
                NavDisplay(
                    entries = navigationState.toEntries(entryProvider),
                    onBack = { navigator.goBack() },
                    modifier = Modifier
                        .fillMaxSize()
                        .hazeSource(state = hazeState)
                )

                val density = LocalDensity.current
                // 全局悬浮组件区域（Capsule Dock）- 使用 GPU 硬件变换矩阵 translationY，0 帧率重排开销
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .graphicsLayer {
                            translationY = with(density) { bottomBarOffsetY.toPx() }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // 1. 全局悬浮 Mini 播放器 (暂隐藏，后续定制样式)
                    /*
                    FloatingMiniPlayer(
                        trackTitle = currentTrackTitle,
                        artistName = currentTrackArtist,
                        isPlaying = isPlaying,
                        onPlayPauseClick = { isPlaying = !isPlaying },
                        onPlayerClick = {
                            navigator.navigate(RouteAlbumDetail("playing_album", currentTrackTitle))
                        }
                    )
                    */

                    // 2. 悬浮胶囊 Dock 底栏 (Compose 官方推荐 Haze 真实高斯模糊)
                    MainBottomBar(
                        currentRoute = navigationState.topLevelRoute,
                        onNavigate = { route ->
                            navigationState.topLevelRoute = route as androidx.navigation3.runtime.NavKey
                            navigator.navigate(route as androidx.navigation3.runtime.NavKey)
                        },
                        hazeState = hazeState
                    )
                }
            }

        }
    }
}
