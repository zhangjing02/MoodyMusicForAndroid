package com.example.moodymusicforandroid

import android.app.Application
import com.example.moodymusicforandroid.common.preferences.PreferencesManager

import com.example.moodymusicforandroid.common.utils.FontManager
import com.example.moodymusicforandroid.common.utils.ThemeManager

import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory

/**
 * Application 类
 * 用于初始化全局配置和组件
 */
class MoodyMusicApplication : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            // Disable hardware bitmaps on Android 11 and below to prevent
            // "Software rendering doesn't support hardware bitmaps" crashes when
            // using BlurView (which uses a software canvas for RenderScript on < API 31)
            .allowHardware(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            .build()
    }

    companion object {
        @JvmStatic
        var currentThemeResId: Int = R.style.Theme_MoodyMusicForAndroid_Green_Handwriting
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化 PreferencesManager
        PreferencesManager.init(this)

        // 初始化主题
        ThemeManager.initTheme(this)

        // 应用组合主题（字体 + 颜色）
        applyCombinedTheme()

        // TODO: 后续可以在这里初始化其他组件，例如：
        // - Room Database
        // - 第三方 SDK
        // - LeakCanary（调试时）
        // - 友盟统计
        // - Bugly

        // 初始化极光推送
        cn.jpush.android.api.JPushInterface.setDebugMode(true)
        cn.jpush.android.api.JPushInterface.init(this)
    }

    /**
     * 应用组合主题（字体风格 + 颜色主题）
     * 保持 FontManager 和 ThemeManager 逻辑独立，在此组合
     */
    private fun applyCombinedTheme() {
        val fontStyle = FontManager.getFontStyle(this)
        val colorTheme = ThemeManager.getTheme(this)

        currentThemeResId = when (colorTheme) {
            ThemeManager.ThemeMode.DEFAULT -> {
                when (fontStyle) {
                    FontManager.FontStyle.HANDWRITING -> R.style.Theme_MoodyMusicForAndroid_Green_Handwriting
                    FontManager.FontStyle.MODERN -> R.style.Theme_MoodyMusicForAndroid_Green_Modern
                    FontManager.FontStyle.ELEGANT -> R.style.Theme_MoodyMusicForAndroid_Green_Elegant
                }
            }
            ThemeManager.ThemeMode.OCEAN -> {
                when (fontStyle) {
                    FontManager.FontStyle.HANDWRITING -> R.style.Theme_MoodyMusicForAndroid_Ocean_Handwriting
                    FontManager.FontStyle.MODERN -> R.style.Theme_MoodyMusicForAndroid_Ocean_Modern
                    FontManager.FontStyle.ELEGANT -> R.style.Theme_MoodyMusicForAndroid_Ocean_Elegant
                }
            }
            ThemeManager.ThemeMode.SUNSET -> {
                when (fontStyle) {
                    FontManager.FontStyle.HANDWRITING -> R.style.Theme_MoodyMusicForAndroid_Sunset_Handwriting
                    FontManager.FontStyle.MODERN -> R.style.Theme_MoodyMusicForAndroid_Sunset_Modern
                    FontManager.FontStyle.ELEGANT -> R.style.Theme_MoodyMusicForAndroid_Sunset_Elegant
                }
            }
            ThemeManager.ThemeMode.NIGHT -> {
                when (fontStyle) {
                    FontManager.FontStyle.HANDWRITING -> R.style.Theme_MoodyMusicForAndroid_Night_Handwriting
                    FontManager.FontStyle.MODERN -> R.style.Theme_MoodyMusicForAndroid_Night_Modern
                    FontManager.FontStyle.ELEGANT -> R.style.Theme_MoodyMusicForAndroid_Night_Elegant
                }
            }
        }
    }

    /**
     * 更新主题（供外部调用）
     * 重新计算组合主题
     */
    fun updateTheme() {
        applyCombinedTheme()
    }
}
