package com.example.moodymusicforandroid

import android.app.Application
import com.example.moodymusicforandroid.common.preferences.PreferencesManager

/**
 * Application 类
 * 用于初始化全局配置和组件
 */
class MoodyMusicApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // 初始化 PreferencesManager
        PreferencesManager.init(this)

        // TODO: 后续可以在这里初始化其他组件，例如：
        // - Room Database
        // - 第三方 SDK
        // - LeakCanary（调试时）
        // - 友盟统计
        // - Bugly
    }
}
