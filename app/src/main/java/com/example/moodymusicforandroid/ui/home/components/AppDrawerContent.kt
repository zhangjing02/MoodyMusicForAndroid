package com.example.moodymusicforandroid.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.moodymusicforandroid.R
import com.example.moodymusicforandroid.common.utils.FontManager
import com.example.moodymusicforandroid.common.utils.ThemeManager

/**
 * 侧滑抽屉内容组件
 *
 * 负责展示侧边栏的各项功能，包括：
 * - 用户登录状态和基本信息展示
 * - 主题切换菜单（默认、海洋蓝、日落橙、暗夜紫）
 * - 字体风格切换菜单（手写、现代、清秀）
 * - 教室认证和退出登录功能
 */
@Composable
fun AppDrawerContent(
    /** 用户是否已登录 */
    isLoggedIn: Boolean,
    /** 用户的昵称，若未登录则显示默认文案 */
    userName: String,
    /** 点击关闭按钮时的回调 */
    onCloseClick: () -> Unit,
    /** 点击"教室认证"按钮时的回调 */
    onAuthClick: () -> Unit,
    /** 点击"退出当前认证"按钮时的回调 */
    onLogoutClick: () -> Unit,
    /** 切换主题模式时的回调 */
    onThemeClick: (ThemeManager.ThemeMode) -> Unit,
    /** 切换字体风格时的回调 */
    onFontClick: (FontManager.FontStyle) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(24.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "音信",
                    fontSize = 28.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isLoggedIn) "欢迎回来，$userName" else "听，风吹过的声音",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Theme Section
        Text(
            text = "主题",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
        )
        
        val themes = listOf(
            "默认绿" to ThemeManager.ThemeMode.DEFAULT,
            "海洋蓝" to ThemeManager.ThemeMode.OCEAN,
            "日落橙" to ThemeManager.ThemeMode.SUNSET,
            "暗夜紫" to ThemeManager.ThemeMode.NIGHT
        )
        
        themes.forEach { (title, mode) ->
            TextButton(
                onClick = { onThemeClick(mode) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(title, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Font Section
        Text(
            text = "字体风格",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
        )
        
        val fonts = listOf(
            "手写飘逸" to FontManager.FontStyle.HANDWRITING,
            "现代轻盈" to FontManager.FontStyle.MODERN,
            "清秀文艺" to FontManager.FontStyle.ELEGANT
        )
        
        fonts.forEach { (title, style) ->
            TextButton(
                onClick = { onFontClick(style) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(title, modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.onSurface)
            }
        }

        // Classroom Section
        Text(
            text = "教室",
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 12.dp)
        )
        
        Button(
            onClick = onAuthClick,
            enabled = !isLoggedIn,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(if (isLoggedIn) "已认证" else "教室认证")
        }
        
        if (isLoggedIn) {
            TextButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Text("退出当前认证", color = MaterialTheme.colorScheme.error)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}
