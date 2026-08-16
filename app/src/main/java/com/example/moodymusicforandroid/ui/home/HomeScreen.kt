package com.example.moodymusicforandroid.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp) // 预留底部浮动 MiniPlayer 和 Dock 空间
    ) {
        // 1. 杂志固定刊头栏 (Society Weekly Top Bar)
        item(key = "society_weekly_top_bar") {
            SocietyWeeklyTopBar(
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 2. 动态切片流 (Block-Based SDUI Items)
        items(feedItems, key = { it.id }) { block ->
            when (block.type) {
                HomeBlockType.HERO_BANNER -> {
                    val hero = block.toHeroBanner()
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

                HomeBlockType.CATEGORY_TABS -> {
                    val tabsData = block.toCategoryTabs()
                    CategoryTabsBlock(
                        data = tabsData,
                        onTabSelect = { categoryId -> viewModel.selectCategory(categoryId) }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                HomeBlockType.SECTION_TITLE -> {
                    val titleData = block.toSectionTitle()
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

                HomeBlockType.ESSAY_CARD -> {
                    val essayData = block.toEssayCard()
                    EssaysSection(
                        title = essayData.title,
                        essays = essayData.essays,
                        onArticleClick = onArticleClick
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                HomeBlockType.ARTIST_GRID -> {
                    val artistData = block.toArtistGrid()
                    ArtistGridBlock(
                        data = artistData,
                        onArtistClick = onArtistClick
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                HomeBlockType.TRACK_LIST -> {
                    val trackData = block.toTrackList()
                    TrackListBlock(
                        data = trackData,
                        onTrackClick = { track -> onAlbumClick(track.id, track.title) },
                        onPlayToggle = { trackId -> viewModel.toggleTrackPlay(trackId) }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                HomeBlockType.ARCHIVE_CARD -> {
                    val archiveData = block.toArchiveCard()
                    ArchiveCardBlock(
                        data = archiveData,
                        onClick = { onAlbumClick(archiveData.id, archiveData.title) }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                HomeBlockType.IMAGE_FEATURE -> {
                    val imageData = block.toImageFeature()
                    ImageFeatureBlock(
                        data = imageData,
                        onClick = { onArticleClick("visual_feature") }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                }

                HomeBlockType.QUICK_ACTIONS -> {
                    val actionsData = block.toQuickActions()
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

                else -> {
                    // 具备向后兼容性，优雅忽略未知切片类型
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
            .padding(vertical = 8.dp),
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
                model = "https://m-api.changgepd.top/storage/avatars/user_avatar_default.jpg",
                contentDescription = "User Avatar",
                fallbackRes = R.drawable.user_avatar_default,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
