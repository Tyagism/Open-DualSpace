package com.opendualspace.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Extended colors for glassmorphism effects not covered by Material3
data class ExtendedColors(
    val glassSurface: Color,
    val glassBorder: Color,
    val glassHighlight: Color,
    val glassOverlay: Color,
    val gradientStart: Color,
    val gradientMiddle: Color,
    val gradientEnd: Color,
    val success: Color,
    val warning: Color,
    val info: Color,
    val cardBackground: Color,
    val chipSelected: Color,
    val chipUnselected: Color,
    val divider: Color,
    val frozenOverlay: Color,
    val frozenBadge: Color
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        glassSurface = Color.Transparent,
        glassBorder = Color.Transparent,
        glassHighlight = Color.Transparent,
        glassOverlay = Color.Transparent,
        gradientStart = Color.Transparent,
        gradientMiddle = Color.Transparent,
        gradientEnd = Color.Transparent,
        success = Color.Transparent,
        warning = Color.Transparent,
        info = Color.Transparent,
        cardBackground = Color.Transparent,
        chipSelected = Color.Transparent,
        chipUnselected = Color.Transparent,
        divider = Color.Transparent,
        frozenOverlay = Color.Transparent,
        frozenBadge = Color.Transparent
    )
}

private val LightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryVariant,
    onPrimaryContainer = LightOnBackground,
    secondary = LightSecondary,
    onSecondary = LightOnPrimary,
    tertiary = LightTertiary,
    onTertiary = LightOnPrimary,
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    error = LightError,
    onError = LightOnPrimary,
    outline = LightDivider,
    outlineVariant = LightSurfaceContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryVariant,
    onPrimaryContainer = DarkOnBackground,
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    tertiary = DarkTertiary,
    onTertiary = DarkOnPrimary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    error = DarkError,
    onError = DarkOnPrimary,
    outline = DarkDivider,
    outlineVariant = DarkSurfaceContainer
)

private val LightExtendedColors = ExtendedColors(
    glassSurface = LightGlassSurface,
    glassBorder = LightGlassBorder,
    glassHighlight = LightGlassHighlight,
    glassOverlay = LightGlassOverlay,
    gradientStart = LightGradientStart,
    gradientMiddle = LightGradientMiddle,
    gradientEnd = LightGradientEnd,
    success = LightSuccess,
    warning = LightWarning,
    info = LightInfo,
    cardBackground = LightCardBackground,
    chipSelected = LightChipSelected,
    chipUnselected = LightChipUnselected,
    divider = LightDivider,
    frozenOverlay = LightFrozenOverlay,
    frozenBadge = LightFrozenBadge
)

private val DarkExtendedColors = ExtendedColors(
    glassSurface = DarkGlassSurface,
    glassBorder = DarkGlassBorder,
    glassHighlight = DarkGlassHighlight,
    glassOverlay = DarkGlassOverlay,
    gradientStart = DarkGradientStart,
    gradientMiddle = DarkGradientMiddle,
    gradientEnd = DarkGradientEnd,
    success = DarkSuccess,
    warning = DarkWarning,
    info = DarkInfo,
    cardBackground = DarkCardBackground,
    chipSelected = DarkChipSelected,
    chipUnselected = DarkChipUnselected,
    divider = DarkDivider,
    frozenOverlay = DarkFrozenOverlay,
    frozenBadge = DarkFrozenBadge
)

@Composable
fun OpenDualSpaceTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    androidx.compose.runtime.CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

// Extension property for easy access to extended colors
object DualSpaceTheme {
    val extendedColors: ExtendedColors
        @Composable
        get() = LocalExtendedColors.current
}
