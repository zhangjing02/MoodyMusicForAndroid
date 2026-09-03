package com.example.moodymusicforandroid.ui.home.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.moodymusicforandroid.base.BaseViewModel
import com.example.moodymusicforandroid.common.network.ApiServiceProvider
import com.example.moodymusicforandroid.common.config.AppConfig
import com.example.moodymusicforandroid.data.api.MoodyApiProvider
import com.example.moodymusicforandroid.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 首页 ViewModel
 * 管理切片化动态首页流 (Block-Based SDUI) 的网络拉取、多态状态解析与离线保底默认数据。
 */
class HomeViewModel : BaseViewModel() {

    private val _homeFeedItems = MutableStateFlow<List<HomeBlockItem>>(getOfflineDefaultFeed())
    val homeFeedItems: StateFlow<List<HomeBlockItem>> = _homeFeedItems.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow("all")
    val selectedCategoryId: StateFlow<String> = _selectedCategoryId.asStateFlow()

    private val _currentlyPlayingTrackId = MutableStateFlow<String?>(null)
    val currentlyPlayingTrackId: StateFlow<String?> = _currentlyPlayingTrackId.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        fetchHomeFeed()
    }

    /**
     * 从服务端拉取动态切片流 (SDUI)
     */
    fun fetchHomeFeed() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                // 1. 优先请求 ApiService 接口
                val response = ApiServiceProvider.apiService.getHomeFeed()
                if (response.isSuccessful && response.body() != null) {
                    val feed = response.body()!!
                    if (feed.items.isNotEmpty()) {
                        _homeFeedItems.value = preParseItems(feed.items)
                        _isRefreshing.value = false
                        return@launch
                    }
                }

                // 2. 备用尝试 MoodyApiService 统一接口
                val moodyResponse = MoodyApiProvider.apiService.getHomeFeed()
                if (moodyResponse.code == 200 && moodyResponse.data != null) {
                    val feed = moodyResponse.data!!
                    if (feed.items.isNotEmpty()) {
                        _homeFeedItems.value = preParseItems(feed.items)
                        _isRefreshing.value = false
                        return@launch
                    }
                }
            } catch (e: Exception) {
                // 网络异常或无数据时，静默保留高质量离线保底数据
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private fun preParseItems(items: List<HomeBlockItem>): List<HomeBlockItem> {
        return items.map { block ->
            if (block.parsedData != null) return@map block
            val parsed = when (block.type) {
                HomeBlockType.HERO_BANNER -> block.toHeroBanner()
                HomeBlockType.CATEGORY_TABS -> block.toCategoryTabs()
                HomeBlockType.SECTION_TITLE -> block.toSectionTitle()
                HomeBlockType.ESSAY_CARD -> block.toEssayCard()
                HomeBlockType.ARTIST_GRID -> block.toArtistGrid()
                HomeBlockType.TRACK_LIST -> block.toTrackList()
                HomeBlockType.ARCHIVE_CARD -> block.toArchiveCard()
                HomeBlockType.IMAGE_FEATURE -> block.toImageFeature()
                HomeBlockType.QUICK_ACTIONS -> block.toQuickActions()
                else -> null
            }
            if (parsed != null) block.copy(parsedData = parsed) else block
        }
    }

    /**
     * 切换分类标签状态
     */
    fun selectCategory(categoryId: String) {
        _selectedCategoryId.value = categoryId
        _homeFeedItems.value = _homeFeedItems.value.map { block ->
            if (block.type == HomeBlockType.CATEGORY_TABS) {
                val currentTabsData = block.toCategoryTabs()
                val updatedTabs = currentTabsData.tabs.map { tab ->
                    tab.copy(isSelected = (tab.id == categoryId))
                }
                block.copy(parsedData = currentTabsData.copy(tabs = updatedTabs, selectedId = categoryId))
            } else {
                block
            }
        }
    }

    /**
     * 切换单曲试听播放状态
     */
    fun toggleTrackPlay(trackId: String) {
        val nextPlayingId = if (_currentlyPlayingTrackId.value == trackId) null else trackId
        _currentlyPlayingTrackId.value = nextPlayingId

        _homeFeedItems.value = _homeFeedItems.value.map { block ->
            if (block.type == HomeBlockType.TRACK_LIST) {
                val trackListData = block.toTrackList()
                val updatedTracks = trackListData.tracks.map { track ->
                    track.copy(isPlaying = (track.id == nextPlayingId))
                }
                block.copy(parsedData = trackListData.copy(tracks = updatedTracks))
            } else {
                block
            }
        }
    }

    /**
     * 离线保底默认数据列表（保证网络断开或首次加载时 UI 完美呈现全部 9 种切片）
     */
    private fun getOfflineDefaultFeed(): List<HomeBlockItem> {
        return listOf(
            // 1. 今日精选主标题与期号 (section_title)
            HomeBlockItem(
                id = "block_section_main_title",
                type = HomeBlockType.SECTION_TITLE,
                parsedData = SectionTitleData(
                    title = "今日精选",
                    subtitle = "10月24日，周一 • 第 82 期",
                    actionText = "SOCIETY WEEKLY"
                )
            ),

            // 2. 分类标签栏 (category_tabs)
            HomeBlockItem(
                id = "block_category_tabs",
                type = HomeBlockType.CATEGORY_TABS,
                parsedData = CategoryTabsData(
                    selectedId = "all",
                    tabs = listOf(
                        CategoryTabItem("all", "全部", isSelected = true),
                        CategoryTabItem("folk", "民谣复兴"),
                        CategoryTabItem("jazz", "暗色爵士"),
                        CategoryTabItem("classical", "古典乐"),
                        CategoryTabItem("ambient", "环境音")
                    )
                )
            ),

            // 3. Hero 深度专题卡片 (hero_banner)
            HomeBlockItem(
                id = "block_hero_featured",
                type = HomeBlockType.HERO_BANNER,
                parsedData = HeroBannerData(
                    title = "回响：寻找消失的黑胶灵魂",
                    tag = "深度专题 / DEEP DIVE",
                    summary = "在数字化的浪潮中，我们如何通过物理的震动重新找回听觉的本真？这是一场跨越半个世纪的模拟信号寻访。",
                    imageUrl = "/storage/covers/hero/hero_acoustic_guitar.jpg",
                    articleId = "vinyl_soul",
                    albumId = "forest_echo",
                    albumTitle = "林间碎影",
                    primaryActionText = "阅读专题",
                    secondaryActionText = "聆听专辑"
                )
            ),

            // 4. 快捷功能入口 (quick_actions)
            HomeBlockItem(
                id = "block_quick_actions",
                type = HomeBlockType.QUICK_ACTIONS,
                parsedData = QuickActionsData(
                    title = "随行速达",
                    actions = listOf(
                        QuickActionItem("daily_radar", "每日随心听", icon = "radio", subtitle = "私享雷达", route = "daily_radar"),
                        QuickActionItem("vinyl_radio", "黑胶电台", icon = "vinyl", subtitle = "模拟声场", route = "vinyl_radio"),
                        QuickActionItem("classroom", "音乐教室", icon = "seat", subtitle = "同好座位", route = "classroom"),
                        QuickActionItem("new_charts", "新碟排行榜", icon = "trend", subtitle = "本周热度", route = "new_charts")
                    )
                )
            ),

            // 5. 艺术家标题与网格 (section_title + artist_grid)
            HomeBlockItem(
                id = "block_artist_header",
                type = HomeBlockType.SECTION_TITLE,
                parsedData = SectionTitleData(
                    title = "精选艺术家名录",
                    subtitle = "ARTIST DIRECTORY • 档案库",
                    actionText = "浏览全部",
                    actionRoute = "all_artists"
                )
            ),
            HomeBlockItem(
                id = "block_artist_grid",
                type = HomeBlockType.ARTIST_GRID,
                parsedData = ArtistGridData(
                    title = "精选艺术家名录",
                    actionText = "浏览全部",
                    layout = "grid",
                    artists = listOf(
                        ArtistBlockItem(
                            id = "artist_1",
                            name = "晨曦乐团",
                            genre = "民谣复兴",
                            albumCount = 4,
                            avatarUrl = "/storage/covers/albums/album_forest_track.jpg"
                        ),
                        ArtistBlockItem(
                            id = "artist_2",
                            name = "Blue Note Collective",
                            genre = "暗色爵士",
                            albumCount = 6,
                            avatarUrl = "/storage/covers/albums/album_modern_jazz.jpg"
                        ),
                        ArtistBlockItem(
                            id = "artist_3",
                            name = "巴赫作品集",
                            genre = "古典乐",
                            albumCount = 12,
                            avatarUrl = "/storage/covers/albums/album_classical_piano.jpg"
                        ),
                        ArtistBlockItem(
                            id = "artist_4",
                            name = "静水深流",
                            genre = "环境音",
                            albumCount = 3,
                            avatarUrl = "/storage/covers/albums/album_electronic_vibes.jpg"
                        )
                    )
                )
            ),

            // 6. 今日单曲试听 (section_title + track_list)
            HomeBlockItem(
                id = "block_track_header",
                type = HomeBlockType.SECTION_TITLE,
                parsedData = SectionTitleData(
                    title = "今日单曲试听",
                    subtitle = "DAILY AUDITION • 320K HI-RES",
                    actionText = "全部播放",
                    actionRoute = "play_all_tracks"
                )
            ),
            HomeBlockItem(
                id = "block_track_list",
                type = HomeBlockType.TRACK_LIST,
                parsedData = TrackListData(
                    title = "今日单曲试听",
                    tracks = listOf(
                        TrackBlockItem(
                            id = "track_101",
                            title = "林间碎影",
                            artist = "晨曦乐团",
                            album = "旷野之息",
                            duration = "04:12",
                            coverUrl = "/storage/covers/albums/album_forest_track.jpg",
                            audioUrl = AppConfig.resolveUrl("/storage/audio/demo1.mp3")
                        ),
                        TrackBlockItem(
                            id = "track_102",
                            title = "午夜微醺",
                            artist = "Blue Note Collective",
                            album = "午夜萨克斯",
                            duration = "05:38",
                            coverUrl = "/storage/covers/albums/album_modern_jazz.jpg",
                            audioUrl = AppConfig.resolveUrl("/storage/audio/demo2.mp3")
                        ),
                        TrackBlockItem(
                            id = "track_103",
                            title = "十二平均律 第一前奏曲",
                            artist = "巴赫作品集",
                            album = "十二平均律",
                            duration = "02:45",
                            coverUrl = "/storage/covers/albums/album_classical_piano.jpg",
                            audioUrl = AppConfig.resolveUrl("/storage/audio/demo3.mp3")
                        ),
                        TrackBlockItem(
                            id = "track_104",
                            title = "微光细雨",
                            artist = "静水深流",
                            album = "流动的静谧",
                            duration = "06:20",
                            coverUrl = "/storage/covers/albums/album_electronic_vibes.jpg",
                            audioUrl = AppConfig.resolveUrl("/storage/audio/demo4.mp3")
                        )
                    )
                )
            ),

            // 7. 杂志摄影视觉大图 (image_feature)
            HomeBlockItem(
                id = "block_image_feature",
                type = HomeBlockType.IMAGE_FEATURE,
                parsedData = ImageFeatureData(
                    imageUrl = "/storage/covers/hero/hero_acoustic_guitar.jpg",
                    caption = "静谧之声 / SOUND OF SILENCE",
                    author = "Photo by Songbook Studio • 1974 模拟胶片复刻",
                    aspectRatio = 1.78f,
                    targetRoute = "image_feature_detail"
                )
            ),

            // 8. 时代留声机 / 档案卡片 (section_title + archive_card)
            HomeBlockItem(
                id = "block_archive_header",
                type = HomeBlockType.SECTION_TITLE,
                parsedData = SectionTitleData(
                    title = "时代留声机",
                    subtitle = "RETROSPECTIVE & ARCHIVES",
                    actionText = "进入展馆",
                    actionRoute = "archive_gallery"
                )
            ),
            HomeBlockItem(
                id = "block_archive_card",
                type = HomeBlockType.ARCHIVE_CARD,
                parsedData = ArchiveCardData(
                    id = "archive_hebei",
                    title = "冀西南林家铺子",
                    subtitle = "万能青年旅店 • 2020",
                    description = "当代华语独立摇滚里程碑之作。在管乐与电声交织的迷宫中，记录华北大地的阵痛与诗意沉思。",
                    imageUrl = "/storage/covers/albums/album_hebei_kirin.jpg",
                    badge = "典藏黑胶",
                    targetRoute = "album_detail_hebei"
                )
            ),

            // 9. 选集随笔 (essay_card)
            HomeBlockItem(
                id = "block_essay_card",
                type = HomeBlockType.ESSAY_CARD,
                parsedData = EssayCardData(
                    title = "选集随笔",
                    essays = listOf(
                        EssayItemData(
                            id = "essay_1",
                            date = "OCT 22, 2023",
                            title = "当我们在听莫扎特时，我们在想些什么？",
                            summary = "不仅仅是旋律的优美，更是一种秩序的重建。莫扎特的音乐中隐藏着宇宙最初的几何结构。每当我们感到世界的混乱，那一组组跳跃的音符就像是精准的坐标..."
                        ),
                        EssayItemData(
                            id = "essay_2",
                            date = "OCT 19, 2023",
                            title = "木吉他：一种触觉的乡愁",
                            summary = "指尖划过琴弦的声音，是数字化永远无法模拟的颗粒感。那是一种带着温度的物理接触，是树木干枯后依旧在空气中颤动的生命。每一把吉他都有它独特的性格..."
                        ),
                        EssayItemData(
                            id = "essay_3",
                            date = "OCT 15, 2023",
                            title = "声波中的建筑学：论环境音乐与空间构筑",
                            summary = "当旋律退居为背景，空间本身的质感便浮现出来。声音不再是填补沉默的实体，而是丈量光影与空气流动的一把隐形标尺..."
                        )
                    )
                )
            )
        )
    }
}
