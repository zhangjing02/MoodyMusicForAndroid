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
 *
 * 同时提供左右滑动的 snap 行为：滑动结束后自动吸附到最近的"页"位置，
 * 确保两边始终能看到一点桌子边缘，提示用户可以继续滑动。
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

    // Snap behavior
    private var isSnapping = false
    private var lastCheckedScrollX = -1
    private val snapRunnable = Runnable { checkAndSnap() }

    /** 每页露出多少像素的桌子边缘（约 24dp） */
    private val peekPx: Int by lazy {
        (24 * resources.displayMetrics.density).toInt()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                isScrollingHorizontal = false
                // 取消正在进行的 snap 动画
                isSnapping = false
                removeCallbacks(snapRunnable)
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

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_CANCEL) {
            if (isScrollingHorizontal) {
                postSnapCheck()
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun fling(velocityX: Int) {
        super.fling(velocityX)
        postSnapCheck()
    }

    private fun postSnapCheck() {
        if (isSnapping) return
        removeCallbacks(snapRunnable)
        lastCheckedScrollX = -1
        postDelayed(snapRunnable, 50)
    }

    /**
     * 检查滚动是否停止，停止后 snap 到最近位置
     */
    private fun checkAndSnap() {
        if (isSnapping) return

        val currentScrollX = scrollX
        if (currentScrollX == lastCheckedScrollX) {
            // 滚动已停止，执行 snap
            snapToNearestPosition()
        } else {
            lastCheckedScrollX = currentScrollX
            postDelayed(snapRunnable, 50)
        }
    }

    /**
     * 计算所有 snap 位置，并将视图平滑滚动到最近的位置。
     *
     * 每一页的可视宽度 = 容器宽度 - peekPx，保证切换页后能看到上一页
     * 最后一个位置的桌子边缘。
     */
    private fun snapToNearestPosition() {
        val child = getChildAt(0) ?: return
        val contentWidth = child.width
        val containerWidth = width
        val maxScroll = contentWidth - containerWidth

        if (maxScroll <= 0) return

        // 每页步长：容器宽度减去 peek 裸露量，使下一页露出上一页的桌边
        val pageSize = containerWidth - peekPx

        // 构建 snap 位置列表
        val snapPositions = mutableListOf<Int>()
        var pos = 0
        while (pos < maxScroll) {
            snapPositions.add(pos)
            pos += pageSize
        }
        // 确保 maxScroll 始终是一个 snap 位置（最右端）
        if (snapPositions.isEmpty() || snapPositions.last() != maxScroll) {
            snapPositions.add(maxScroll)
        }

        // 找到距离当前 scrollX 最近的 snap 位置
        val currentX = scrollX
        var nearest = snapPositions[0]
        var minDist = Int.MAX_VALUE
        for (snapPos in snapPositions) {
            val dist = abs(currentX - snapPos)
            if (dist < minDist) {
                minDist = dist
                nearest = snapPos
            }
        }

        if (currentX != nearest) {
            isSnapping = true
            smoothScrollTo(nearest, 0)
            // 动画结束后重置标志
            postDelayed({ isSnapping = false }, 350)
        }
    }
}
