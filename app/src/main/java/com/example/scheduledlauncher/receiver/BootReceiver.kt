package com.example.scheduledlauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.scheduledlauncher.data.SchedulePreferences
import com.example.scheduledlauncher.scheduler.AlarmScheduler

/**
 * 开机完成接收器
 * 手机重启后，系统发送 BOOT_COMPLETED 广播
 * 此接收器负责恢复之前设置的定时闹钟
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val config = SchedulePreferences.load(context)
            // 仅在定时功能启用时才重新注册闹钟
            if (config.isEnabled) {
                AlarmScheduler.schedule(context, config)
            }
        }
    }
}
