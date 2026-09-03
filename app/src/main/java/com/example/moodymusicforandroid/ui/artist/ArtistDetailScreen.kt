package com.example.moodymusicforandroid.ui.artist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.theme.SongbookColors

/**
 * 艺术家主页 (Artist Detail Screen)
 *
 * 特色：
 * 1. 阿比盖尔·陈巨幅肖像立绘与 Contemporary Folk 杂志风角标；
 * 2. 气势磅礴的衬线姓名排版与生平导读；
 * 3. 月收听人数与精选作品数据面板；
 * 4. 作品全集排序 Tab 与双列唱片封面网格。
 */
@Composable
fun ArtistDetailScreen(
    artistId: String = "abigail_chen",
    artistName: String = "阿比盖尔·陈",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onAlbumClick: (String, String) -> Unit = { _, _ -> },
    onPlayAllClick: () -> Unit = {}
) {
    var isFollowing by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("按时间排序", "按热度排序", "录音室专辑")

    val discography = listOf(
        ArtistAlbumItem(
            id = "afternoon_echo",
            title = "午后的回声",
            yearAndTracks = "2024 • 12 TRACKS",
            imageUrl = "/storage/covers/albums/album__2_8.jpg",
            fallbackRes = R.drawable.album_afternoon_echo
        ),
        ArtistAlbumItem(
            id = "wild_roam",
            title = "荒野散策",
            yearAndTracks = "2023 • 10 TRACKS",
            imageUrl = "/storage/covers/albums/album__2_3.jpg",
            fallbackRes = R.drawable.album_wild_roam
        ),
        ArtistAlbumItem(
            id = "blue_monsoon",
            title = "蓝色季候风",
            yearAndTracks = "2022 • 14 TRACKS",
            imageUrl = "/storage/covers/albums/album__2_4.jpg",
            fallbackRes = R.drawable.album_blue_monsoon
        ),
        ArtistAlbumItem(
            id = "forest_mist",
            title = "沉默的见证",
            yearAndTracks = "2021 • 8 TRACKS",
            imageUrl = "/storage/covers/hero/hero_forest_mist.jpg",
            fallbackRes = R.drawable.hero_forest_mist
        ),
        ArtistAlbumItem(
            id = "stone_poem",
            title = "石上的诗篇",
            yearAndTracks = "2020 • 11 TRACKS",
            imageUrl = "/storage/covers/albums/album_electronic_vibes.jpg",
            fallbackRes = R.drawable.album_electronic_vibes
        ),
        ArtistAlbumItem(
            id = "rainy_talk",
            title = "雨夜谈话",
            yearAndTracks = "2019 • 13 TRACKS",
            imageUrl = "/storage/covers/albums/album__2_7.jpg",
            fallbackRes = R.drawable.album_rainy_talk
        )
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = SongbookColors.BurntOrange
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "艺术家",
                    style = MaterialTheme.typography.titleLarge,
                    color = SongbookColors.BurntOrange,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // 1. 艺术家巨幅肖像与标签
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    SongbookImage(
                        model = "/storage/artists/artist_abigail_chen.jpg",
                        contentDescription = artistName,
                        fallbackRes = R.drawable.artist_abigail_chen,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 底部黑色渐变与风格标签
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Text(
                            text = "CONTEMPORARY FOLK",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. 艺术家宏伟衬线大名与导言
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Text(
                        text = artistName,
                        style = MaterialTheme.typography.displayMedium,
                        color = SongbookColors.BurntOrange,
                        fontWeight = FontWeight.Black
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "在极简主义与叙事民谣的交汇点，阿比盖尔·陈以其独特的“现代颂歌”风格重新定义了独立乐坛。她的作品如同一本散发着墨香的旧乐谱，在喧嚣的数字时代提供了一片宁静的听觉绿洲。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 统计数据
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        Column {
                            Text(
                                text = "月收听人数",
                                style = MaterialTheme.typography.labelSmall,
                                color = SongbookColors.Outline,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "2,840,192",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                text = "精选作品",
                                style = MaterialTheme.typography.labelSmall,
                                color = SongbookColors.Outline,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "12 Albums",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 关注与播放按钮组
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { isFollowing = !isFollowing },
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isFollowing) SongbookColors.MutedOlive else SongbookColors.BurntOrange,
                                contentColor = Color.White
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Icon(
                                imageVector = if (isFollowing) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFollowing) "已关注" else "关注歌手",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        Button(
                            onClick = onPlayAllClick,
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 12.dp)
                        ) {
                            Text(
                                text = "播放全部",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(36.dp))
            }

            // 3. 作品全集标题与 Tab 排序
            item {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "作品全集",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            tabs.forEachIndexed { index, tabName ->
                                val isSelected = selectedTab == index
                                Text(
                                    text = tabName,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) SongbookColors.BurntOrange else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.clickable { selectedTab = index }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = SongbookColors.OutlineVariant.copy(alpha = 0.25f))
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }

            // 4. 双列专辑网格
            item {
                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    for (i in discography.indices step 2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val albumLeft = discography[i]
                            val albumRight = discography.getOrNull(i + 1)

                            ArtistAlbumCard(
                                album = albumLeft,
                                modifier = Modifier.weight(1f),
                                onClick = { onAlbumClick(albumLeft.id, albumLeft.title) }
                            )

                            if (albumRight != null) {
                                ArtistAlbumCard(
                                    album = albumRight,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onAlbumClick(albumRight.id, albumRight.title) }
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistAlbumCard(
    album: ArtistAlbumItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(4.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            SongbookImage(
                model = album.imageUrl,
                contentDescription = album.title,
                fallbackRes = album.fallbackRes,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = album.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = album.yearAndTracks,
            style = MaterialTheme.typography.labelSmall,
            color = SongbookColors.Outline,
            maxLines = 1
        )
    }
}

data class ArtistAlbumItem(
    val id: String,
    val title: String,
    val yearAndTracks: String,
    val imageUrl: String,
    val fallbackRes: Int
)
