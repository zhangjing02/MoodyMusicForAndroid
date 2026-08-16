package com.example.moodymusicforandroid.ui.home.components

import android.app.Activity
import android.graphics.Color as AndroidColor
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.ui.navigation.RouteDiscover
import com.example.moodymusicforandroid.ui.navigation.RouteHome
import com.example.moodymusicforandroid.ui.navigation.RouteLibrary
import eightbitlab.com.blurview.BlurView

@Composable
fun MainBottomBar(
    currentRoute: Any,
    onNavigate: (Any) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 48.dp, end = 48.dp, top = 24.dp, bottom = 12.dp)
            .height(64.dp)
    ) {
        // 毛玻璃背景层
        AndroidView(
            factory = { ctx ->
                val blurView = BlurView(ctx)
                val drawable = GradientDrawable().apply {
                    cornerRadius = 32f * ctx.resources.displayMetrics.density
                    setColor(AndroidColor.TRANSPARENT)
                }
                blurView.background = drawable
                blurView.clipToOutline = true
                blurView.post {
                    val decorView = (ctx as? Activity)?.window?.decorView as? ViewGroup
                    if (decorView != null) {
                        blurView.setupWith(decorView)
                            .setBlurRadius(16f)
                            .setOverlayColor(AndroidColor.argb(0x4D, 0xF4, 0xF4, 0xF4))
                    }
                }
                blurView
            },
            modifier = Modifier.matchParentSize()
        )

        Row(
            modifier = Modifier.matchParentSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavIcon(
                isSelected = currentRoute is RouteHome,
                onClick = { onNavigate(RouteHome) },
                iconRes = R.drawable.ic_nav_home_stitch,
                contentDescription = "首页"
            )
            NavIcon(
                isSelected = currentRoute is RouteDiscover,
                onClick = { onNavigate(RouteDiscover) },
                iconRes = R.drawable.ic_nav_discover_stitch,
                contentDescription = "发现"
            )
            NavIcon(
                isSelected = currentRoute is RouteLibrary,
                onClick = { onNavigate(RouteLibrary) },
                iconRes = R.drawable.ic_nav_library_stitch,
                contentDescription = "个人收藏"
            )
        }
    }
}

@Composable
private fun NavIcon(
    isSelected: Boolean,
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String
) {
    val tint by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        animationSpec = tween(durationMillis = 220),
        label = "navTint"
    )

    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "navScale"
    )

    Box(
        modifier = Modifier
            .size(48.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier
                .size(28.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                },
            tint = tint
        )
    }
}
