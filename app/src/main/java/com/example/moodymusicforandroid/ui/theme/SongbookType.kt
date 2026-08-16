package com.example.moodymusicforandroid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R

/**
 * 现代颂歌 (The Modern Songbook) 字体排印体系
 *
 * 核心设计规则：
 * 1. Display & Headline 采用高质量衬线体 (Serif)，构建杂志大标题的典雅文学质感。
 * 2. Title, Body & Label 采用高可读性无衬线体 (Sans)，承载清晰信息流与元数据。
 * 3. 标签/元数据 (Label) 采用大写加字间距 (tracking / letter spacing)。
 */

val SongbookSerifFontFamily = FontFamily(
    Font(R.font.source_han_serif_sc_regular, FontWeight.Normal),
    Font(R.font.source_han_serif_sc_bold, FontWeight.Bold),
    Font(R.font.source_han_serif_sc_bold, FontWeight.Black),
    Font(R.font.source_han_serif_sc_regular, FontWeight.Normal, FontStyle.Italic)
)

val SongbookSansFontFamily = FontFamily(
    Font(R.font.source_han_sans_sc_regular, FontWeight.Normal),
    Font(R.font.source_han_sans_sc_bold, FontWeight.Bold),
    Font(R.font.source_han_sans_sc_bold, FontWeight.SemiBold)
)

val SongbookHandwritingFontFamily = FontFamily(
    Font(R.font.lxgw_wenkai_gb_regular, FontWeight.Normal)
)

val SongbookTypography = Typography(
    // 巨幅标题 (Display) - 艺术家巨幅立绘与刊头
    displayLarge = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Black,
        fontSize = 52.sp,
        lineHeight = 56.sp,
        letterSpacing = (-1.0).sp
    ),
    displayMedium = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 46.sp,
        letterSpacing = (-0.5).sp
    ),
    displaySmall = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        lineHeight = 38.sp,
        letterSpacing = 0.sp
    ),

    // 重点章节头 (Headline) - 杂志专题与大栏目
    headlineLarge = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.2).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),

    // 标题级别 (Title) - 专辑名、歌曲名、卡片标题
    titleLarge = TextStyle(
        fontFamily = SongbookSerifFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),

    // 正文级别 (Body) - 随笔、录音手记、简介
    bodyLarge = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.25.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.2.sp
    ),
    bodySmall = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.2.sp
    ),

    // 标签与元数据 (Label) - 杂志版号、年代、曲目数、胶囊标签
    labelLarge = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 1.0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SongbookSansFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.5.sp
    )
)
