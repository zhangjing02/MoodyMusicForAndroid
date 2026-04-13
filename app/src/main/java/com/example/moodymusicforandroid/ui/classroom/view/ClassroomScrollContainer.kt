package com.example.moodymusicforandroid.ui.classroom.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.HorizontalScrollView
import kotlin.math.abs

/**
 * 自定义 HorizontalScrollView，用于解决在教室内双向滑动的冲突。
 * 当检测到用户主要进行纵向滑动时，将事件放弃，交给 RecyclerView 处理。
 */
class ClassroomScrollContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : HorizontalScrollView(context, attrs, defStyleAttr) {

    private var lastX = 0f
    private var lastY = 0f
    private var isScrollingHorizontal = false
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                isScrollingHorizontal = false
                // 确保按下时父布局不要拦截
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val currentX = ev.x
                val currentY = ev.y
                val dx = abs(currentX - lastX)
                val dy = abs(currentY - lastY)

                if (!isScrollingHorizontal && dy > touchSlop && dy > dx) {
                    // 明显的纵向滑动倾向
                    parent.requestDisallowInterceptTouchEvent(false)
                    return false // 不拦截，交给内层 RecyclerView
                }
                
                if (dx > touchSlop && dx > dy) {
                    isScrollingHorizontal = true
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }
}
