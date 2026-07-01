package com.opendualspace.app.data.model

import android.graphics.drawable.Drawable

/**
 * Represents an installed app that can be cloned.
 */
data class InstalledApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isSystemApp: Boolean = false,
    val isCloned: Boolean = false,
    val apkSizeBytes: Long = 0L,
    val category: AppCategory = AppCategory.OTHER
)

/**
 * Represents a cloned app running in the Work Profile.
 */
data class ClonedApp(
    val packageName: String,
    val appName: String,
    val icon: Drawable?,
    val isFrozen: Boolean = false,
    val clonedAt: Long = System.currentTimeMillis(),
    val storageUsedBytes: Long = 0L,
    val cacheUsedBytes: Long = 0L,
    val dataUsedBytes: Long = 0L,
    val ramUsageMB: Float = 0f
)

/**
 * App categories for filtering.
 */
enum class AppCategory(val displayName: String) {
    SOCIAL("Social"),
    GAMES("Games"),
    TOOLS("Tools"),
    MEDIA("Media"),
    COMMUNICATION("Communication"),
    PRODUCTIVITY("Productivity"),
    OTHER("Other");

    companion object {
        fun fromPackageName(packageName: String): AppCategory {
            return when {
                // Social media apps
                packageName.contains("instagram") ||
                packageName.contains("facebook") ||
                packageName.contains("twitter") ||
                packageName.contains("tiktok") ||
                packageName.contains("snapchat") ||
                packageName.contains("reddit") ||
                packageName.contains("pinterest") -> SOCIAL

                // Communication apps
                packageName.contains("whatsapp") ||
                packageName.contains("telegram") ||
                packageName.contains("signal") ||
                packageName.contains("discord") ||
                packageName.contains("messenger") ||
                packageName.contains("viber") ||
                packageName.contains("skype") -> COMMUNICATION

                // Games
                packageName.contains("game") ||
                packageName.contains("play") -> GAMES

                // Media
                packageName.contains("youtube") ||
                packageName.contains("spotify") ||
                packageName.contains("netflix") ||
                packageName.contains("music") ||
                packageName.contains("video") ||
                packageName.contains("gallery") ||
                packageName.contains("camera") -> MEDIA

                // Tools/Productivity
                packageName.contains("calculator") ||
                packageName.contains("calendar") ||
                packageName.contains("notes") ||
                packageName.contains("office") ||
                packageName.contains("drive") ||
                packageName.contains("docs") -> PRODUCTIVITY

                else -> OTHER
            }
        }
    }
}

/**
 * Resource usage summary for the DualSpace.
 */
data class ResourceUsage(
    val totalRamMB: Float = 0f,
    val usedRamMB: Float = 0f,
    val totalStorageBytes: Long = 0L,
    val usedStorageBytes: Long = 0L,
    val clonedAppCount: Int = 0,
    val frozenAppCount: Int = 0
)
