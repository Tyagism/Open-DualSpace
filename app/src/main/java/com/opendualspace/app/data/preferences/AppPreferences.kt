package com.opendualspace.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "open_dualspace_preferences"
)

/**
 * DataStore wrapper for app preferences.
 */
class AppPreferences(private val context: Context) {

    companion object {
        private val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        private val KEY_APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled")
        private val KEY_PIN_HASH = stringPreferencesKey("pin_hash")
        private val KEY_AUTO_START = booleanPreferencesKey("auto_start")
        private val KEY_SHOW_SYSTEM_APPS = booleanPreferencesKey("show_system_apps")
        private val KEY_SETUP_COMPLETED = booleanPreferencesKey("setup_completed")
        private val KEY_SORT_ORDER = stringPreferencesKey("sort_order")
        private val KEY_USE_BIOMETRIC = booleanPreferencesKey("use_biometric")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_MODE] ?: false // Default: light theme
    }

    val isAppLockEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_APP_LOCK_ENABLED] ?: false
    }

    val pinHash: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[KEY_PIN_HASH]
    }

    val isAutoStartEnabled: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_AUTO_START] ?: true
    }

    val showSystemApps: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SHOW_SYSTEM_APPS] ?: false
    }

    val isSetupCompleted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_SETUP_COMPLETED] ?: false
    }

    val sortOrder: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_SORT_ORDER] ?: "name_asc"
    }

    val useBiometric: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_USE_BIOMETRIC] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    suspend fun setAppLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_APP_LOCK_ENABLED] = enabled }
    }

    suspend fun setPinHash(hash: String) {
        context.dataStore.edit { it[KEY_PIN_HASH] = hash }
    }

    suspend fun setAutoStart(enabled: Boolean) {
        context.dataStore.edit { it[KEY_AUTO_START] = enabled }
    }

    suspend fun setShowSystemApps(show: Boolean) {
        context.dataStore.edit { it[KEY_SHOW_SYSTEM_APPS] = show }
    }

    suspend fun setSetupCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_SETUP_COMPLETED] = completed }
    }

    suspend fun setSortOrder(order: String) {
        context.dataStore.edit { it[KEY_SORT_ORDER] = order }
    }

    suspend fun setUseBiometric(enabled: Boolean) {
        context.dataStore.edit { it[KEY_USE_BIOMETRIC] = enabled }
    }
}
