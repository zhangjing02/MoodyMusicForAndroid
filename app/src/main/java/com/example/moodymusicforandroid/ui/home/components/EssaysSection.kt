package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.ui.theme.SongbookColors

/**
 * 随笔文章数据模型
 */
data class EssayArticle(
    val id: String,
    val date: String,
    val title: String,
    val summary: String
)

/**
 * 首页文艺随笔列表组件 (EssaysSection)
 * 采用左侧细线时间轴、衬线大标题与呼吸感排版，呈现人文杂志阅读深度。
 */
@Composable
fun EssaysSection(
    modifier: Modifier = Modifier,
    onArticleClick: (String) -> Unit = {}
) {
    val essays = listOf(
        EssayArticle(
            id = "essay_1",
            date = "OCT 22, 2023",
            title = "当我们在听莫扎特时，我们在想些什么？",
            summary = "不仅仅是旋律的优美，更是一种秩序的重建。莫扎特的音乐中隐藏着宇宙最初的几何结构。每当我们感到世界的混乱，那一组组跳跃的音符就像是精准的坐标..."
        ),
        EssayArticle(
            id = "essay_2",
            date = "OCT 19, 2023",
            title = "木吉他：一种触觉的乡愁",
            summary = "指尖划过琴弦的声音，是数字化永远无法模拟的颗粒感。那是一种带着温度的物理接触，是树木干枯后依旧在空气中颤动的生命。每一把吉他都有它独特的性格..."
        ),
        EssayArticle(
            id = "essay_3",
            date = "OCT 15, 2023",
            title = "声波中的建筑学：论环境音乐与空间构筑",
            summary = "当旋律退居为背景，空间本身的质感便浮现出来。声音不再是填补沉默的实体，而是丈量光影与空气流动的一把隐形标尺..."
        )
    )

    Column(modifier = modifier.fillMaxWidth()) {
        // 栏目标题
        Text(
            text = "选集随笔",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        // 随笔文章列表
        Column(
            verticalArrangement = Arrangement.spacedBy(28.dp)
        ) {
            essays.forEach { essay ->
                EssayItem(
                    essay = essay,
                    onClick = { onArticleClick(essay.id) }
                )
            }
        }
    }
}

@Composable
private fun EssayItem(
    essay: EssayArticle,
    onClick: () -> Unit
) {
    val timelineColor = SongbookColors.BurntOrange.copy(alpha = 0.25f)
    val dotColor = SongbookColors.BurntOrange

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        // 左侧时间线与圆点
        Box(
            modifier = Modifier
                .width(24.dp)
                .fillMaxHeight()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // 绘制左侧垂直线
                drawLine(
                    color = timelineColor,
                    start = Offset(x = 6.dp.toPx(), y = 0f),
                    end = Offset(x = 6.dp.toPx(), y = size.height),
                    strokeWidth = 1.dp.toPx()
                )
                // 绘制顶部起始圆点
                drawCircle(
                    color = dotColor,
                    radius = 3.5.dp.toPx(),
                    center = Offset(x = 6.dp.toPx(), y = 8.dp.toPx())
                )
            }
        }

        // 内容区域
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
        ) {
            // 发布日期 (All-caps)
            Text(
                text = essay.date,
                style = MaterialTheme.typography.labelSmall,
                color = SongbookColors.TerracottaBrown,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 随笔标题 (Serif)
            Text(
                text = essay.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                lineHeight = 26.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 摘要
            Text(
                text = essay.summary,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontStyle = FontStyle.Italic,
                lineHeight = 22.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 阅读全文链接
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "阅读全文",
                    style = MaterialTheme.typography.labelMedium,
                    color = SongbookColors.BurntOrange,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Read article",
                    tint = SongbookColors.BurntOrange,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
