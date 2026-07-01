package com.opendualspace.app.ui.screens.applist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.opendualspace.app.data.model.AppCategory
import com.opendualspace.app.data.model.InstalledApp
import com.opendualspace.app.data.repository.AppRepository
import com.opendualspace.app.domain.manager.WorkProfileManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppListUiState(
    val allApps: List<InstalledApp> = emptyList(),
    val filteredApps: List<InstalledApp> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val showSystemApps: Boolean = false,
    val isLoading: Boolean = true,
    val cloningPackage: String? = null,
    val lastCloneResult: CloneResult? = null
)

data class CloneResult(
    val packageName: String,
    val appName: String,
    val success: Boolean
)

class AppListViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val workProfileManager = WorkProfileManager(application)

    private val _uiState = MutableStateFlow(AppListUiState())
    val uiState: StateFlow<AppListUiState> = _uiState.asStateFlow()

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val apps = appRepository.getInstalledApps(
                includeSystem = _uiState.value.showSystemApps,
                forceRefresh = true
            )

            // Mark already cloned apps from local store
            val clonedPackages = workProfileManager.clonedAppsStore
                .getClonedPackagesSnapshot()

            val markedApps = apps.map { app ->
                app.copy(isCloned = clonedPackages.contains(app.packageName))
            }

            _uiState.value = _uiState.value.copy(
                allApps = markedApps,
                isLoading = false
            )

            applyFilters()
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun selectCategory(category: String?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    fun toggleSystemApps(show: Boolean) {
        _uiState.value = _uiState.value.copy(showSystemApps = show)
        loadApps()
    }

    fun cloneApp(packageName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(cloningPackage = packageName)

            val appName = _uiState.value.allApps
                .find { it.packageName == packageName }?.appName ?: "App"

            val success = workProfileManager.cloneApp(packageName)

            if (success) {
                // Mark as cloned in UI
                val updatedApps = _uiState.value.allApps.map { app ->
                    if (app.packageName == packageName) app.copy(isCloned = true)
                    else app
                }
                _uiState.value = _uiState.value.copy(
                    allApps = updatedApps,
                    cloningPackage = null,
                    lastCloneResult = CloneResult(packageName, appName, true)
                )
                applyFilters()
            } else {
                _uiState.value = _uiState.value.copy(
                    cloningPackage = null,
                    lastCloneResult = CloneResult(packageName, appName, false)
                )
            }
        }
    }

    fun clearCloneResult() {
        _uiState.value = _uiState.value.copy(lastCloneResult = null)
    }

    private fun applyFilters() {
        val state = _uiState.value
        var filtered = state.allApps

        // Search filter
        if (state.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.appName.contains(state.searchQuery, ignoreCase = true) ||
                it.packageName.contains(state.searchQuery, ignoreCase = true)
            }
        }

        // Category filter
        state.selectedCategory?.let { categoryName ->
            val category = try {
                AppCategory.valueOf(categoryName.uppercase())
            } catch (e: Exception) {
                null
            }
            if (category != null) {
                filtered = filtered.filter { it.category == category }
            }
        }

        _uiState.value = state.copy(filteredApps = filtered)
    }
}
