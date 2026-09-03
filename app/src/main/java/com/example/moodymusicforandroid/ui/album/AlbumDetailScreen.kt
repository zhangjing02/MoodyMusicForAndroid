package com.example.moodymusicforandroid.ui.album

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.theme.SongbookColors

/**
 * 专辑曲目模型
 */
data class TrackItemData(
    val trackNumber: String,
    val title: String,
    val duration: String,
    val isPlaying: Boolean = false
)

/**
 * 专辑详情页 (The Songbook - Album Detail Screen)
 *
 * 特色：
 * 1. 沉浸式《林间碎影》巨幅海报与纸质渐变过渡；
 * 2. 负 Margin 错位重叠的大标题、双色衬线与录音手记；
 * 3. 极简呼吸感曲目列表与播放中动态音频均衡器 (Equalizer) 微动效；
 * 4. 实体黑胶唱片质感。
 */
@Composable
fun AlbumDetailScreen(
    albumId: String = "forest_echo",
    albumTitle: String = "林间碎影",
    artistName: String = "周深处 & 森林合唱团",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onTrackClick: (TrackItemData) -> Unit = {},
    onPlayAllClick: () -> Unit = {}
) {
    var playingTrackIndex by remember { mutableStateOf(1) } // 默认第2首播放中

    val tracks = remember {
        listOf(
            TrackItemData("01", "晨露中的第一道光", "04:12"),
            TrackItemData("02", "苔藓上的私语", "03:45"),
            TrackItemData("03", "无名鸟的迁徙", "05:20"),
            TrackItemData("04", "山间骤雨", "04:08"),
            TrackItemData("05", "归途之风", "04:50"),
            TrackItemData("06", "落叶与溪流", "03:32"),
            TrackItemData("07", "星光下的守望", "04:15"),
            TrackItemData("08", "原木的回声", "03:58")
        )
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                Color.Transparent
                            )
                        )
                    )
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
                    text = "专辑",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // 1. 巨幅沉浸式海报与纸质渐变过渡
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                ) {
                    SongbookImage(
                        model = "/storage/covers/hero/hero_forest_mist.jpg",
                        contentDescription = "林间碎影",
                        fallbackRes = R.drawable.hero_forest_mist,
                        modifier = Modifier.fillMaxSize()
                    )

                    // 纸质底色底部渐变晕染
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Transparent,
                                        MaterialTheme.colorScheme.background.copy(alpha = 0.6f),
                                        MaterialTheme.colorScheme.background
                                    )
                                )
                            )
                    )
                }
            }

            // 2. 杂志排版专辑信息与手记
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .padding(horizontal = 24.dp)
                ) {
                    // 档案元数据
                    Text(
                        text = "FOLK ARCHIVES • 2024",
                        style = MaterialTheme.typography.labelSmall,
                        color = SongbookColors.MutedOlive,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 大标题（双色衬线：林间 + 焦橙斜体 碎影）
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "林间",
                            style = MaterialTheme.typography.displayLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "碎影",
                            style = MaterialTheme.typography.displayLarge,
                            color = SongbookColors.BurntOrange,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 艺术家
                    Text(
                        text = artistName,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 录音手记
                    Text(
                        text = "这张专辑是在深秋的秦岭森林深处录制的。我们搬运了三台开盘式录音机，以及一些简单的原声乐器。你可以听到清晨鸟鸣的律动，溪流在岩石间撞击的低频，甚至能捕捉到风穿过针叶林的沙沙声。这是一场关于时间流逝与自然共鸣的对话，没有任何电子润色，只有木头与金属最原始的振颤。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 「全部播放」按钮
                    Button(
                        onClick = onPlayAllClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SongbookColors.BurntOrange,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "全部播放",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. 曲目列表头
            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "曲目目录",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "10 TRACKS • 42:15",
                            style = MaterialTheme.typography.labelSmall,
                            color = SongbookColors.Outline,
                            letterSpacing = 1.sp
                        )
                    }
                    HorizontalDivider(color = SongbookColors.OutlineVariant.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // 4. 曲目列表项 (呼吸感间距，无生硬分割线)
            itemsIndexed(tracks) { index, track ->
                val isCurrentPlaying = playingTrackIndex == index
                TrackRowItem(
                    track = track,
                    isPlaying = isCurrentPlaying,
                    onClick = {
                        playingTrackIndex = index
                        onTrackClick(track)
                    }
                )
            }
        }
    }
}

/**
 * 呼吸感曲目行组件
 */
@Composable
private fun TrackRowItem(
    track: TrackItemData,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 曲目序号
        Text(
            text = track.trackNumber,
            style = MaterialTheme.typography.labelMedium,
            color = if (isPlaying) SongbookColors.BurntOrange else SongbookColors.Outline.copy(alpha = 0.5f),
            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.width(32.dp)
        )

        // 歌曲名称
        Text(
            text = track.title,
            style = MaterialTheme.typography.bodyLarge,
            color = if (isPlaying) SongbookColors.BurntOrange else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isPlaying) FontWeight.Bold else FontWeight.Normal,
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )

        // 右侧：播放中动态均衡器动效 OR 更多操作图标
        if (isPlaying) {
            AnimatedEqualizer(tint = SongbookColors.BurntOrange)
        } else {
            Text(
                text = track.duration,
                style = MaterialTheme.typography.bodySmall,
                color = SongbookColors.Outline.copy(alpha = 0.4f),
                modifier = Modifier.padding(end = 8.dp)
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = SongbookColors.Outline.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

/**
 * 均衡器跳动微动画组件
 */
@Composable
fun AnimatedEqualizer(
    tint: Color,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer")

    val h1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h1"
    )
    val h2 by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h2"
    )
    val h3 by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "h3"
    )

    Canvas(modifier = modifier.size(width = 20.dp, height = 18.dp)) {
        val barWidth = 3.dp.toPx()
        val spacing = 3.dp.toPx()
        val maxHeight = size.height

        // Bar 1
        val b1Height = maxHeight * h1
        drawRoundRect(
            color = tint,
            topLeft = Offset(0f, maxHeight - b1Height),
            size = Size(barWidth, b1Height),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Bar 2
        val b2Height = maxHeight * h2
        drawRoundRect(
            color = tint,
            topLeft = Offset(barWidth + spacing, maxHeight - b2Height),
            size = Size(barWidth, b2Height),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Bar 3
        val b3Height = maxHeight * h3
        drawRoundRect(
            color = tint,
            topLeft = Offset((barWidth + spacing) * 2, maxHeight - b3Height),
            size = Size(barWidth, b3Height),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
    }
}
