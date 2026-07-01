package com.opendualspace.app.ui.screens.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.opendualspace.app.ui.components.AdBannerPlaceholder
import com.opendualspace.app.ui.components.GlassmorphismCard
import com.opendualspace.app.ui.theme.DualSpaceTheme

/**
 * Settings screen with theme toggle, security, behavior, and danger zone.
 */
@Composable
fun SettingsScreen(
    onThemeToggle: (Boolean) -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .statusBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Title
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Appearance section
        item {
            SettingsSection(title = "Appearance") {
                SettingsToggleItem(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Switch between light and dark themes",
                    isChecked = uiState.isDarkMode,
                    onToggle = {
                        viewModel.toggleDarkMode(it)
                        onThemeToggle(it)
                    }
                )
            }
        }

        // Security section
        item {
            SettingsSection(title = "Security") {
                SettingsToggleItem(
                    icon = Icons.Outlined.Lock,
                    title = "App Lock",
                    subtitle = "Require PIN or biometric to open DualSpace",
                    isChecked = uiState.isAppLockEnabled,
                    onToggle = { viewModel.toggleAppLock(it) }
                )
            }
        }

        // Behavior section
        item {
            SettingsSection(title = "Behavior") {
                SettingsToggleItem(
                    icon = Icons.Outlined.RestartAlt,
                    title = "Auto-Start on Boot",
                    subtitle = "Re-enable cloned apps when device restarts",
                    isChecked = uiState.isAutoStartEnabled,
                    onToggle = { viewModel.toggleAutoStart(it) }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                SettingsToggleItem(
                    icon = Icons.Outlined.Android,
                    title = "Show System Apps",
                    subtitle = "Include system apps in the app list",
                    isChecked = uiState.showSystemApps,
                    onToggle = { viewModel.toggleSystemApps(it) }
                )
            }
        }

        // Ad placeholder
        item {
            AdBannerPlaceholder(
                adUnitId = "ca-app-pub-XXXXXXXX/settings-banner"
            )
        }

        // About section
        item {
            SettingsSection(title = "About") {
                SettingsClickItem(
                    icon = Icons.Outlined.Code,
                    title = "View on GitHub",
                    subtitle = "Open source — contribute or report issues"
                ) {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/opendualspace/open-dualspace")
                        )
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No browser app found", Toast.LENGTH_SHORT).show()
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                SettingsClickItem(
                    icon = Icons.Outlined.Star,
                    title = "Rate on Play Store",
                    subtitle = "Help us improve with your feedback"
                ) {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.opendualspace.app")
                        )
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=com.opendualspace.app")
                            )
                            context.startActivity(intent)
                        } catch (e2: ActivityNotFoundException) {
                            Toast.makeText(context, "Unable to open Play Store", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                SettingsClickItem(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data"
                ) {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/opendualspace/open-dualspace/blob/main/PRIVACY.md")
                        )
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(context, "No browser app found", Toast.LENGTH_SHORT).show()
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
                SettingsInfoItem(
                    icon = Icons.Outlined.Info,
                    title = "Version",
                    value = uiState.appVersion
                )
            }
        }

        // Danger zone — show if there are cloned apps OR work profile is active
        if (uiState.hasClonedApps || uiState.isWorkProfileActive) {
            item {
                GlassmorphismCard(
                    modifier = Modifier.fillMaxWidth(),
                    enableLiquidAnimation = false
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = "Danger Zone",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Removing DualSpace will clear all cloned app data. This action is irreversible.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedButton(
                            onClick = { viewModel.showDestroyDialog() },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            ),
                            border = ButtonDefaults.outlinedButtonBorder(
                                enabled = true
                            )
                        ) {
                            Icon(Icons.Filled.DeleteForever, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Remove DualSpace", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }

    // Destroy confirmation dialog
    if (uiState.showDestroyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDestroyDialog() },
            title = {
                Text(
                    "Remove DualSpace?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "This will delete all cloned app data. " +
                    "This action is irreversible. Are you absolutely sure?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.destroyWorkProfile() },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete Everything", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDestroyDialog() }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )
        GlassmorphismCard(
            modifier = Modifier.fillMaxWidth(),
            enableLiquidAnimation = false
        ) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
                uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = androidx.compose.ui.graphics.Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
