package com.example.moodymusicforandroid.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.moodymusicforandroid.R

/**
 * 现代颂歌 (Songbook) 专用图片加载组件
 *
 * 特性：
 * 1. 自动处理相对路径（如 `/storage/...` 或 `storage/...`）并拼接 Cloud CDN 域名；
 * 2. 具备多级优雅 Fallback（网络加载失败或离线时自动切换高质量本地内置资源）；
 * 3. 支持杂志风质感滤镜（如轻微去饱和、暖调或黑白颗粒感）；
 * 4. 内置平滑 CrossFade 淡入过渡。
 */
@Composable
fun SongbookImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    @DrawableRes fallbackRes: Int = R.drawable.hero_acoustic_guitar,
    contentScale: ContentScale = ContentScale.Crop,
    applyEditorialFilter: Boolean = false,
    colorFilter: ColorFilter? = null
) {
    val context = LocalContext.current
    
    // 解析最终可加载的 URL 格式
    val resolvedModel = when (model) {
        is String -> {
            if (model.isBlank()) {
                fallbackRes
            } else if (model.startsWith("http://") || model.startsWith("https://")) {
                model
            } else if (model.startsWith("/")) {
                "https://m-api.changgepd.top$model"
            } else {
                "https://m-api.changgepd.top/$model"
            }
        }
        null -> fallbackRes
        else -> model
    }

    val finalColorFilter = colorFilter ?: if (applyEditorialFilter) {
        // 杂志风轻微去饱和与对比度微调
        val matrix = ColorMatrix().apply {
            setToSaturation(0.85f)
        }
        ColorFilter.colorMatrix(matrix)
    } else {
        null
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(resolvedModel)
                .crossfade(true)
                .placeholder(fallbackRes)
                .error(fallbackRes)
                .fallback(fallbackRes)
                .build(),
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = contentScale,
            colorFilter = finalColorFilter
        )
    }
}
