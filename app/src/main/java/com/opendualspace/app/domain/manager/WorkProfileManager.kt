package com.opendualspace.app.domain.manager

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.opendualspace.app.data.model.ClonedApp
import com.opendualspace.app.data.preferences.ClonedAppsStore
import com.opendualspace.app.receiver.DeviceAdminReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages the Work Profile lifecycle and app operations within it.
 * Falls back to a local clone registry (ClonedAppsStore) when no
 * Work Profile is available, so the app works on any consumer device.
 */
class WorkProfileManager(private val context: Context) {

    companion object {
        private const val TAG = "WorkProfileMgr"
        const val REQUEST_PROVISION_MANAGED_PROFILE = 1001
    }

    private val devicePolicyManager: DevicePolicyManager =
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager

    private val adminComponent: ComponentName =
        DeviceAdminReceiver.getComponentName(context)

    private val packageManager: PackageManager = context.packageManager

    val clonedAppsStore = ClonedAppsStore(context)

    /**
     * Check if this app is the profile owner (i.e., Work Profile is active).
     */
    fun isWorkProfileActive(): Boolean {
        return try {
            devicePolicyManager.isProfileOwnerApp(context.packageName)
        } catch (e: Exception) {
            Log.w(TAG, "Failed to check profile owner status", e)
            false
        }
    }

    /**
     * Check if this app is a device admin.
     */
    fun isDeviceAdmin(): Boolean {
        return try {
            devicePolicyManager.isAdminActive(adminComponent)
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Create an intent to provision a new Work Profile.
     * The caller should start this intent with startActivityForResult().
     */
    fun createProvisioningIntent(): Intent {
        return Intent(DevicePolicyManager.ACTION_PROVISION_MANAGED_PROFILE).apply {
            putExtra(
                DevicePolicyManager.EXTRA_PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME,
                adminComponent
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                putExtra(
                    DevicePolicyManager.EXTRA_PROVISIONING_SKIP_ENCRYPTION,
                    true
                )
            }
        }
    }

    /**
     * Check if Work Profile provisioning is possible on this device.
     */
    fun canProvisionWorkProfile(): Boolean {
        return try {
            val intent = createProvisioningIntent()
            intent.resolveActivity(packageManager) != null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Clone an app. Uses Work Profile if active, otherwise saves
     * to the local clone store for standalone mode.
     */
    suspend fun cloneApp(packageName: String): Boolean {
        if (isWorkProfileActive()) {
            return try {
                devicePolicyManager.enableSystemApp(adminComponent, packageName)
                clonedAppsStore.addClonedApp(packageName)
                Log.i(TAG, "Cloned app via Work Profile: $packageName")
                true
            } catch (e: Exception) {
                Log.e(TAG, "Work Profile clone failed, falling back to local: $packageName", e)
                clonedAppsStore.addClonedApp(packageName)
                true
            }
        }

        // Standalone mode — just register in local store
        clonedAppsStore.addClonedApp(packageName)
        Log.i(TAG, "Cloned app (standalone): $packageName")
        return true
    }

    /**
     * Remove a cloned app.
     */
    suspend fun uncloneApp(packageName: String): Boolean {
        if (isWorkProfileActive()) {
            try {
                devicePolicyManager.setApplicationHidden(adminComponent, packageName, true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to hide app in work profile: $packageName", e)
            }
        }

        clonedAppsStore.removeClonedApp(packageName)
        Log.i(TAG, "Uncloned app: $packageName")
        return true
    }

    /**
     * Freeze a cloned app (disable it to save resources).
     */
    suspend fun freezeApp(packageName: String): Boolean {
        if (isWorkProfileActive()) {
            try {
                devicePolicyManager.setApplicationHidden(adminComponent, packageName, true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to freeze app in work profile: $packageName", e)
            }
        }

        clonedAppsStore.toggleFrozen(packageName)
        Log.i(TAG, "Froze app: $packageName")
        return true
    }

    /**
     * Unfreeze a cloned app (enable it again).
     */
    suspend fun unfreezeApp(packageName: String): Boolean {
        if (isWorkProfileActive()) {
            try {
                devicePolicyManager.setApplicationHidden(adminComponent, packageName, false)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unfreeze app in work profile: $packageName", e)
            }
        }

        clonedAppsStore.toggleFrozen(packageName)
        Log.i(TAG, "Unfroze app: $packageName")
        return true
    }

    /**
     * Get all cloned apps. Reads from local store and resolves
     * against installed packages to get icons and names.
     */
    suspend fun getClonedApps(): List<ClonedApp> = withContext(Dispatchers.IO) {
        try {
            val clonedPackages = clonedAppsStore.getClonedPackagesSnapshot()
            val frozenPackages = clonedAppsStore.getFrozenPackagesSnapshot()

            if (clonedPackages.isEmpty()) return@withContext emptyList()

            val apps = clonedPackages.mapNotNull { pkgName ->
                try {
                    val appInfo = packageManager.getApplicationInfo(pkgName, 0)
                    ClonedApp(
                        packageName = pkgName,
                        appName = appInfo.loadLabel(packageManager).toString(),
                        icon = try { appInfo.loadIcon(packageManager) } catch (e: Exception) { null },
                        isFrozen = pkgName in frozenPackages,
                        storageUsedBytes = try {
                            java.io.File(appInfo.sourceDir).length()
                        } catch (e: Exception) { 0L }
                    )
                } catch (e: PackageManager.NameNotFoundException) {
                    // App was uninstalled — remove from our store
                    clonedAppsStore.removeClonedApp(pkgName)
                    null
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to resolve cloned app: $pkgName", e)
                    null
                }
            }.sortedBy { it.appName.lowercase() }

            apps
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get cloned apps", e)
            emptyList()
        }
    }

    /**
     * Launch a cloned app.
     */
    fun launchClonedApp(packageName: String): Boolean {
        return try {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                true
            } else {
                Log.w(TAG, "No launch intent for $packageName")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to launch cloned app: $packageName", e)
            false
        }
    }

    /**
     * Create a home screen shortcut for a cloned app.
     */
    fun createShortcut(packageName: String, appName: String): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val shortcutManager = context.getSystemService(
                    android.content.pm.ShortcutManager::class.java
                )
                if (shortcutManager?.isRequestPinShortcutSupported == true) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                        ?: return false
                    intent.action = Intent.ACTION_MAIN

                    val shortcutInfo = android.content.pm.ShortcutInfo.Builder(
                        context, "clone_$packageName"
                    )
                        .setShortLabel("$appName (Clone)")
                        .setLongLabel("$appName — Open DualSpace Clone")
                        .setIntent(intent)
                        .build()

                    shortcutManager.requestPinShortcut(shortcutInfo, null)
                    true
                } else {
                    false
                }
            } else {
                // Legacy shortcut creation
                val intent = Intent("com.android.launcher.action.INSTALL_SHORTCUT").apply {
                    putExtra(Intent.EXTRA_SHORTCUT_NAME, "$appName (Clone)")
                    putExtra(
                        Intent.EXTRA_SHORTCUT_INTENT,
                        packageManager.getLaunchIntentForPackage(packageName)
                    )
                }
                context.sendBroadcast(intent)
                true
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create shortcut for $packageName", e)
            false
        }
    }

    /**
     * Destroy the entire Work Profile (removes all cloned apps and data).
     */
    suspend fun destroyWorkProfile(): Boolean {
        // Clear local store regardless
        clonedAppsStore.clearAll()

        if (!isWorkProfileActive()) {
            Log.i(TAG, "No active work profile — cleared local store only")
            return true
        }

        return try {
            devicePolicyManager.wipeData(0)
            Log.i(TAG, "Work Profile destroyed")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to destroy Work Profile", e)
            false
        }
    }
}
