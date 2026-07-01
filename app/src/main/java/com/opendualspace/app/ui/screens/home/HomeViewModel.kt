package com.opendualspace.app.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.opendualspace.app.data.model.ClonedApp
import com.opendualspace.app.data.model.ResourceUsage
import com.opendualspace.app.data.repository.AppRepository
import com.opendualspace.app.domain.manager.WorkProfileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class HomeUiState(
    val clonedApps: List<ClonedApp> = emptyList(),
    val resourceUsage: ResourceUsage = ResourceUsage(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val workProfileManager = WorkProfileManager(application)
    private val appRepository = AppRepository(application)

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Always load cloned apps from local store (works with or without work profile)
                val clonedApps = workProfileManager.getClonedApps()

                // Always load resource usage
                val resourceUsage = appRepository.getResourceUsage().copy(
                    clonedAppCount = clonedApps.size,
                    frozenAppCount = clonedApps.count { it.isFrozen }
                )

                _uiState.value = HomeUiState(
                    clonedApps = clonedApps,
                    resourceUsage = resourceUsage,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.message}"
                )
            }
        }
    }

    fun launchApp(packageName: String) {
        workProfileManager.launchClonedApp(packageName)
    }

    fun toggleFreeze(packageName: String) {
        viewModelScope.launch {
            val app = _uiState.value.clonedApps.find { it.packageName == packageName }
            if (app != null) {
                val success = if (app.isFrozen) {
                    workProfileManager.unfreezeApp(packageName)
                } else {
                    workProfileManager.freezeApp(packageName)
                }

                if (success) {
                    loadData() // Refresh
                }
            }
        }
    }

    fun uncloneApp(packageName: String) {
        viewModelScope.launch {
            val success = workProfileManager.uncloneApp(packageName)
            if (success) {
                loadData()
            }
        }
    }

    fun addShortcut(packageName: String, appName: String) {
        workProfileManager.createShortcut(packageName, appName)
    }
}
