package com.opendualspace.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.opendualspace.app.BuildConfig
import com.opendualspace.app.data.preferences.AppPreferences
import com.opendualspace.app.domain.manager.WorkProfileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val isDarkMode: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val isAutoStartEnabled: Boolean = true,
    val showSystemApps: Boolean = false,
    val isWorkProfileActive: Boolean = false,
    val hasClonedApps: Boolean = false,
    val appVersion: String = "1.0.0",
    val showDestroyDialog: Boolean = false
)

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = AppPreferences(application)
    private val workProfileManager = WorkProfileManager(application)

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            val clonedPackages = workProfileManager.clonedAppsStore.getClonedPackagesSnapshot()

            _uiState.value = SettingsUiState(
                isDarkMode = preferences.isDarkMode.first(),
                isAppLockEnabled = preferences.isAppLockEnabled.first(),
                isAutoStartEnabled = preferences.isAutoStartEnabled.first(),
                showSystemApps = preferences.showSystemApps.first(),
                isWorkProfileActive = workProfileManager.isWorkProfileActive(),
                hasClonedApps = clonedPackages.isNotEmpty(),
                appVersion = try {
                    BuildConfig.VERSION_NAME
                } catch (e: Exception) {
                    "1.0.0"
                }
            )
        }
    }

    fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setDarkMode(enabled)
            _uiState.value = _uiState.value.copy(isDarkMode = enabled)
        }
    }

    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAppLockEnabled(enabled)
            _uiState.value = _uiState.value.copy(isAppLockEnabled = enabled)
        }
    }

    fun toggleAutoStart(enabled: Boolean) {
        viewModelScope.launch {
            preferences.setAutoStart(enabled)
            _uiState.value = _uiState.value.copy(isAutoStartEnabled = enabled)
        }
    }

    fun toggleSystemApps(show: Boolean) {
        viewModelScope.launch {
            preferences.setShowSystemApps(show)
            _uiState.value = _uiState.value.copy(showSystemApps = show)
        }
    }

    fun showDestroyDialog() {
        _uiState.value = _uiState.value.copy(showDestroyDialog = true)
    }

    fun hideDestroyDialog() {
        _uiState.value = _uiState.value.copy(showDestroyDialog = false)
    }

    fun destroyWorkProfile() {
        viewModelScope.launch {
            workProfileManager.destroyWorkProfile()
            _uiState.value = _uiState.value.copy(
                isWorkProfileActive = false,
                hasClonedApps = false,
                showDestroyDialog = false
            )
        }
    }
}
