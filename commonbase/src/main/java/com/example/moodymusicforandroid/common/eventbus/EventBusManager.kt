package com.example.moodymusicforandroid.common.eventbus

import org.greenrobot.eventbus.EventBus

/**
 * EventBus 管理类
 * 封装 EventBus 的注册、注销和发送操作，提供统一的事件发送接口
 *
 * 注意：注册和注销操作由 BaseActivity 和 BaseFragment 自动处理，
 * 页面不需要手动调用 register 和 unregister 方法
 */
object EventBusManager {

    private val eventBus: EventBus = EventBus.getDefault()

    /**
     * 注册订阅者（由基类自动调用，无需手动调用）
     * @param subscriber 订阅者对象（Activity 或 Fragment）
     */
    fun register(subscriber: Any) {
        if (!isRegistered(subscriber)) {
            eventBus.register(subscriber)
        }
    }

    /**
     * 注销订阅者（由基类自动调用，无需手动调用）
     * @param subscriber 订阅者对象（Activity 或 Fragment）
     */
    fun unregister(subscriber: Any) {
        if (isRegistered(subscriber)) {
            eventBus.unregister(subscriber)
        }
    }

    /**
     * 检查是否已注册
     */
    fun isRegistered(subscriber: Any): Boolean {
        return eventBus.isRegistered(subscriber)
    }

    // ========== 事件发送方法 ==========

    /**
     * 发送事件（完整版）
     * @param eventType 事件类型
     * @param message 事件消息
     * @param data 事件数据
     */
    fun post(eventType: Int, message: String = "", data: Any? = null) {
        val event = BaseEvent(eventType, message, data)
        eventBus.post(event)
    }

    /**
     * 发送只有类型的事件
     */
    fun post(eventType: Int) {
        val event = BaseEvent.create(eventType)
        eventBus.post(event)
    }

    /**
     * 发送带数据的事件
     */
    fun postWithData(eventType: Int, data: Any?) {
        val event = BaseEvent.createWithData(eventType, data)
        eventBus.post(event)
    }

    /**
     * 发送带消息的事件
     */
    fun postWithMessage(eventType: Int, message: String) {
        val event = BaseEvent.createWithMessage(eventType, message)
        eventBus.post(event)
    }

    /**
     * 发送 BaseEvent 对象
     */
    fun post(event: BaseEvent) {
        eventBus.post(event)
    }

    /**
     * 发送粘性事件（事件会被保留，后续注册的订阅者也能收到）
     */
    fun postSticky(eventType: Int, message: String = "", data: Any? = null) {
        val event = BaseEvent(eventType, message, data)
        eventBus.postSticky(event)
    }

    /**
     * 发送粘性 BaseEvent 对象
     */
    fun postSticky(event: BaseEvent) {
        eventBus.postSticky(event)
    }

    /**
     * 移除粘性事件
     */
    fun removeStickyEvent(event: BaseEvent): Boolean {
        return eventBus.removeStickyEvent(event)
    }

    /**
     * 移除所有粘性事件
     */
    fun removeAllStickyEvents() {
        eventBus.removeAllStickyEvents()
    }

    /**
     * 获取粘性事件
     */
    fun getStickyEvent(eventType: Class<BaseEvent>): BaseEvent? {
        return eventBus.getStickyEvent(eventType)
    }

    /**
     * 取消事件传递（用于在订阅者中阻止事件继续传递）
     */
    fun cancelEventDelivery(event: BaseEvent) {
        eventBus.cancelEventDelivery(event)
    }

    // ========== 便捷发送方法 ==========

    // ========== 音乐播放相关 ==========
    fun postMusicPlay(message: String = "", musicData: Any? = null) {
        post(EventType.MUSIC_PLAY, message, musicData)
    }

    fun postMusicPause(message: String = "暂停播放") {
        post(EventType.MUSIC_PAUSE, message)
    }

    fun postMusicResume(message: String = "恢复播放") {
        post(EventType.MUSIC_RESUME, message)
    }

    fun postMusicStop(message: String = "停止播放") {
        post(EventType.MUSIC_STOP, message)
    }

    fun postMusicNext(musicData: Any? = null) {
        post(EventType.MUSIC_NEXT, "下一首", musicData)
    }

    fun postMusicPrevious(musicData: Any? = null) {
        post(EventType.MUSIC_PREVIOUS, "上一首", musicData)
    }

    fun postMusicProgress(progress: Int, currentPosition: Long, duration: Long) {
        val data = mapOf(
            "progress" to progress,
            "currentPosition" to currentPosition,
            "duration" to duration
        )
        post(EventType.MUSIC_PROGRESS_UPDATE, "", data)
    }

    // ========== 用户相关 ==========
    fun postUserLogin(userData: Any? = null) {
        post(EventType.USER_LOGIN, "用户登录", userData)
    }

    fun postUserLogout(message: String = "用户登出") {
        post(EventType.USER_LOGOUT, message)
    }

    fun postUserInfoUpdate(userData: Any? = null) {
        post(EventType.USER_INFO_UPDATE, "用户信息更新", userData)
    }

    // ========== UI 相关 ==========
    fun postUIRefresh(message: String = "刷新界面") {
        post(EventType.UI_REFRESH, message)
    }

    fun postUIReload(message: String = "重新加载") {
        post(EventType.UI_RELOAD, message)
    }

    fun postUIShowToast(message: String) {
        post(EventType.UI_SHOW_TOAST, message)
    }

    fun postUIClosePage(message: String = "") {
        post(EventType.UI_CLOSE_PAGE, message)
    }

    fun postUIFinishActivity(message: String = "") {
        post(EventType.UI_FINISH_ACTIVITY, message)
    }

    // ========== 网络相关 ==========
    fun postNetworkConnected() {
        post(EventType.Network_CONNECTED, "网络已连接")
    }

    fun postNetworkDisconnected() {
        post(EventType.Network_DISCONNECTED, "网络已断开")
    }

    // ========== 数据同步相关 ==========
    fun postDataSyncStart(message: String = "开始同步") {
        post(EventType.DATA_SYNC_START, message)
    }

    fun postDataSyncComplete(message: String = "同步完成") {
        post(EventType.DATA_SYNC_COMPLETE, message)
    }

    fun postDataUpdate(data: Any? = null) {
        post(EventType.DATA_UPDATE, "数据已更新", data)
    }

    /**
     * 清除所有事件（包括粘性事件）
     */
    fun clear() {
        removeAllStickyEvents()
    }

    /**
     * 获取 EventBus 实例（用于高级用法）
     */
    fun getEventBus(): EventBus {
        return eventBus
    }
}
