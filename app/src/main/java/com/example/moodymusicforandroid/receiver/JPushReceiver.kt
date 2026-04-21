package com.example.moodymusicforandroid.receiver

import android.content.Context
import android.util.Log
import cn.jpush.android.api.CmdMessage
import cn.jpush.android.api.CustomMessage
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.service.JPushMessageReceiver
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.example.moodymusicforandroid.common.utils.AppFlags

/**
 * JPush 消息接收器
 *
 * ──────────────────────────────────────────────────────────
 * 透传消息处理（核心：被动刷新 — 设计文档 §5）
 * ──────────────────────────────────────────────────────────
 *
 * 后端触发推送的 payload 结构：
 * {
 *   "title": "refresh_comments",
 *   "extras": {
 *     "album_id": "db_42",
 *     "action": "FETCH_NEW"
 *   }
 * }
 *
 * 安卓端处理策略（参见设计文档 §5.1 ~ §5.3）：
 *
 * ┌──────────────────────┬──────────────────────────────────────────────┐
 * │ 场景                 │ 动作                                          │
 * ├──────────────────────┼──────────────────────────────────────────────┤
 * │ App 在后台（Service  │ 仅置脏标记 AppFlags.hasNewComments = true     │
 * │  存活，UI 不可见）   │ 禁止发起网络请求                              │
 * ├──────────────────────┼──────────────────────────────────────────────┤
 * │ 用户切回前台         │ AlbumDetailActivity.onResume() 检查标记，      │
 * │ (onResume)           │ 触发 ViewModel.fetchSocialContent()           │
 * ├──────────────────────┼──────────────────────────────────────────────┤
 * │ 用户当前在专辑页     │ LiveBus/LocalBroadcast 直接触发刷新           │
 * │ (isResumed == true)  │ ViewModel 更新 UI（DiffUtil 防止闪烁）        │
 * └──────────────────────┴──────────────────────────────────────────────┘
 */
class JPushReceiver : JPushMessageReceiver() {

    companion object {
        private const val TAG = "JPushReceiver"

        /** 透传消息 action 值 */
        const val ACTION_FETCH_NEW = "FETCH_NEW"
        const val ACTION_KICK_OUT = "KICK_OUT"

        /**
         * LocalBroadcast Action — 通知前台 UI 刷新评论
         * 在 AlbumDetailActivity 中注册此 Action 的接收器
         */
        const val BROADCAST_ACTION_REFRESH_COMMENTS =
            "com.example.moodymusicforandroid.ACTION_REFRESH_COMMENTS"

        /** Intent extra key：对应专辑 ID */
        const val EXTRA_ALBUM_ID = "album_id"
    }

    // ──────────────────────────────────────────
    // 透传消息（核心入口）
    // ──────────────────────────────────────────
    override fun onMessage(context: Context?, customMessage: CustomMessage?) {
        super.onMessage(context, customMessage)

        val raw = customMessage?.message ?: return
        Log.d(TAG, "[onMessage] 收到透传: $raw")

        try {
            val json = Gson().fromJson(raw, JsonObject::class.java)
            val extras = json.getAsJsonObject("extras") ?: return
            val action  = extras.get("action")?.asString   ?: return

            if (action == ACTION_KICK_OUT) {
                Log.d(TAG, "[onMessage] KICK_OUT signal received")
                handleKickOut(context)
                return
            }

            if (action != ACTION_FETCH_NEW) {
                Log.d(TAG, "[onMessage] 未知 action: $action，忽略")
                return
            }

            val albumId = extras.get("album_id")?.asString ?: return
            Log.d(TAG, "[onMessage] FETCH_NEW signal for album: $albumId")
            handleFetchNew(context, albumId)

        } catch (e: Exception) {
            Log.e(TAG, "[onMessage] 解析透传 JSON 失败: ${e.message}")
        }
    }

    /**
     * 处理 FETCH_NEW 信号
     *
     * 判断当前 App 是否在前台：
     * - 前台 → 发送 LocalBroadcast，Activity 直接刷新
     * - 后台 → 设置全局脏标记，等待 onResume 触发刷新
     */
    private fun handleFetchNew(context: Context?, albumId: String) {
        if (context == null) return

        if (AppFlags.isAlbumDetailVisible && AppFlags.visibleAlbumId == albumId) {
            // 场景 C：用户当前正在看这张专辑 — 通过 LocalBroadcast 直接通知 UI
            Log.d(TAG, "[handleFetchNew] 前台可见，发送 LocalBroadcast")
            val intent = android.content.Intent(BROADCAST_ACTION_REFRESH_COMMENTS).apply {
                putExtra(EXTRA_ALBUM_ID, albumId)
            }
            androidx.localbroadcastmanager.content.LocalBroadcastManager
                .getInstance(context)
                .sendBroadcast(intent)
        } else {
            // 场景 A/B：后台或切到其他页面 — 仅置脏标记，onResume 处理
            Log.d(TAG, "[handleFetchNew] 后台状态，置脏标记 hasNewComments=true (albumId=$albumId)")
            AppFlags.hasNewComments = true
            AppFlags.pendingRefreshAlbumId = albumId
        }
    }

    /**
     * 处理 KICK_OUT 互踢信号
     */
    private fun handleKickOut(context: Context?) {
        Log.d(TAG, "[handleKickOut] 用户被互踢，清除本地登录状态")
        try {
            com.example.moodymusicforandroid.common.preferences.PreferencesManager.clearUserInfo()
        } catch (e: Exception) {}

        // 设置标记，让前台 Activity 弹框提示
        AppFlags.showKickOutDialog = true

        // 发送事件，让基类 Activity 处理弹窗显示，不强制跳转
        com.example.moodymusicforandroid.common.eventbus.EventBusManager.post(
            com.example.moodymusicforandroid.common.eventbus.EventType.AUTH_TOKEN_EXPIRED,
            "KICKED_OUT"
        )
    }

    // ──────────────────────────────────────────
    // 通知栏消息（普通推送，非透传）
    // ──────────────────────────────────────────
    override fun onNotifyMessageOpened(context: Context?, message: NotificationMessage?) {
        Log.d(TAG, "[onNotifyMessageOpened] 用户点击了通知")
        super.onNotifyMessageOpened(context, message)
    }

    override fun onNotifyMessageArrived(context: Context?, message: NotificationMessage?) {
        Log.d(TAG, "[onNotifyMessageArrived] 通知已抵达")
        super.onNotifyMessageArrived(context, message)
    }

    override fun onNotifyMessageDismiss(context: Context?, message: NotificationMessage?) {
        Log.d(TAG, "[onNotifyMessageDismiss] 通知被清除")
        super.onNotifyMessageDismiss(context, message)
    }

    override fun onRegister(context: Context?, registrationId: String?) {
        Log.d(TAG, "[onRegister] Registration Id: $registrationId")
        super.onRegister(context, registrationId)
    }

    override fun onConnected(context: Context?, isConnected: Boolean) {
        Log.d(TAG, "[onConnected] $isConnected")
        super.onConnected(context, isConnected)
    }

    override fun onCommandResult(context: Context?, cmdMessage: CmdMessage?) {
        Log.d(TAG, "[onCommandResult] $cmdMessage")
        super.onCommandResult(context, cmdMessage)
    }

    override fun onMultiActionClicked(context: Context?, intent: android.content.Intent?) {
        Log.d(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        super.onMultiActionClicked(context, intent)
    }

    override fun onNotificationSettingsCheck(context: Context?, isOn: Boolean, source: Int) {
        Log.d(TAG, "[onNotificationSettingsCheck] isOn=$isOn, source=$source")
        super.onNotificationSettingsCheck(context, isOn, source)
    }
}
