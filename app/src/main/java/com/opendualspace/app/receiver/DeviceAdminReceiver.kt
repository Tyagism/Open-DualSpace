package com.opendualspace.app.receiver

import android.app.admin.DeviceAdminReceiver
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

/**
 * Device Admin Receiver that handles Work Profile (Managed Profile) lifecycle events.
 * This is the core component that makes app cloning possible through Android's
 * native profile isolation mechanism.
 */
class DeviceAdminReceiver : DeviceAdminReceiver() {

    companion object {
        private const val TAG = "DualSpaceAdmin"

        fun getComponentName(context: Context): ComponentName {
            return ComponentName(context, DeviceAdminReceiver::class.java)
        }
    }

    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Log.i(TAG, "Device admin enabled")
    }

    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Log.i(TAG, "Device admin disabled")
    }

    override fun onProfileProvisioningComplete(context: Context, intent: Intent) {
        super.onProfileProvisioningComplete(context, intent)
        Log.i(TAG, "Work Profile provisioning complete")

        // Enable the managed profile
        val manager = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val componentName = getComponentName(context)

        try {
            // Add cross-profile intent filter so primary profile can send intents to the work profile
            val filter = android.content.IntentFilter().apply {
                addAction(com.opendualspace.app.service.WorkProfileActionActivity.ACTION_CLONE)
                addAction(com.opendualspace.app.service.WorkProfileActionActivity.ACTION_UNCLONE)
                addAction(com.opendualspace.app.service.WorkProfileActionActivity.ACTION_FREEZE)
                addAction(com.opendualspace.app.service.WorkProfileActionActivity.ACTION_UNFREEZE)
            }
            manager.addCrossProfileIntentFilter(
                componentName,
                filter,
                DevicePolicyManager.FLAG_MANAGED_CAN_ACCESS_PARENT or DevicePolicyManager.FLAG_PARENT_CAN_ACCESS_MANAGED
            )

            // Enable the profile
            manager.setProfileName(componentName, "Open DualSpace")
            manager.setProfileEnabled(componentName)

            Log.i(TAG, "Work Profile enabled successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to enable Work Profile", e)
        }
    }

    override fun onDisableRequested(context: Context, intent: Intent): CharSequence {
        return "Disabling Open DualSpace admin will remove all cloned app data. Are you sure?"
    }

    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Log.d(TAG, "Lock task mode entering for $pkg")
    }

    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Log.d(TAG, "Lock task mode exiting")
    }
}
