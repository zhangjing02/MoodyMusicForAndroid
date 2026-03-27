package com.example.moodymusicforandroid

import android.app.Application
import com.example.moodymusicforandroid.common.preferences.PreferencesManager
import com.example.moodymusicforandroid.common.utils.FontManager
import com.example.moodymusicforandroid.common.utils.ThemeManager

/**
 * Application 类
 * 用于初始化全局配置和组件
 */
class MoodyMusicApplication : Application() {

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
