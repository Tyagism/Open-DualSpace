package com.opendualspace.app.data.repository

import android.app.ActivityManager
import android.app.usage.StorageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.UserHandle
import com.opendualspace.app.data.model.AppCategory
import com.opendualspace.app.data.model.InstalledApp
import com.opendualspace.app.data.model.ResourceUsage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Repository for querying installed apps and gathering resource usage data.
 */
class AppRepository(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager
    private var cachedApps: List<InstalledApp>? = null

    /**
     * Get all installed user apps (non-system by default).
     */
    suspend fun getInstalledApps(
        includeSystem: Boolean = false,
        forceRefresh: Boolean = false
    ): List<InstalledApp> = withContext(Dispatchers.IO) {
        if (!forceRefresh && cachedApps != null) {
            return@withContext cachedApps!!
        }

        val apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { appInfo ->
                // Always exclude ourselves
                if (appInfo.packageName == context.packageName) return@filter false

                // Filter system apps unless requested
                if (!includeSystem) {
                    val isSystem = appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
                    val isUpdatedSystem = appInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP != 0
                    if (isSystem && !isUpdatedSystem) return@filter false
                }

                // Must have a launch intent
                packageManager.getLaunchIntentForPackage(appInfo.packageName) != null
            }
            .map { appInfo ->
                InstalledApp(
                    packageName = appInfo.packageName,
                    appName = appInfo.loadLabel(packageManager).toString(),
                    icon = try {
                        appInfo.loadIcon(packageManager)
                    } catch (e: Exception) {
                        null
                    },
                    isSystemApp = appInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
                    apkSizeBytes = try {
                        File(appInfo.sourceDir).length()
                    } catch (e: Exception) {
                        0L
                    },
                    category = AppCategory.fromPackageName(appInfo.packageName)
                )
            }
            .sortedBy { it.appName.lowercase() }

        cachedApps = apps
        apps
    }

    /**
     * Search installed apps by name.
     */
    suspend fun searchApps(
        query: String,
        includeSystem: Boolean = false
    ): List<InstalledApp> {
        val allApps = getInstalledApps(includeSystem)
        if (query.isBlank()) return allApps

        return allApps.filter {
            it.appName.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
    }

    /**
     * Filter apps by category.
     */
    suspend fun getAppsByCategory(
        category: AppCategory?,
        includeSystem: Boolean = false
    ): List<InstalledApp> {
        val allApps = getInstalledApps(includeSystem)
        if (category == null) return allApps
        return allApps.filter { it.category == category }
    }

    /**
     * Get storage usage for a specific app package.
     */
    fun getAppStorageInfo(packageName: String): Pair<Long, Long> {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val storageStatsManager =
                    context.getSystemService(Context.STORAGE_STATS_SERVICE) as StorageStatsManager
                val stats = storageStatsManager.queryStatsForPackage(
                    android.os.storage.StorageManager.UUID_DEFAULT,
                    packageName,
                    UserHandle.getUserHandleForUid(android.os.Process.myUid())
                )
                Pair(stats.appBytes + stats.dataBytes, stats.cacheBytes)
            } else {
                Pair(0L, 0L)
            }
        } catch (e: Exception) {
            Pair(0L, 0L)
        }
    }

    /**
     * Get overall resource usage for the DualSpace.
     */
    fun getResourceUsage(): ResourceUsage {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)

        val totalRamMB = memInfo.totalMem / (1024f * 1024f)
        val availRamMB = memInfo.availMem / (1024f * 1024f)
        val usedRamMB = totalRamMB - availRamMB

        // Storage info
        val statFs = StatFs(Environment.getDataDirectory().path)
        val totalStorageBytes = statFs.totalBytes
        val availStorageBytes = statFs.availableBytes
        val usedStorageBytes = totalStorageBytes - availStorageBytes

        return ResourceUsage(
            totalRamMB = totalRamMB,
            usedRamMB = usedRamMB,
            totalStorageBytes = totalStorageBytes,
            usedStorageBytes = usedStorageBytes
        )
    }

    /**
     * Invalidate the cached app list.
     */
    fun invalidateCache() {
        cachedApps = null
    }
}
