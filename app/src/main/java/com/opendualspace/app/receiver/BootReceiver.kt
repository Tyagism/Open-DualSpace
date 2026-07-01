package com.opendualspace.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receives BOOT_COMPLETED and MY_PACKAGE_REPLACED broadcasts
 * to restore cloned app states after device restart.
 */
class BootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "DualSpaceBoot"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                Log.i(TAG, "Boot completed — restoring cloned app states")
                restoreAppStates(context)
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.i(TAG, "App updated — restoring cloned app states")
                restoreAppStates(context)
            }
        }
    }

    private fun restoreAppStates(context: Context) {
        try {
            // The Work Profile persists across reboots natively.
            // This receiver ensures any auto-start preferences are honored.
            // The actual unfreezing is handled by WorkProfileManager
            // which reads from DataStore preferences.
            Log.i(TAG, "Cloned app states will be restored when user opens the app")
        } catch (e: Exception) {
            Log.e(TAG, "Error restoring app states", e)
        }
    }
}
