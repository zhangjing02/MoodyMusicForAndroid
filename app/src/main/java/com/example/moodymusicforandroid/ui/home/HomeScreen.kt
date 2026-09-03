package com.example.moodymusicforandroid.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.data.model.*
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.home.components.*
import com.example.moodymusicforandroid.ui.home.viewmodel.HomeViewModel
import com.example.moodymusicforandroid.ui.theme.SongbookColors

import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

/**
 * 现代颂歌 (The Modern Songbook) 首页
 *
 * 采用 Block-Based SDUI (切片化动态首页流) 解析与多布局渲染架构。
 * 支持 9 大多态切片结构：
 * 1. hero_banner -> HeroFeaturedCard (深度专题大卡)
 * 2. category_tabs -> CategoryTabsBlock (分类标签切换)
 * 3. section_title -> SectionTitleBlock (刊头与章节大标题)
 * 4. quick_actions -> QuickActionsBlock (随行速达功能入口)
 * 5. artist_grid -> ArtistGridBlock (艺术家双列网格)
 * 6. track_list -> TrackListBlock (单曲高质试听列表)
 * 7. image_feature -> ImageFeatureBlock (杂志摄影视觉大图)
 * 8. archive_card -> ArchiveCardBlock (时代留声机/典藏黑胶)
 * 9. essay_card -> EssaysSection (文艺选集随笔)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    onMenuClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onAlbumClick: (String, String) -> Unit = { _, _ -> },
    onArtistClick: (String, String) -> Unit = { _, _ -> },
    onArticleClick: (String) -> Unit = {}
) {
    val feedItems by viewModel.homeFeedItems.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val pullToRefreshState = rememberPullToRefreshState()
    val listState = rememberLazyListState()
    val statusBarTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.fetchHomeFeed() },
        state = pullToRefreshState,
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        indicator = {
            Indicator(
                state = pullToRefreshState,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = statusBarTop + 6.dp),
                isRefreshing = isRefreshing,
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                color = SongbookColors.BurntOrange
            )
        }
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(
                top = statusBarTop + 6.dp,
                bottom = 140.dp
            )
        ) {
        // 1. 杂志固定刊头栏 (Society Weekly Top Bar)
        item(key = "society_weekly_top_bar") {
            SocietyWeeklyTopBar(
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. 动态切片流 (Block-Based SDUI Items) - 细粒度分帧解构
        feedItems.forEach { block ->
            when (block.type) {
                HomeBlockType.HERO_BANNER -> {
                    item(key = block.id, contentType = block.type) {
                        val hero = block.parsedData as? HeroBannerData ?: block.toHeroBanner()
                        HeroFeaturedCard(
                            title = hero.title,
                            tag = hero.tag,
                            summary = hero.summary,
                            imageUrl = hero.imageUrl,
                            primaryActionText = hero.primaryActionText,
                            secondaryActionText = hero.secondaryActionText,
                            onReadArticleClick = { onArticleClick(hero.articleId) },
                            onPlayAlbumClick = { onAlbumClick(hero.albumId, hero.albumTitle) }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                HomeBlockType.CATEGORY_TABS -> {
                    item(key = block.id, contentType = block.type) {
                        val tabsData = block.parsedData as? CategoryTabsData ?: block.toCategoryTabs()
                        CategoryTabsBlock(
                            data = tabsData,
                            onTabSelect = { categoryId -> viewModel.selectCategory(categoryId) }
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }

                HomeBlockType.SECTION_TITLE -> {
                    item(key = block.id, contentType = block.type) {
                        val titleData = block.parsedData as? SectionTitleData ?: block.toSectionTitle()
                        SectionTitleBlock(
                            data = titleData,
                            onActionClick = { route ->
                                when (route) {
                                    "all_artists" -> onArtistClick("all", "精选艺术家")
                                    "play_all_tracks" -> onAlbumClick("daily_tracks", "今日单曲集")
                                    "archive_gallery" -> onAlbumClick("archive_gallery", "时代留声机")
                                    else -> onAlbumClick("society_weekly", "SOCIETY WEEKLY")
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                HomeBlockType.ARTIST_GRID -> {
                    val artistData = block.parsedData as? ArtistGridData ?: block.toArtistGrid()
                    for (i in artistData.artists.indices step 2) {
                        val left = artistData.artists[i]
                        val right = artistData.artists.getOrNull(i + 1)
                        item(key = "artist_pair_${left.id}_${right?.id ?: ""}", contentType = "artist_row") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                ArtistGridCard(
                                    artist = left,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onArtistClick(left.id, left.name) }
                                )
                                if (right != null) {
                                    ArtistGridCard(
                                        artist = right,
                                        modifier = Modifier.weight(1f),
                                        onClick = { onArtistClick(right.id, right.name) }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                    item(key = "artist_bottom_space_${block.id}") {
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                HomeBlockType.TRACK_LIST -> {
                    val trackData = block.parsedData as? TrackListData ?: block.toTrackList()
                    items(
                        items = trackData.tracks,
                        key = { "track_${it.id}" },
                        contentType = { "track_item" }
                    ) { track ->
                        val index = trackData.tracks.indexOf(track) + 1
                        TrackListItemCard(
                            index = index,
                            track = track,
                            onClick = { onAlbumClick(track.id, track.title) },
                            onPlayToggle = { viewModel.toggleTrackPlay(track.id) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item(key = "track_bottom_space_${block.id}") {
                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }

                HomeBlockType.ARCHIVE_CARD -> {
                    item(key = block.id, contentType = block.type) {
                        val archiveData = block.parsedData as? ArchiveCardData ?: block.toArchiveCard()
                        ArchiveCardBlock(
                            data = archiveData,
                            onClick = { onAlbumClick(archiveData.id, archiveData.title) }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                HomeBlockType.IMAGE_FEATURE -> {
                    item(key = block.id, contentType = block.type) {
                        val imageData = block.parsedData as? ImageFeatureData ?: block.toImageFeature()
                        ImageFeatureBlock(
                            data = imageData,
                            onClick = { onArticleClick("visual_feature") }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                HomeBlockType.ESSAY_CARD -> {
                    val essayData = block.parsedData as? EssayCardData ?: block.toEssayCard()
                    item(key = "essay_header_${block.id}", contentType = "essay_header") {
                        Text(
                            text = essayData.title,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 20.dp)
                        )
                    }
                    items(
                        items = essayData.essays,
                        key = { "essay_${it.id}" },
                        contentType = { "essay_item" }
                    ) { essay ->
                        EssayItem(
                            essay = essay,
                            onClick = { onArticleClick(essay.id) }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                HomeBlockType.QUICK_ACTIONS -> {
                    item(key = block.id, contentType = block.type) {
                        val actionsData = block.parsedData as? QuickActionsData ?: block.toQuickActions()
                        QuickActionsBlock(
                            data = actionsData,
                            onActionClick = { action ->
                                when (action.id) {
                                    "classroom" -> onAlbumClick("classroom", "音乐教室")
                                    "vinyl_radio" -> onAlbumClick("vinyl_radio", "黑胶电台")
                                    "daily_radar" -> onAlbumClick("daily_radar", "每日随心听")
                                    "new_charts" -> onAlbumClick("new_charts", "新碟排行榜")
                                    else -> onAlbumClick(action.id, action.title)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }

                else -> {
                    // 具备向后兼容性，优雅忽略未知切片类型
                }
            }
        }
    }
}
}

/**
 * 杂志刊头 TopBar 组件
 */
@Composable
private fun SocietyWeeklyTopBar(
    onMenuClick: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 2.dp, bottom = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧抽屉菜单按钮
        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = SongbookColors.BurntOrange
            )
        }

        // 中间刊头大字 (Serif Italic)
        Text(
            text = "SOCIETY WEEKLY",
            style = MaterialTheme.typography.headlineMedium,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            letterSpacing = 1.sp
        )

        // 右侧头像
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                .clickable { onAvatarClick() }
        ) {
            SongbookImage(
                model = "/storage/avatars/user_avatar_default.jpg",
                contentDescription = "User Avatar",
                fallbackRes = R.drawable.user_avatar_default,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
