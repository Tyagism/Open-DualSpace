package com.opendualspace.app.ui.screens.applist

import androidx.compose.animation.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opendualspace.app.ui.components.*

/**
 * Screen for browsing installed apps and cloning them.
 * Features: search, category filter, clone button per app, snackbar feedback.
 */
@Composable
fun AppListScreen(
    viewModel: AppListViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show snackbar on clone result
    LaunchedEffect(uiState.lastCloneResult) {
        uiState.lastCloneResult?.let { result ->
            val message = if (result.success) {
                "${result.appName} cloned successfully!"
            } else {
                "Failed to clone ${result.appName}"
            }
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearCloneResult()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
                .statusBarsPadding()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Select App to Clone",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            GlassSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                placeholder = "Search apps..."
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category filter chips (horizontally scrollable)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChips(
                    selectedCategory = uiState.selectedCategory,
                    onCategorySelected = { viewModel.selectCategory(it) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // App count
            if (!uiState.isLoading) {
                Text(
                    text = "${uiState.filteredApps.size} apps found",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            // Ad banner placeholder
            AdBannerPlaceholder(
                modifier = Modifier.padding(vertical = 8.dp),
                adUnitId = "ca-app-pub-XXXXXXXX/applist-banner"
            )

            // App list
            if (uiState.isLoading) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(8) {
                        ShimmerLoading(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(72.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 100.dp) // Nav bar space
                ) {
                    items(
                        items = uiState.filteredApps,
                        key = { it.packageName }
                    ) { app ->
                        AppCard(
                            app = app,
                            onClone = { viewModel.cloneApp(app.packageName) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
            }
        }
    }
}
