package com.example.scheduledlauncher.data

import android.content.Context

/**
 * 定时配置数据类
 */
data class ScheduleConfig(
    val targetPackage: String = "",   // 目标 App 包名
    val targetAppName: String = "",   // 目标 App 显示名称
    val hour: Int = 9,                // 触发小时 (0-23)
    val minute: Int = 0,              // 触发分钟 (0-59)
    val isEnabled: Boolean = false    // 是否启用定时
)

/**
 * SharedPreferences 封装 —— 保存和加载定时配置
 */
object SchedulePreferences {

    private const val PREFS_NAME = "schedule_prefs"
    private const val KEY_TARGET_PACKAGE = "target_package"
    private const val KEY_TARGET_APP_NAME = "target_app_name"
    private const val KEY_HOUR = "hour"
    private const val KEY_MINUTE = "minute"
    private const val KEY_ENABLED = "is_enabled"

    fun save(context: Context, config: ScheduleConfig) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TARGET_PACKAGE, config.targetPackage)
            .putString(KEY_TARGET_APP_NAME, config.targetAppName)
            .putInt(KEY_HOUR, config.hour)
            .putInt(KEY_MINUTE, config.minute)
            .putBoolean(KEY_ENABLED, config.isEnabled)
            .apply()
    }

    fun load(context: Context): ScheduleConfig {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return ScheduleConfig(
            targetPackage = prefs.getString(KEY_TARGET_PACKAGE, "") ?: "",
            targetAppName = prefs.getString(KEY_TARGET_APP_NAME, "") ?: "",
            hour = prefs.getInt(KEY_HOUR, 9),
            minute = prefs.getInt(KEY_MINUTE, 0),
            isEnabled = prefs.getBoolean(KEY_ENABLED, false)
        )
    }
}
