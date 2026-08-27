package com.rosan.dhizuku.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.rosan.dhizuku.data.common.util.DhizukuState

class DaemonReceiver : BroadcastReceiver() {

    companion object {
        private fun tryStartDaemon(context: Context) {
            // 只在确认是 Device Owner 时才拉，避免非 DO 状态下也起前台服务
            if (!DhizukuState.state.isOwner) return
            val intent = Intent(context, DaemonService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val action = intent.action ?: return

        when (action) {
            // 原有：开机/锁屏开机拉起
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED -> {
                tryStartDaemon(context)
            }
            // 新增：用户解锁/亮屏拉起（治华为锁屏冻 Daemon）
            Intent.ACTION_USER_PRESENT,
            Intent.ACTION_SCREEN_ON -> {
                tryStartDaemon(context)
            }
        }
    }
}
