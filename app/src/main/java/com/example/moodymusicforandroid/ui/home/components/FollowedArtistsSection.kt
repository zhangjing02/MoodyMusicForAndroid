package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.theme.SongbookColors

/**
 * 关注艺人横向滚动展示组件 (FollowedArtistsSection)
 * 采用杂志圆形头像与细边框
 */
@Composable
fun FollowedArtistsSection(
    modifier: Modifier = Modifier,
    onArtistClick: (String, String) -> Unit = { _, _ -> },
    onBrowseAllClick: () -> Unit = {}
) {
    val artists = listOf(
        FollowedArtistItem("李健", "https://m-api.changgepd.top/storage/artists/artist_1.jpg", R.drawable.hero_acoustic_guitar),
        FollowedArtistItem("陈绮贞", "https://m-api.changgepd.top/storage/artists/artist_beatrice.jpg", R.drawable.artist_beatrice),
        FollowedArtistItem("万能青年旅店", "https://m-api.changgepd.top/storage/artists/artist_charlie.jpg", R.drawable.artist_charlie),
        FollowedArtistItem("坂本龍一", "https://m-api.changgepd.top/storage/artists/artist_5.jpg", R.drawable.album_classical_piano),
        FollowedArtistItem("落日飞车", "https://m-api.changgepd.top/storage/covers/albums/album__3_6.jpg", R.drawable.album_modern_jazz)
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
                text = "关注的艺术家",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "BROWSE ALL",
                style = MaterialTheme.typography.labelSmall,
                color = SongbookColors.BurntOrange,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBrowseAllClick() }
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(artists) { artist ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onArtistClick(artist.name, artist.name) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                            .border(1.5.dp, SongbookColors.MutedOlive.copy(alpha = 0.3f), CircleShape)
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                        ) {
                            SongbookImage(
                                model = artist.imageUrl,
                                contentDescription = artist.name,
                                fallbackRes = artist.fallbackRes,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = artist.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

data class FollowedArtistItem(
    val name: String,
    val imageUrl: String,
    val fallbackRes: Int
)
