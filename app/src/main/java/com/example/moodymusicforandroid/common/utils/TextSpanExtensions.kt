package com.example.moodymusicforandroid.common.utils

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.style.SubscriptSpan
import android.text.style.SuperscriptSpan
import android.text.style.UnderlineSpan

/**
 * SpannableString扩展函数 - 实现富文本效果
 */

/**
 * 创建带样式的SpannableString
 */
fun String.style(
    size: Float? = null,
    color: Int? = null,
    backgroundColor: Int? = null,
    style: Typeface? = null,
    isSubscript: Boolean = false,
    isSuperscript: Boolean = false,
    isUnderline: Boolean = false
): SpannableString {
    val spannableString = SpannableString(this)

    size?.let {
        spannableString.setSpan(AbsoluteSizeSpan(it.toInt(), true), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    color?.let {
        spannableString.setSpan(ForegroundColorSpan(it), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    backgroundColor?.let {
        spannableString.setSpan(BackgroundColorSpan(it), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    style?.let {
        spannableString.setSpan(StyleSpan(it.style), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (isSubscript) {
        spannableString.setSpan(SubscriptSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (isSuperscript) {
        spannableString.setSpan(SuperscriptSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    if (isUnderline) {
        spannableString.setSpan(UnderlineSpan(), 0, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    return spannableString
}

/**
 * 多段文本拼接，每段有不同的样式
 */
fun buildStyledText(vararg parts: Pair<String, TextStyleConfig>): SpannableString {
    val fullText = parts.joinToString("") { it.first }
    val spannableString = SpannableString(fullText)

    var currentIndex = 0
    parts.forEach { (text, config) ->
        val endIndex = currentIndex + text.length

        config.size?.let {
            spannableString.setSpan(AbsoluteSizeSpan(it.toInt(), true), currentIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        config.relativeSize?.let {
            spannableString.setSpan(RelativeSizeSpan(it), currentIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        config.color?.let {
            spannableString.setSpan(ForegroundColorSpan(it), currentIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        config.backgroundColor?.let {
            spannableString.setSpan(BackgroundColorSpan(it), currentIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        config.style?.let {
            spannableString.setSpan(StyleSpan(it), currentIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        currentIndex = endIndex
    }

    return spannableString
}

/**
 * 文本样式配置
 */
data class TextStyleConfig(
    val size: Float? = null,
    val relativeSize: Float? = null,
    val color: Int? = null,
    val backgroundColor: Int? = null,
    val style: Int? = null
)

/**
 * 使用示例：
 *
 * // 简单样式
 * val text = "重要文字".style(
 *     color = Color.RED,
 *     size = 18f,
 *     style = Typeface.BOLD
 * )
 * textView.text = text
 *
 * // 多段文本拼接
 * val complexText = buildStyledText(
 *     "普通文字" to TextStyleConfig(),
 *     "重要" to TextStyleConfig(color = Color.RED, style = Typeface.BOLD, relativeSize = 1.2f),
 *     "普通文字" to TextStyleConfig()
 * )
 * textView.text = complexText
 */

/**
 * 高亮关键词
 */
fun String.highlightKeyword(keyword: String, highlightColor: Int = Color.parseColor("#FFFF00")): SpannableString {
    val spannableString = SpannableString(this)
    val startIndex = lowercase().indexOf(keyword.lowercase())

    if (startIndex != -1) {
        val endIndex = startIndex + keyword.length
        spannableString.setSpan(
            BackgroundColorSpan(highlightColor),
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    return spannableString
}

/**
 * 创建标签式文本（带圆角背景）
 * 注意：这只是文本样式，如需真正的圆角背景请使用TextView的background
 */
fun String.asLabel(textColor: Int, backgroundColor: Int): SpannableString {
    return this.style(
        color = textColor,
        backgroundColor = backgroundColor
    )
}
