package com.example.moodymusicforandroid.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.home.components.EditorPicksGrid
import com.example.moodymusicforandroid.ui.home.components.EssaysSection
import com.example.moodymusicforandroid.ui.home.components.HeroFeaturedCard
import com.example.moodymusicforandroid.ui.theme.SongbookColors
import com.example.moodymusicforandroid.ui.theme.SongbookSpacing

/**
 * 现代颂歌 (The Modern Songbook) 首页
 *
 * 模块组成：
 * 1. 杂志刊头 (SOCIETY WEEKLY & 今日精选/期号)
 * 2. Hero 深度专题卡片 (《回响：寻找消失的黑胶灵魂》)
 * 3. 编辑精选 2×2 错落网格 (旷野之息、午夜萨克斯、十二平均律、流动的静谧)
 * 4. 选集随笔文艺列表 (时间轴与文学导读)
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onMenuClick: () -> Unit = {},
    onAvatarClick: () -> Unit = {},
    onAlbumClick: (String, String) -> Unit = { _, _ -> },
    onArtistClick: (String, String) -> Unit = { _, _ -> },
    onArticleClick: (String) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 140.dp) // 预留底部浮动 MiniPlayer 和 Dock 空间
    ) {
        // 1. 杂志刊头栏 (Society Weekly Top Bar)
        item {
            SocietyWeeklyTopBar(
                onMenuClick = onMenuClick,
                onAvatarClick = onAvatarClick
            )
            Spacer(modifier = Modifier.height(28.dp))
        }

        // 2. 今日精选主标题与期号
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "今日精选",
                    style = MaterialTheme.typography.displayMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "10月24日，周一 • 第 82 期",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                    letterSpacing = 2.sp
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Hero 深度专题卡片
        item {
            HeroFeaturedCard(
                onReadArticleClick = { onArticleClick("vinyl_soul") },
                onPlayAlbumClick = { onAlbumClick("forest_echo", "林间碎影") }
            )
            Spacer(modifier = Modifier.height(36.dp))
        }

        // 4. 编辑精选 2×2 网格
        item {
            EditorPicksGrid(
                onAlbumClick = { albumId, albumTitle -> onAlbumClick(albumId, albumTitle) },
                onBrowseAllClick = { onAlbumClick("all_albums", "精选唱片集") }
            )
            Spacer(modifier = Modifier.height(56.dp))
        }

        // 5. 选集随笔列表
        item {
            EssaysSection(
                onArticleClick = onArticleClick
            )
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
