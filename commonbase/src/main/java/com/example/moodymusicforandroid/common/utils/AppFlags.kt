package com.example.moodymusicforandroid.common.utils

/**
 * 全局 App 状态标记（轻量单例）
 *
 * 使用场景：
 * - isAlbumDetailVisible + visibleAlbumId：供 JPushReceiver 判断当前是否在专辑详情页
 * - hasNewComments + pendingRefreshAlbumId：后台脏标记，供 onResume 消费
 *
 * 调用方：
 * - AlbumDetailActivity.onResume()  → 读 hasNewComments，触发刷新后重置
 * - AlbumDetailActivity.onResume()  → 设置 isAlbumDetailVisible = true
 * - AlbumDetailActivity.onPause()   → 设置 isAlbumDetailVisible = false
 */
object AppFlags {
    /** 专辑详情页是否当前可见（前台） */
    @Volatile var isAlbumDetailVisible: Boolean = false

    /** 当前可见的专辑 ID（用于精准匹配推送） */
    @Volatile var visibleAlbumId: String = ""

    /** 是否有新评论待刷新 */
    @Volatile var hasNewComments: Boolean = false

    /** 待刷新的专辑 ID */
    @Volatile var pendingRefreshAlbumId: String = ""

    /** 是否需要展示互踢弹窗 */
    @Volatile var showKickOutDialog: Boolean = false
}
