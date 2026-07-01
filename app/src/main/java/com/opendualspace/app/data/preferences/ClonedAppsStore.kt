package com.opendualspace.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.clonedAppsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "cloned_apps_store"
)

/**
 * Persists the set of package names the user has marked as "cloned".
 * Used as the source of truth when a real Work Profile is not available.
 */
class ClonedAppsStore(private val context: Context) {

    companion object {
        private val KEY_CLONED_PACKAGES = stringSetPreferencesKey("cloned_packages")
        private val KEY_FROZEN_PACKAGES = stringSetPreferencesKey("frozen_packages")
    }

    /** All package names that the user has cloned. */
    val clonedPackages: Flow<Set<String>> = context.clonedAppsDataStore.data.map { prefs ->
        prefs[KEY_CLONED_PACKAGES] ?: emptySet()
    }

    /** All package names that the user has frozen. */
    val frozenPackages: Flow<Set<String>> = context.clonedAppsDataStore.data.map { prefs ->
        prefs[KEY_FROZEN_PACKAGES] ?: emptySet()
    }

    /** Add a package to the cloned set. */
    suspend fun addClonedApp(packageName: String) {
        context.clonedAppsDataStore.edit { prefs ->
            val current = prefs[KEY_CLONED_PACKAGES] ?: emptySet()
            prefs[KEY_CLONED_PACKAGES] = current + packageName
        }
    }

    /** Remove a package from the cloned set (and frozen set). */
    suspend fun removeClonedApp(packageName: String) {
        context.clonedAppsDataStore.edit { prefs ->
            val cloned = prefs[KEY_CLONED_PACKAGES] ?: emptySet()
            val frozen = prefs[KEY_FROZEN_PACKAGES] ?: emptySet()
            prefs[KEY_CLONED_PACKAGES] = cloned - packageName
            prefs[KEY_FROZEN_PACKAGES] = frozen - packageName
        }
    }

    /** Check if a package is cloned. */
    suspend fun isCloned(packageName: String): Boolean {
        return context.clonedAppsDataStore.data.first()[KEY_CLONED_PACKAGES]
            ?.contains(packageName) ?: false
    }

    /** Toggle frozen state for a package. Returns new frozen state. */
    suspend fun toggleFrozen(packageName: String): Boolean {
        var nowFrozen = false
        context.clonedAppsDataStore.edit { prefs ->
            val frozen = prefs[KEY_FROZEN_PACKAGES] ?: emptySet()
            if (packageName in frozen) {
                prefs[KEY_FROZEN_PACKAGES] = frozen - packageName
                nowFrozen = false
            } else {
                prefs[KEY_FROZEN_PACKAGES] = frozen + packageName
                nowFrozen = true
            }
        }
        return nowFrozen
    }

    /** Get a snapshot of all cloned packages. */
    suspend fun getClonedPackagesSnapshot(): Set<String> {
        return context.clonedAppsDataStore.data.first()[KEY_CLONED_PACKAGES] ?: emptySet()
    }

    /** Get a snapshot of all frozen packages. */
    suspend fun getFrozenPackagesSnapshot(): Set<String> {
        return context.clonedAppsDataStore.data.first()[KEY_FROZEN_PACKAGES] ?: emptySet()
    }

    /** Clear all cloned and frozen data. */
    suspend fun clearAll() {
        context.clonedAppsDataStore.edit { prefs ->
            prefs.remove(KEY_CLONED_PACKAGES)
            prefs.remove(KEY_FROZEN_PACKAGES)
        }
    }
}
