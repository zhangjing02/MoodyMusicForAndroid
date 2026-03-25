package com.example.moodymusicforandroid.common.eventbus

/**
 * 统一的事件对象
 * 所有通过 EventBus 发送的事件都应该使用这个类
 *
 * @property eventType 事件类型，使用 EventType 中定义的常量
 * @property eventMessage 事件的简单信息（可选），用于日志或简单通知
 * @property eventData 事件的具体数据（可选），可以是任意对象
 */
data class BaseEvent(
    val eventType: Int,
    val eventMessage: String = "",
    val eventData: Any? = null
) {
    companion object {
        /**
         * 快速创建只有类型的事件
         */
        fun create(type: Int): BaseEvent {
            return BaseEvent(type)
        }

        /**
         * 创建带消息的事件
         */
        fun createWithMessage(type: Int, message: String): BaseEvent {
            return BaseEvent(type, message)
        }

        /**
         * 创建带数据的事件
         */
        fun createWithData(type: Int, data: Any?): BaseEvent {
            return BaseEvent(type, eventData = data)
        }

        /**
         * 创建完整的事件（消息 + 数据）
         */
        fun createFull(type: Int, message: String, data: Any?): BaseEvent {
            return BaseEvent(type, message, data)
        }
    }

    /**
     * 获取事件数据并转换为指定类型
     * @return 转换后的数据，如果类型不匹配或数据为 null 则返回 null
     */
    inline fun <reified T> getData(): T? {
        return if (eventData is T) eventData else null
    }

    /**
     * 获取事件数据，如果为 null 则返回默认值
     */
    inline fun <reified T> getDataOrDefault(defaultValue: T): T {
        return if (eventData is T) eventData else defaultValue
    }

    /**
     * 检查事件类型是否匹配
     */
    fun isType(type: Int): Boolean {
        return this.eventType == type
    }

    override fun toString(): String {
        return "BaseEvent(type=$eventType, message='$eventMessage', data=$eventData)"
    }
}
