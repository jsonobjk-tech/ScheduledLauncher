package com.example.scheduledlauncher.ui

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * 已安装应用信息
 */
data class AppInfo(
    val packageName: String,
    val appName: String
)

/**
 * 应用选择弹窗 —— 列出所有已安装的可启动应用
 */
@Composable
fun AppPickerDialog(
    onDismiss: () -> Unit,
    onAppSelected: (String, String) -> Unit
) {
    val context = LocalContext.current
    val apps = remember { loadInstalledApps(context) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Default.Android, contentDescription = null) },
        title = { Text("选择要启动的应用") },
        text = {
            if (apps.isEmpty()) {
                Text("没有找到可启动的应用")
            } else {
                // 搜索框
                var searchQuery by remember { mutableStateOf("") }
                val filteredApps = apps.filter {
                    it.appName.contains(searchQuery, ignoreCase = true) ||
                    it.packageName.contains(searchQuery, ignoreCase = true)
                }

                Column {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("搜索应用...") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn(
                        modifier = Modifier.height(400.dp)
                    ) {
                        items(filteredApps) { app ->
                            ListItem(
                                headlineContent = { Text(app.appName) },
                                supportingContent = {
                                    Text(
                                        app.packageName,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.clickable {
                                    onAppSelected(app.packageName, app.appName)
                                }
                            )
                            HorizontalDivider()
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

/**
 * 从 PackageManager 获取所有可启动的应用列表
 * 排除自身，按名称排序
 */
private fun loadInstalledApps(context: android.content.Context): List<AppInfo> {
    val pm = context.packageManager
    val mainIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }
    val activities = pm.queryIntentActivities(mainIntent, 0)
    return activities
        .map { resolveInfo ->
            AppInfo(
                packageName = resolveInfo.activityInfo.packageName,
                appName = resolveInfo.loadLabel(pm).toString()
            )
        }
        .filter { it.packageName != context.packageName } // 排除本应用自身
        .sortedBy { it.appName.lowercase() }
}
