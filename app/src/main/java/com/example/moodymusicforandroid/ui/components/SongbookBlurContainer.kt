package com.example.moodymusicforandroid.ui.components

import android.app.Activity
import android.graphics.Outline
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moodymusicforandroid.ui.theme.SongbookColors
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderEffectBlur
import eightbitlab.com.blurview.RenderScriptBlur

/**
 * 现代颂歌 硬件级动态毛玻璃容器 (SongbookBlurContainer)
 *
 * 采用双重保险与真实高斯模糊引擎：
 * 1. 【底色防线】：Compose 层铺设 85% 温暖米白纸质底色与 15% 幽灵边框，确保任何设备（包括模拟器）都拥有清晰高级的胶囊实体轮廓；
 * 2. 【高斯模糊】：BlurView 在硬件支持时实时抓取背景进行 18px 真实高斯模糊计算，呈现出磨砂毛玻璃透光质感。
 */
@Composable
fun SongbookBlurContainer(
    modifier: Modifier = Modifier,
    blurRadius: Float = 18f,
    cornerRadius: Dp = 32.dp,
    overlayColor: Color = SongbookColors.Surface.copy(alpha = 0.85f),
    borderColor: Color = SongbookColors.GhostBorder,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val cornerRadiusPx = with(density) { cornerRadius.toPx() }
    val shape = remember(cornerRadius) { RoundedCornerShape(cornerRadius) }

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = SongbookColors.SoftCharcoal.copy(alpha = 0.10f),
                spotColor = SongbookColors.SoftCharcoal.copy(alpha = 0.18f)
            )
            .clip(shape)
            .background(overlayColor) // 确保胶囊底色始终可见且有实体质感
            .border(borderWidth, borderColor, shape)
    ) {
        // BlurView 硬件级背景高斯模糊层
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { ctx ->
                val blurView = BlurView(ctx)
                val activity = ctx as? Activity
                val decorView = activity?.window?.decorView as? ViewGroup
                val windowBackground = decorView?.background ?: ColorDrawable(android.graphics.Color.WHITE)

                blurView.outlineProvider = object : ViewOutlineProvider() {
                    override fun getOutline(view: View, outline: Outline) {
                        outline.setRoundRect(0, 0, view.width, view.height, cornerRadiusPx)
                    }
                }
                blurView.clipToOutline = true
                blurView.setOverlayColor(overlayColor.toArgb())

                blurView.post {
                    if (decorView != null) {
                        try {
                            val blurAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                RenderEffectBlur()
                            } else {
                                RenderScriptBlur(ctx)
                            }

                            blurView.setupWith(decorView, blurAlgorithm)
                                .setFrameClearDrawable(windowBackground)
                                .setBlurRadius(blurRadius)
                                .setBlurAutoUpdate(true)
                        } catch (e: Throwable) {
                            e.printStackTrace()
                        }
                    }
                }

                blurView
            },
            update = { blurView ->
                blurView.setOverlayColor(overlayColor.toArgb())
            }
        )

        // 顶层：Compose 内容
        content()
    }
}
