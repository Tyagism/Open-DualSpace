package com.opendualspace.app.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opendualspace.app.ui.components.*
import com.opendualspace.app.ui.theme.DualSpaceTheme

/**
 * Home screen / Dashboard showing cloned apps grid and resource usage.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onAddClone: () -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showUncloneDialog by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (uiState.isLoading) {
            // Loading skeleton
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }
                item {
                    ShimmerLoading(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )
                }
                items(3) {
                    ShimmerLoading(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Header
                item {
                    Text(
                        text = "My Cloned Apps",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Ad banner placeholder (top)
                item {
                    AdBannerPlaceholder(
                        modifier = Modifier.padding(vertical = 4.dp),
                        adUnitId = "ca-app-pub-XXXXXXXX/banner-top"
                    )
                }

                // Resource usage card
                item {
                    ResourceUsageCard(
                        totalRamMB = uiState.resourceUsage.totalRamMB,
                        usedRamMB = uiState.resourceUsage.usedRamMB,
                        totalStorageBytes = uiState.resourceUsage.totalStorageBytes,
                        usedStorageBytes = uiState.resourceUsage.usedStorageBytes,
                        clonedAppCount = uiState.resourceUsage.clonedAppCount,
                        frozenAppCount = uiState.resourceUsage.frozenAppCount
                    )
                }

                // Error message
                uiState.errorMessage?.let { error ->
                    item {
                        GlassmorphismCard(
                            modifier = Modifier.fillMaxWidth(),
                            enableLiquidAnimation = false
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Warning,
                                    contentDescription = "Error",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = error,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }

                // Cloned apps list
                if (uiState.clonedApps.isEmpty()) {
                    item {
                        EmptyStateCard(onAddClone = onAddClone)
                    }
                } else {
                    item {
                        Text(
                            text = "${uiState.clonedApps.size} app${if (uiState.clonedApps.size != 1) "s" else ""} cloned",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    items(
                        items = uiState.clonedApps,
                        key = { it.packageName }
                    ) { app ->
                        ClonedAppCard(
                            app = app,
                            onLaunch = { viewModel.launchApp(app.packageName) },
                            onToggleFreeze = { viewModel.toggleFreeze(app.packageName) },
                            onUnclone = { showUncloneDialog = app.packageName },
                            onAddShortcut = {
                                viewModel.addShortcut(app.packageName, app.appName)
                            },
                            modifier = Modifier.animateItem()
                        )
                    }
                }

                // Ad banner placeholder (bottom)
                item {
                    AdBannerPlaceholder(
                        modifier = Modifier.padding(vertical = 8.dp),
                        adUnitId = "ca-app-pub-XXXXXXXX/banner-bottom"
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) } // FAB space
            }
        }

        // Floating action button
        FloatingActionButton(
            onClick = onAddClone,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add Clone")
        }
    }

    // Unclone confirmation dialog
    showUncloneDialog?.let { packageName ->
        val appName = uiState.clonedApps
            .find { it.packageName == packageName }?.appName ?: "this app"

        AlertDialog(
            onDismissRequest = { showUncloneDialog = null },
            title = { Text("Remove Clone?") },
            text = {
                Text("This will delete all data for $appName in the dual space. This cannot be undone.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.uncloneApp(packageName)
                        showUncloneDialog = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUncloneDialog = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun EmptyStateCard(onAddClone: () -> Unit) {
    GlassmorphismCard(
        modifier = Modifier.fillMaxWidth(),
        enableLiquidAnimation = true
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.ContentCopy,
                    contentDescription = "Clone",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "No Cloned Apps Yet",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap the + button to clone your first app\nand run dual accounts!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onAddClone,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Clone", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
