package com.rosan.dhizuku.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * DaemonReceiver (保活版):
 *  原逻辑保留, 额外在收到 USER_PRESENT/SCREEN_ON 时尝试拉起 DaemonService,
 *  应对华为/ColorOS 等 ROM 锁屏冻结后台服务导致 bindUserService 超时。
 */
class DaemonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_SCREEN_ON -> {
                // 主动拉起守护服务, START_STICKY 会兜底重建
                val svc = Intent(context, DaemonService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(svc)
                } else {
                    context.startService(svc)
                }
            }
        }
    }
}
