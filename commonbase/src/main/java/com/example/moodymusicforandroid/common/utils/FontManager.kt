package com.example.moodymusicforandroid.common.utils

import android.content.Context

/**
 * 字体管理类
 * 管理应用的字体风格切换
 */
object FontManager {

    private const val PREFS_NAME = "font_prefs"
    private const val KEY_FONT_STYLE = "font_style"

    /**
     * 字体风格
     */
    enum class FontStyle(val value: Int, val displayName: String) {
        HANDWRITING(0, "手写飘逸"),
        MODERN(1, "现代轻盈"),
        ELEGANT(2, "清秀文艺")
    }

    /**
     * 获取当前字体风格
     */
    fun getFontStyle(context: Context): FontStyle {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val fontValue = prefs.getInt(KEY_FONT_STYLE, FontStyle.HANDWRITING.value)
        return FontStyle.values().firstOrNull { it.value == fontValue } ?: FontStyle.HANDWRITING
    }

    /**
     * 设置字体风格
     */
    fun setFontStyle(context: Context, fontStyle: FontStyle) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putInt(KEY_FONT_STYLE, fontStyle.value).apply()
    }

    /**
     * 获取字体风格的显示名称
     */
    fun getDisplayName(fontStyle: FontStyle): String {
        return fontStyle.displayName
    }
}
