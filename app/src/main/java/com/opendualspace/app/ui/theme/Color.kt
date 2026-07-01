package com.opendualspace.app.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// LIGHT THEME — Apple-style Glassmorphism
// ==========================================

// Primary brand colors
val LightPrimary = Color(0xFF4F46E5)           // Indigo
val LightPrimaryVariant = Color(0xFF6366F1)    // Lighter indigo
val LightSecondary = Color(0xFF06B6D4)         // Cyan
val LightTertiary = Color(0xFF8B5CF6)          // Violet

// Background & Surface
val LightBackground = Color(0xFFF8FAFC)        // Cool white
val LightSurface = Color(0xFFFFFFFF)           // Pure white
val LightSurfaceVariant = Color(0xFFF1F5F9)    // Slate 100
val LightSurfaceContainer = Color(0xFFE2E8F0)  // Slate 200

// Glass effects (semi-transparent) — Apple-style frosted vibrancy
val LightGlassSurface = Color(0xD9FFFFFF)      // 85% white glass — slightly more opaque for readability
val LightGlassBorder = Color(0x40FFFFFF)       // 25% white border
val LightGlassHighlight = Color(0x1F4F46E5)    // 12% indigo highlight for liquid shimmer
val LightGlassOverlay = Color(0x0A000000)      // 4% black overlay

// Text
val LightOnBackground = Color(0xFF0F172A)      // Slate 900
val LightOnSurface = Color(0xFF1E293B)         // Slate 800
val LightOnSurfaceVariant = Color(0xFF64748B)  // Slate 500
val LightOnPrimary = Color(0xFFFFFFFF)         // White

// Accents
val LightSuccess = Color(0xFF10B981)           // Emerald
val LightWarning = Color(0xFFF59E0B)           // Amber
val LightError = Color(0xFFEF4444)             // Red
val LightInfo = Color(0xFF3B82F6)              // Blue

// Gradients — richer for liquid glass refraction
val LightGradientStart = Color(0xFF4F46E5)     // Indigo
val LightGradientMiddle = Color(0xFF7C3AED)    // Violet
val LightGradientEnd = Color(0xFF06B6D4)       // Cyan

// Card & Chip colors
val LightCardBackground = Color(0xE6FFFFFF)    // 90% white
val LightChipSelected = Color(0xFF4F46E5)      // Indigo
val LightChipUnselected = Color(0xFFE2E8F0)    // Slate 200
val LightDivider = Color(0xFFE2E8F0)           // Slate 200

// Frozen state
val LightFrozenOverlay = Color(0x4D94A3B8)     // 30% slate
val LightFrozenBadge = Color(0xFF94A3B8)       // Slate 400


// ==========================================
// DARK THEME — AMOLED Dark Glassmorphism
// Premium dark vibrancy à la iOS dark mode
// ==========================================

// Primary brand colors
val DarkPrimary = Color(0xFF818CF8)            // Indigo 400
val DarkPrimaryVariant = Color(0xFFA78BFA)     // Violet 400
val DarkSecondary = Color(0xFF22D3EE)          // Cyan 400
val DarkTertiary = Color(0xFFC084FC)           // Purple 400

// Background & Surface
val DarkBackground = Color(0xFF0A0E1A)         // Deep navy
val DarkSurface = Color(0xFF111827)            // Gray 900
val DarkSurfaceVariant = Color(0xFF1E293B)     // Slate 800
val DarkSurfaceContainer = Color(0xFF374151)   // Gray 700

// Glass effects (dark glass) — Apple dark vibrancy material
val DarkGlassSurface = Color(0xA61E293B)       // 65% slate glass — richer depth
val DarkGlassBorder = Color(0x30FFFFFF)        // 19% white border — subtle glow
val DarkGlassHighlight = Color(0x1F818CF8)     // 12% indigo highlight for liquid refraction
val DarkGlassOverlay = Color(0x26000000)       // 15% black overlay

// Text
val DarkOnBackground = Color(0xFFF8FAFC)       // Slate 50
val DarkOnSurface = Color(0xFFE2E8F0)          // Slate 200
val DarkOnSurfaceVariant = Color(0xFF94A3B8)   // Slate 400
val DarkOnPrimary = Color(0xFF0F172A)          // Slate 900

// Accents
val DarkSuccess = Color(0xFF34D399)            // Emerald 400
val DarkWarning = Color(0xFFFBBF24)            // Amber 400
val DarkError = Color(0xFFF87171)              // Red 400
val DarkInfo = Color(0xFF60A5FA)               // Blue 400

// Gradients — vibrant for dark liquid glass refraction
val DarkGradientStart = Color(0xFF818CF8)      // Indigo 400
val DarkGradientMiddle = Color(0xFFA78BFA)     // Violet 400
val DarkGradientEnd = Color(0xFF22D3EE)        // Cyan 400

// Card & Chip colors
val DarkCardBackground = Color(0xB3111827)     // 70% gray 900
val DarkChipSelected = Color(0xFF818CF8)       // Indigo 400
val DarkChipUnselected = Color(0xFF1E293B)     // Slate 800
val DarkDivider = Color(0xFF1E293B)            // Slate 800

// Frozen state
val DarkFrozenOverlay = Color(0x4D475569)      // 30% slate 600
val DarkFrozenBadge = Color(0xFF64748B)        // Slate 500
