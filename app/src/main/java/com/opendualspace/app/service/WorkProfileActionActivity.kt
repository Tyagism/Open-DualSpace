package com.opendualspace.app.service

import android.app.Activity
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.opendualspace.app.receiver.DeviceAdminReceiver

/**
 * Transparent Activity that runs in the Work Profile.
 * Receives cross-profile intents from the primary profile to clone/freeze apps.
 * This is necessary because only the Profile Owner (this app in the work profile)
 * can call DevicePolicyManager.enableSystemApp().
 */
class WorkProfileActionActivity : Activity() {

    companion object {
        private const val TAG = "WorkProfileAction"
        const val ACTION_CLONE = "com.opendualspace.action.CLONE_APP"
        const val ACTION_UNCLONE = "com.opendualspace.action.UNCLONE_APP"
        const val ACTION_FREEZE = "com.opendualspace.action.FREEZE_APP"
        const val ACTION_UNFREEZE = "com.opendualspace.action.UNFREEZE_APP"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val action = intent?.action
        val packageName = intent?.getStringExtra(EXTRA_PACKAGE_NAME)

        if (packageName.isNullOrEmpty()) {
            finish()
            return
        }

        val dpm = getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val admin = DeviceAdminReceiver.getComponentName(this)

        if (!dpm.isProfileOwnerApp(this.packageName)) {
            Log.e(TAG, "Not profile owner! Cannot perform work profile actions.")
            finish()
            return
        }

        try {
            when (action) {
                ACTION_CLONE -> {
                    Log.i(TAG, "Enabling app in work profile: $packageName")
                    dpm.enableSystemApp(admin, packageName)
                }
                ACTION_UNCLONE, ACTION_FREEZE -> {
                    Log.i(TAG, "Hiding app in work profile: $packageName")
                    dpm.setApplicationHidden(admin, packageName, true)
                }
                ACTION_UNFREEZE -> {
                    Log.i(TAG, "Unhiding app in work profile: $packageName")
                    dpm.setApplicationHidden(admin, packageName, false)
                }
                else -> {
                    Log.w(TAG, "Unknown action: $action")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error performing work profile action $action for $packageName", e)
        }

        finish()
    }
}
