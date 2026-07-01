package com.opendualspace.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.opendualspace.app.ui.theme.DualSpaceTheme

/**
 * Apple-style glassmorphism card with frosted glass effect, animated
 * liquid glass refraction shimmer, layered border highlights, and
 * subtle depth illusion. Inspired by iOS/visionOS vibrancy materials.
 */
@Composable
fun GlassmorphismCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    blurRadius: Dp = 16.dp,
    enableLiquidAnimation: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    val extendedColors = DualSpaceTheme.extendedColors
    val shape = RoundedCornerShape(cornerRadius)

    // Primary liquid glass shimmer — slow, organic movement
    val infiniteTransition = rememberInfiniteTransition(label = "liquid_glass")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )

    // Secondary refraction — subtle chromatic shift for depth
    val refractionOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 7000,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "refraction"
    )

    val liquidBrush = if (enableLiquidAnimation) {
        Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                extendedColors.glassHighlight.copy(alpha = 0.12f),
                Color.White.copy(alpha = 0.06f),
                extendedColors.glassHighlight.copy(alpha = 0.10f),
                Color.Transparent
            ),
            start = Offset(shimmerOffset * 1000f, shimmerOffset * 200f),
            end = Offset(shimmerOffset * 1000f + 600f, shimmerOffset * 200f + 600f)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    // Subtle refraction overlay — gives a "light bending through glass" look
    val refractionBrush = if (enableLiquidAnimation) {
        Brush.radialGradient(
            colors = listOf(
                extendedColors.gradientStart.copy(alpha = 0.04f),
                Color.Transparent,
                extendedColors.gradientEnd.copy(alpha = 0.03f)
            ),
            center = Offset(
                refractionOffset * 400f + 100f,
                refractionOffset * 300f + 50f
            ),
            radius = 500f
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent, Color.Transparent)
        )
    }

    // Multi-layered border for Apple-style depth
    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.35f),
            Color.White.copy(alpha = 0.12f),
            extendedColors.glassBorder.copy(alpha = 0.08f)
        ),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(extendedColors.glassSurface)
            .border(
                width = 0.75.dp,
                brush = borderBrush,
                shape = shape
            )
    ) {
        // Frosted glass base layer — subtle inner highlight at top
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.02f)
                        ),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Refraction layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(refractionBrush)
        )

        // Liquid glass shimmer overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(liquidBrush)
        )

        // Content
        Column(
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    }
}

/**
 * Liquid glass button with animated gradient and press effect.
 * Apple-style vibrancy with smooth chromatic shift.
 */
@Composable
fun LiquidGlassButton(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    content: @Composable RowScope.() -> Unit
) {
    val extendedColors = DualSpaceTheme.extendedColors

    // Animated gradient shift
    val infiniteTransition = rememberInfiniteTransition(label = "button_gradient")
    val gradientShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_shift"
    )

    val gradientBrush = Brush.linearGradient(
        colors = listOf(
            extendedColors.gradientStart,
            extendedColors.gradientMiddle,
            extendedColors.gradientEnd
        ),
        start = Offset(gradientShift * 500f, 0f),
        end = Offset(gradientShift * 500f + 800f, 400f)
    )

    val shape = RoundedCornerShape(cornerRadius)

    Row(
        modifier = modifier
            .clip(shape)
            .background(gradientBrush)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        content = content
    )
}

/**
 * Ad banner placeholder for Google AdMob integration.
 * Displays a styled placeholder that can be swapped with real ads.
 */
@Composable
fun AdBannerPlaceholder(
    modifier: Modifier = Modifier,
    adUnitId: String = "ca-app-pub-XXXXXXXXXXXXXXXX/YYYYYYYYYY"
) {
    val extendedColors = DualSpaceTheme.extendedColors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        extendedColors.glassOverlay,
                        extendedColors.glassSurface.copy(alpha = 0.5f)
                    )
                )
            )
            .border(
                width = 0.5.dp,
                color = extendedColors.glassBorder.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = androidx.compose.ui.Alignment.Center
    ) {
        // In production, replace this with:
        // AndroidView(factory = { context ->
        //     AdView(context).apply {
        //         setAdSize(AdSize.BANNER)
        //         this.adUnitId = adUnitId
        //         loadAd(AdRequest.Builder().build())
        //     }
        // })
        androidx.compose.material3.Text(
            text = "Ad Space",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

/**
 * Shimmer loading skeleton with Apple-style smooth gradient animation.
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 12.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_loading")
    val shimmerTranslate by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        start = Offset(shimmerTranslate * 800f, 0f),
        end = Offset(shimmerTranslate * 800f + 400f, 200f)
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush)
    )
}
