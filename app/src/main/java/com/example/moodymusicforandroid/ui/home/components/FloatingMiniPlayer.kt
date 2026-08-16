package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.components.SongbookBlurContainer
import com.example.moodymusicforandroid.ui.components.SongbookImage
import com.example.moodymusicforandroid.ui.theme.SongbookColors


/**
 * 全局悬浮迷你播放器组件 (FloatingMiniPlayer)
 *
 * 特性：
 * 1. 浮动在底栏上方，具备纸质高光质感与微妙漫反射阴影；
 * 2. 黑胶圆形封面（播放时带旋转动效与唱片同心圆暗纹）；
 * 3. 极简曲目信息与焦橙色播放控制按钮。
 */
@Composable
fun FloatingMiniPlayer(
    modifier: Modifier = Modifier,
    trackTitle: String = "苔藓上的私语",
    artistName: String = "周深处 & 森林合唱团",
    coverUrl: String = "https://m-api.changgepd.top/storage/covers/albums/album__1_2.jpg",
    isPlaying: Boolean = true,
    onPlayerClick: () -> Unit = {},
    onPlayPauseClick: () -> Unit = {},
    onPreviousClick: () -> Unit = {},
    onNextClick: () -> Unit = {}
) {
    // 黑胶旋转微动效
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    SongbookBlurContainer(
        modifier = modifier
            .fillMaxWidth(0.92f)
            .height(60.dp)
            .clickable { onPlayerClick() },
        cornerRadius = 16.dp,
        overlayColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
        borderColor = SongbookColors.GhostBorder,
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 黑胶唱片圆形缩略图
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1B1C1A))
                    .border(1.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .rotate(if (isPlaying) rotation else 0f)
                ) {
                    SongbookImage(
                        model = coverUrl,
                        contentDescription = "Playing track cover",
                        fallbackRes = R.drawable.album_forest_track,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 曲目与艺术家信息
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = trackTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = artistName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 轻量播放控制操作按钮
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(
                    onClick = onPreviousClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    PreviousTrackIcon(tint = MaterialTheme.colorScheme.onSurface)
                }

                IconButton(
                    onClick = onPlayPauseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SongbookColors.BurntOrange)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = onNextClick,
                    modifier = Modifier.size(32.dp)
                ) {
                    NextTrackIcon(tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
private fun PreviousTrackIcon(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        scale(scaleX, scaleY, pivot = Offset.Zero) {
            val barPath = Path().apply {
                moveTo(6f, 6f)
                lineTo(8f, 6f)
                lineTo(8f, 18f)
                lineTo(6f, 18f)
                close()
            }
            val trianglePath = Path().apply {
                moveTo(18f, 6f)
                lineTo(8.5f, 12f)
                lineTo(18f, 18f)
                close()
            }
            drawPath(path = barPath, color = tint)
            drawPath(path = trianglePath, color = tint)
        }
    }
}

@Composable
private fun NextTrackIcon(tint: Color) {
    Canvas(modifier = Modifier.size(18.dp)) {
        val scaleX = size.width / 24f
        val scaleY = size.height / 24f
        scale(scaleX, scaleY, pivot = Offset.Zero) {
            val trianglePath = Path().apply {
                moveTo(6f, 6f)
                lineTo(15.5f, 12f)
                lineTo(6f, 18f)
                close()
            }
            val barPath = Path().apply {
                moveTo(16f, 6f)
                lineTo(18f, 6f)
                lineTo(18f, 18f)
                lineTo(16f, 18f)
                close()
            }
            drawPath(path = trianglePath, color = tint)
            drawPath(path = barPath, color = tint)
        }
    }
}
