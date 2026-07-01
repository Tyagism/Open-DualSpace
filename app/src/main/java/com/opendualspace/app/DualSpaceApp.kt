package com.opendualspace.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

/**
 * Application class for Open DualSpace.
 * Initializes notification channels and global dependencies.
 */
class DualSpaceApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // Clone operations channel
            val cloneChannel = NotificationChannel(
                CHANNEL_CLONE_OPS,
                "Clone Operations",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Progress and results for app cloning operations"
                setShowBadge(false)
            }

            // General channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "General",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "General notifications from Open DualSpace"
            }

            notificationManager.createNotificationChannel(cloneChannel)
            notificationManager.createNotificationChannel(generalChannel)
        }
    }

    companion object {
        const val CHANNEL_CLONE_OPS = "clone_operations"
        const val CHANNEL_GENERAL = "general"
    }
}
