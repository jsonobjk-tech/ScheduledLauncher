package com.example.scheduledlauncher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.scheduledlauncher.data.SchedulePreferences
import com.example.scheduledlauncher.scheduler.AlarmScheduler

/**
 * 闹钟触发接收器
 * 当 AlarmManager 设定的时间到达时，系统会调用此接收器
 * 负责：打开目标 App → 重新调度下一次闹钟
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // 读取保存的配置
        val config = SchedulePreferences.load(context)

        // 安全检查：未启用或未选择目标 App 则直接返回
        if (!config.isEnabled || config.targetPackage.isEmpty()) return

        // 通过包名获取启动 Intent 并打开目标 App
        val launchIntent = context.packageManager.getLaunchIntentForPackage(config.targetPackage)
        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(launchIntent)
        }

        // 重新调度明天的闹钟
        AlarmScheduler.schedule(context, config)
    }
}
