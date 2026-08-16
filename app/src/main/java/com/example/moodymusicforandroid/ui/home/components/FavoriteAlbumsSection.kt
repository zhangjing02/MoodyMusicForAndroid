package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.theme.SongbookColors

/**
 * 现代颂歌 收藏专辑展示区域组件 (FavoriteAlbumsSection)
 * 采用不对称剪贴画风格 (Scrapbook Collage Style)
 */
@Composable
fun FavoriteAlbumsSection(
    modifier: Modifier = Modifier,
    onAlbumClick: (String, String) -> Unit = { _, _ -> }
) {
    val collectedAlbums = listOf(
        CollectedAlbumItem("冀西南林家铺子", "万能青年旅店 • 2020", "https://m-api.changgepd.top/storage/covers/albums/album_hebei_kirin.jpg", R.drawable.album_hebei_kirin),
        CollectedAlbumItem("时间的歌", "陈绮贞 • 2013", "https://m-api.changgepd.top/storage/covers/albums/album_time_song.jpg", R.drawable.album_time_song),
        CollectedAlbumItem("Bossa Nova", "落日飞车 • 2011", "https://m-api.changgepd.top/storage/covers/albums/album_modern_jazz.jpg", R.drawable.album_modern_jazz),
        CollectedAlbumItem("12", "坂本龍一 • 2023", "https://m-api.changgepd.top/storage/covers/albums/album_blue_monsoon.jpg", R.drawable.album_blue_monsoon)
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "收藏的专辑",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "324 份收藏",
                style = MaterialTheme.typography.labelSmall,
                color = SongbookColors.Outline
            )
        }

        // 双列卡片网格
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            for (i in collectedAlbums.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val left = collectedAlbums[i]
                    val right = collectedAlbums.getOrNull(i + 1)

                    CollectedAlbumCard(
                        item = left,
                        modifier = Modifier.weight(1f),
                        onClick = { onAlbumClick(left.title, left.title) }
                    )

                    if (right != null) {
                        CollectedAlbumCard(
                            item = right,
                            modifier = Modifier.weight(1f),
                            onClick = { onAlbumClick(right.title, right.title) }
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun CollectedAlbumCard(
    item: CollectedAlbumItem,
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
                model = item.imageUrl,
                contentDescription = item.title,
                fallbackRes = item.fallbackRes,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = item.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = SongbookColors.Outline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

data class CollectedAlbumItem(
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val fallbackRes: Int
)
