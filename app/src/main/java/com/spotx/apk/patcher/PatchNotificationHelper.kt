package com.spotx.apk.patcher

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.spotx.apk.R

class PatchNotificationHelper(private val context: Context) {
    private val channelId = "spotx_patch_channel"

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "SpotX patching",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(channel)
        }
    }

    fun progress(message: String, progress: Int): Notification {
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle("SpotX APK patching")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_notification)
            .setProgress(100, progress, false)
            .setOngoing(true)
            .build()
    }
}
