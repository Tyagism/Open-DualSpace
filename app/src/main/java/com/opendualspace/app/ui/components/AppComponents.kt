package com.opendualspace.app.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.opendualspace.app.data.model.ClonedApp
import com.opendualspace.app.data.model.InstalledApp
import com.opendualspace.app.ui.theme.DualSpaceTheme

/**
 * Card for displaying an installed app in the "Add Clone" list.
 * Features: app icon, name, package size, clone button with animation.
 */
@Composable
fun AppCard(
    app: InstalledApp,
    onClone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    GlassmorphismCard(
        modifier = modifier
            .scale(scale)
            .fillMaxWidth(),
        enableLiquidAnimation = false
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            AppIcon(
                drawable = app.icon,
                size = 48.dp,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.width(14.dp))

            // App info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formatBytes(app.apkSizeBytes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Clone button
            if (app.isCloned) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = extendedColors.success.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Cloned",
                            tint = extendedColors.success,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cloned",
                            style = MaterialTheme.typography.labelMedium,
                            color = extendedColors.success,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
                FilledTonalButton(
                    onClick = onClone,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Icon(
                        Icons.Filled.ContentCopy,
                        contentDescription = "Clone",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Clone",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

/**
 * Card for displaying a cloned app on the home dashboard.
 * Features: launch, freeze/unfreeze toggle, storage/RAM usage, dropdown menu.
 */
@Composable
fun ClonedAppCard(
    app: ClonedApp,
    onLaunch: () -> Unit,
    onToggleFreeze: () -> Unit,
    onUnclone: () -> Unit,
    onAddShortcut: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors
    var menuExpanded by remember { mutableStateOf(false) }

    val frozenAlpha by animateFloatAsState(
        targetValue = if (app.isFrozen) 0.5f else 1f,
        animationSpec = tween(300),
        label = "frozen_alpha"
    )

    GlassmorphismCard(
        modifier = modifier
            .alpha(frozenAlpha)
            .fillMaxWidth()
            .clickable(enabled = !app.isFrozen) { onLaunch() },
        enableLiquidAnimation = !app.isFrozen
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App icon with frozen overlay
                Box {
                    AppIcon(
                        drawable = app.icon,
                        size = 52.dp
                    )
                    if (app.isFrozen) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(extendedColors.frozenOverlay),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AcUnit,
                                contentDescription = "Frozen",
                                tint = extendedColors.frozenBadge,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                // App info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Storage usage
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.Storage,
                                contentDescription = "Storage",
                                modifier = Modifier.size(12.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = formatBytes(app.storageUsedBytes),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // RAM usage
                        if (app.ramUsageMB > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Outlined.Memory,
                                    contentDescription = "RAM",
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = String.format("%.1f MB", app.ramUsageMB),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                // Freeze toggle
                AnimatedFreezeToggle(
                    isFrozen = app.isFrozen,
                    onToggle = onToggleFreeze
                )

                // Overflow menu
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            Icons.Filled.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Launch") },
                            onClick = {
                                menuExpanded = false
                                onLaunch()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Launch, contentDescription = null)
                            },
                            enabled = !app.isFrozen
                        )
                        DropdownMenuItem(
                            text = { Text("Add to Home") },
                            onClick = {
                                menuExpanded = false
                                onAddShortcut()
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.AddToHomeScreen, contentDescription = null)
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Remove Clone") },
                            onClick = {
                                menuExpanded = false
                                onUnclone()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            },
                            colors = MenuDefaults.itemColors(
                                textColor = MaterialTheme.colorScheme.error
                            )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Animated freeze/unfreeze toggle with liquid glass effect.
 */
@Composable
fun AnimatedFreezeToggle(
    isFrozen: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors

    val toggleColor by animateColorAsState(
        targetValue = if (isFrozen) extendedColors.frozenBadge
        else extendedColors.success,
        animationSpec = tween(400),
        label = "toggle_color"
    )

    val iconRotation by animateFloatAsState(
        targetValue = if (isFrozen) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "icon_rotation"
    )

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(toggleColor.copy(alpha = 0.15f))
    ) {
        Icon(
            imageVector = if (isFrozen) Icons.Filled.AcUnit else Icons.Filled.PlayArrow,
            contentDescription = if (isFrozen) "Unfreeze" else "Freeze",
            tint = toggleColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Resource usage card showing RAM and storage for DualSpace.
 */
@Composable
fun ResourceUsageCard(
    totalRamMB: Float,
    usedRamMB: Float,
    totalStorageBytes: Long,
    usedStorageBytes: Long,
    clonedAppCount: Int,
    frozenAppCount: Int,
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors

    GlassmorphismCard(
        modifier = modifier.fillMaxWidth(),
        enableLiquidAnimation = true
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Resource Usage",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    icon = Icons.Outlined.Apps,
                    value = "$clonedAppCount",
                    label = "Cloned",
                    color = MaterialTheme.colorScheme.primary
                )
                StatItem(
                    icon = Icons.Outlined.AcUnit,
                    value = "$frozenAppCount",
                    label = "Frozen",
                    color = extendedColors.frozenBadge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // RAM usage bar
            UsageBar(
                label = "RAM",
                icon = Icons.Outlined.Memory,
                usedText = String.format("%.0f MB", usedRamMB),
                totalText = String.format("%.0f MB", totalRamMB),
                progress = if (totalRamMB > 0) usedRamMB / totalRamMB else 0f,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Storage usage bar
            UsageBar(
                label = "Storage",
                icon = Icons.Outlined.Storage,
                usedText = formatBytes(usedStorageBytes),
                totalText = formatBytes(totalStorageBytes),
                progress = if (totalStorageBytes > 0) usedStorageBytes.toFloat() / totalStorageBytes else 0f,
                color = extendedColors.success
            )
        }
    }
}

@Composable
private fun StatItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun UsageBar(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    usedText: String,
    totalText: String,
    progress: Float,
    color: androidx.compose.ui.graphics.Color
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress"
    )

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = label,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "$usedText / $totalText",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color.copy(alpha = 0.12f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                color,
                                color.copy(alpha = 0.7f)
                            )
                        )
                    )
            )
        }
    }
}

/**
 * App icon renderer from Android Drawable.
 */
@Composable
fun AppIcon(
    drawable: Drawable?,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    if (drawable != null) {
        Image(
            bitmap = drawable.toBitmap(
                width = 128,
                height = 128
            ).asImageBitmap(),
            contentDescription = "App Icon",
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(14.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.Apps,
                contentDescription = "App",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(size / 2)
            )
        }
    }
}

/**
 * Search bar with glassmorphism design.
 */
@Composable
fun GlassSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "Search apps...",
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                text = placeholder,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        Icons.Filled.Clear,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = extendedColors.glassSurface,
            focusedContainerColor = extendedColors.glassSurface,
            unfocusedBorderColor = extendedColors.glassBorder.copy(alpha = 0.3f),
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        ),
        singleLine = true
    )
}

/**
 * Filter chip row for app categories.
 */
@Composable
fun CategoryChips(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Social", "Communication", "Games", "Media", "Tools", "Other")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = (category == "All" && selectedCategory == null) ||
                    category == selectedCategory

            FilterChip(
                selected = isSelected,
                onClick = {
                    onCategorySelected(if (category == "All") null else category)
                },
                label = {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    }
}

/**
 * Bottom navigation bar with glassmorphism effect.
 */
@Composable
fun GlassBottomNavBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val extendedColors = DualSpaceTheme.extendedColors

    NavigationBar(
        modifier = modifier,
        containerColor = extendedColors.glassSurface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = selectedRoute == "home",
            onClick = { onNavigate("home") },
            icon = {
                Icon(
                    if (selectedRoute == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "apps",
            onClick = { onNavigate("apps") },
            icon = {
                Icon(
                    if (selectedRoute == "apps") Icons.Filled.Apps else Icons.Outlined.Apps,
                    contentDescription = "Apps"
                )
            },
            label = { Text("Apps") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
        )
        NavigationBarItem(
            selected = selectedRoute == "settings",
            onClick = { onNavigate("settings") },
            icon = {
                Icon(
                    if (selectedRoute == "settings") Icons.Filled.Settings
                    else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = { Text("Settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
        )
    }
}

// ==========================================
// Utility functions
// ==========================================

fun formatBytes(bytes: Long): String {
    if (bytes <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format(
        "%.1f %s",
        bytes / Math.pow(1024.0, digitGroups.toDouble()),
        units[digitGroups.coerceAtMost(units.size - 1)]
    )
}
