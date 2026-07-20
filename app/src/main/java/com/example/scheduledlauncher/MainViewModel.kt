package com.example.scheduledlauncher

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.scheduledlauncher.data.ScheduleConfig
import com.example.scheduledlauncher.data.SchedulePreferences
import com.example.scheduledlauncher.scheduler.AlarmScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 主 ViewModel —— 管理 UI 状态和用户操作
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    // ── 可观察的 UI 状态 ──

    private val _config = MutableStateFlow(ScheduleConfig())
    val config: StateFlow<ScheduleConfig> = _config.asStateFlow()

    private val _nextTriggerTime = MutableStateFlow("未设置")
    val nextTriggerTime: StateFlow<String> = _nextTriggerTime.asStateFlow()

    init {
        loadConfig()
    }

    // ── 数据加载 ──

    private fun loadConfig() {
        val saved = SchedulePreferences.load(getApplication())
        _config.value = saved
        updateNextTriggerText(saved)
    }

    // ── 用户操作 ──

    /** 更新目标应用 */
    fun updateTargetApp(packageName: String, appName: String) {
        _config.value = _config.value.copy(
            targetPackage = packageName,
            targetAppName = appName
        )
    }

    /** 更新触发时间 */
    fun updateTime(hour: Int, minute: Int) {
        _config.value = _config.value.copy(hour = hour, minute = minute)
    }

    /** 更新启用状态 */
    fun updateEnabled(enabled: Boolean) {
        _config.value = _config.value.copy(isEnabled = enabled)
    }

    /** 保存配置并注册闹钟 */
    fun save() {
        viewModelScope.launch {
            val current = _config.value
            SchedulePreferences.save(getApplication(), current)
            AlarmScheduler.schedule(getApplication(), current)
            updateNextTriggerText(current)
        }
    }

    // ── 私有方法 ──

    private fun updateNextTriggerText(config: ScheduleConfig) {
        _nextTriggerTime.value = if (config.isEnabled && config.targetPackage.isNotEmpty()) {
            val timestamp = AlarmScheduler.getNextTriggerTime(config)
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            "下次触发: ${sdf.format(Date(timestamp))}"
        } else {
            "未设置"
        }
    }
}
