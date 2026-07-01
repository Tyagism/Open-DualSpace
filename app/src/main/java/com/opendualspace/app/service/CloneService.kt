package com.opendualspace.app.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.opendualspace.app.MainActivity
import com.opendualspace.app.domain.manager.WorkProfileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Foreground service for long-running clone operations.
 * Shows a persistent notification during cloning and handles
 * batch clone operations.
 */
class CloneService : Service() {

    companion object {
        private const val TAG = "CloneService"
        private const val CHANNEL_ID = "clone_operations"
        private const val NOTIFICATION_ID = 2001

        const val ACTION_CLONE = "com.opendualspace.action.CLONE"
        const val ACTION_BATCH_CLONE = "com.opendualspace.action.BATCH_CLONE"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_NAME = "extra_app_name"
        const val EXTRA_PACKAGE_NAMES = "extra_package_names"

        fun createCloneIntent(
            context: Context,
            packageName: String,
            appName: String
        ): Intent {
            return Intent(context, CloneService::class.java).apply {
                action = ACTION_CLONE
                putExtra(EXTRA_PACKAGE_NAME, packageName)
                putExtra(EXTRA_APP_NAME, appName)
            }
        }
    }

    private lateinit var workProfileManager: WorkProfileManager
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        workProfileManager = WorkProfileManager(this)
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_CLONE -> {
                val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: ""
                val appName = intent.getStringExtra(EXTRA_APP_NAME) ?: "App"

                startForeground(
                    NOTIFICATION_ID,
                    createProgressNotification("Cloning $appName...")
                )

                serviceScope.launch {
                    // Perform clone operation
                    val success = workProfileManager.cloneApp(packageName)

                    // Update notification with result
                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager

                    if (success) {
                        notificationManager.notify(
                            NOTIFICATION_ID + 1,
                            createResultNotification("$appName cloned successfully!", true)
                        )
                    } else {
                        notificationManager.notify(
                            NOTIFICATION_ID + 1,
                            createResultNotification("Failed to clone $appName", false)
                        )
                    }

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }

            ACTION_BATCH_CLONE -> {
                val packageNames = intent.getStringArrayListExtra(EXTRA_PACKAGE_NAMES) ?: arrayListOf()

                startForeground(
                    NOTIFICATION_ID,
                    createProgressNotification("Cloning ${packageNames.size} apps...")
                )

                serviceScope.launch {
                    var successCount = 0
                    packageNames.forEachIndexed { index, packageName ->
                        val success = workProfileManager.cloneApp(packageName)
                        if (success) successCount++

                        // Update progress
                        val notificationManager =
                            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                        notificationManager.notify(
                            NOTIFICATION_ID,
                            createProgressNotification(
                                "Cloning apps (${index + 1}/${packageNames.size})..."
                            )
                        )
                    }

                    val notificationManager =
                        getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(
                        NOTIFICATION_ID + 1,
                        createResultNotification(
                            "$successCount of ${packageNames.size} apps cloned successfully",
                            successCount > 0
                        )
                    )

                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Clone Operations",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows progress when cloning apps"
                setShowBadge(false)
            }
            val notificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createProgressNotification(message: String): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Open DualSpace")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pendingIntent)
            .setProgress(0, 0, true)
            .setOngoing(true)
            .build()
    }

    private fun createResultNotification(message: String, success: Boolean): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Open DualSpace")
            .setContentText(message)
            .setSmallIcon(
                if (success) android.R.drawable.ic_dialog_info
                else android.R.drawable.ic_dialog_alert
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
    }
}
