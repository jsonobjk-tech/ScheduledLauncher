package com.example.scheduledlauncher

import android.app.AlarmManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.scheduledlauncher.ui.AppPickerDialog
import com.example.scheduledlauncher.ui.HomeScreen
import com.example.scheduledlauncher.ui.theme.ScheduledLauncherTheme

/**
 * 主 Activity —— 应用唯一入口
 *
 * 启动时会检查 Android 12+ 的精确闹钟权限，
 * 如果未授权则引导用户到系统设置页面开启。
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Android 12+ 需要用户手动授权精确闹钟权限
        requestExactAlarmPermissionIfNeeded()

        setContent {
            ScheduledLauncherTheme {
                val viewModel: MainViewModel = viewModel()
                val config by viewModel.config.collectAsState()
                val nextTriggerTime by viewModel.nextTriggerTime.collectAsState()

                var showAppPicker by remember { mutableStateOf(false) }

                HomeScreen(
                    config = config,
                    nextTriggerTime = nextTriggerTime,
                    onSelectApp = { showAppPicker = true },
                    onTimeChanged = { hour, minute -> viewModel.updateTime(hour, minute) },
                    onEnabledChanged = { enabled -> viewModel.updateEnabled(enabled) },
                    onSave = { viewModel.save() }
                )

                if (showAppPicker) {
                    AppPickerDialog(
                        onDismiss = { showAppPicker = false },
                        onAppSelected = { packageName, appName ->
                            viewModel.updateTargetApp(packageName, appName)
                            showAppPicker = false
                        }
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 从系统设置返回后，检查权限是否已被授予
        // 如果已授权，UI 中的保存按钮即可正常调度
    }

    /**
     * Android 12+ 需要引导用户到系统设置开启精确闹钟权限
     */
    private fun requestExactAlarmPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
        }
    }
}
