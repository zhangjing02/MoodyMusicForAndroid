package com.example.moodymusicforandroid.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 现代颂歌 (The Modern Songbook) 形状与圆角体系
 *
 * 遵循设计规范：
 * - 唱片/封面/卡片：偏向小圆角 (2dp~4dp) 以保持实体印刷品的纸感与利落轮廓。
 * - 按钮与输入框：采用 8dp~12dp 的温润倒角。
 * - 胶囊标签、浮动播放器与 Dock：采用 24dp / 32dp / 完全圆形 (CircleShape)。
 */

val SongbookShapes = Shapes(
    extraSmall = RoundedCornerShape(2.dp),
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(12.dp),
    extraLarge = RoundedCornerShape(24.dp)
)

object SongbookSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 12.dp
    val lg = 16.dp
    val xl = 20.dp
    val xxl = 24.dp
    val xxxl = 32.dp
    val huge = 48.dp
    val section = 56.dp
    val bottomContentPadding = 120.dp
}
