package com.example.moodymusicforandroid.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * The Modern Songbook 全局主题组件
 */
@Composable
fun SongbookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) SongbookDarkColorScheme else SongbookLightColorScheme
    val extendedColors = ExtendedColors(
        paperBackground = if (darkTheme) SongbookColors.PaperBackgroundDark else SongbookColors.PaperBackground,
        softCharcoal = if (darkTheme) SongbookColors.SoftCharcoalDark else SongbookColors.SoftCharcoal,
        terracotta = SongbookColors.TerracottaBrown,
        mutedOlive = SongbookColors.MutedOlive,
        ghostBorder = if (darkTheme) SongbookColors.GhostBorder else SongbookColors.GhostBorder
    )

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SongbookTypography,
            shapes = SongbookShapes,
            content = content
        )
    }
}

object SongbookTheme {
    val extendedColors: ExtendedColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
}
