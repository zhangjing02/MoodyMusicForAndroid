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
    var currentRoute by remember { mutableStateOf<Any>(RouteHome) }

    val bottomBarHeight = 120.dp
    val bottomBarHeightPx = with(LocalDensity.current) { bottomBarHeight.roundToPx().toFloat() }
    val bottomBarOffsetHeightPx = remember { mutableStateOf(0f) }

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = bottomBarOffsetHeightPx.value - delta
                bottomBarOffsetHeightPx.value = newOffset.coerceIn(0f, bottomBarHeightPx)
                return Offset.Zero
            }
        }
    }

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
                .nestedScroll(nestedScrollConnection)
        ) {
            val entryProvider = entryProvider {
                entry<RouteHome> {
                    currentRoute = RouteHome
                    HomeScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onAvatarClick = onAuthClick,
                        onAlbumClick = { id, title ->
                            currentRoute = RouteAlbumDetail(id, title)
                            navigator.navigate(RouteAlbumDetail(id, title))
                        },
                        onArtistClick = { id, name ->
                            currentRoute = RouteArtistDetail(id, name)
                            navigator.navigate(RouteArtistDetail(id, name))
                        },
                        onArticleClick = { _ ->
                            currentRoute = RouteAlbumDetail("vinyl_soul", "回响：寻找消失的黑胶灵魂")
                            navigator.navigate(RouteAlbumDetail("vinyl_soul", "回响：寻找消失的黑胶灵魂"))
                        }
                    )
                }

                entry<RouteDiscover> {
                    currentRoute = RouteDiscover
                    DiscoverScreen(
                        onMenuClick = { coroutineScope.launch { drawerState.open() } },
                        onArtistClick = { id, name ->
                            currentRoute = RouteArtistDetail(id, name)
                            navigator.navigate(RouteArtistDetail(id, name))
                        }
                    )
                }

                entry<RouteLibrary> {
                    currentRoute = RouteLibrary
                    LibraryScreen()
                }

                entry<RouteArtistDetail> { key ->
                    currentRoute = key
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
                    currentRoute = key
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
                    currentRoute = key
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

            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = { navigator.goBack() }
            )

            // 全局悬浮组件区域（Capsule Dock）
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset { IntOffset(x = 0, y = bottomBarOffsetHeightPx.value.roundToInt()) }
                    .padding(bottom = 16.dp),
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

                // 2. 悬浮胶囊 Dock 底栏
                MainBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        currentRoute = route
                        navigator.navigate(route as androidx.navigation3.runtime.NavKey)
                    }
                )
            }

        }
    }
}
