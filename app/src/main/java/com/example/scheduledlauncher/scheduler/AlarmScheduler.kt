package com.example.scheduledlauncher.scheduler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.scheduledlauncher.data.ScheduleConfig
import com.example.scheduledlauncher.receiver.AlarmReceiver
import java.util.Calendar

/**
 * 闹钟调度器 —— 封装 AlarmManager 的调度和取消操作
 */
object AlarmScheduler {

    private const val ALARM_REQUEST_CODE = 1001

    /**
     * 设置每日定时闹钟
     * - 取消已有闹钟
     * - 计算下一次触发时间
     * - 使用 setExactAndAllowWhileIdle 确保省电模式下也能触发
     */
    fun schedule(context: Context, config: ScheduleConfig) {
        // 先取消已有的闹钟，避免重复
        cancel(context)

        // 未启用或未选择目标 App，不设置闹钟
        if (!config.isEnabled) return
        if (config.targetPackage.isEmpty()) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 计算下一次触发时间：今天的设定时间，如果已过则推到明天
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.hour)
            set(Calendar.MINUTE, config.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }

    /**
     * 取消已设置的闹钟
     */
    fun cancel(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }

    /**
     * 计算下一次触发的时间戳（毫秒），用于 UI 显示
     */
    fun getNextTriggerTime(config: ScheduleConfig): Long {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, config.hour)
            set(Calendar.MINUTE, config.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return calendar.timeInMillis
    }
}
