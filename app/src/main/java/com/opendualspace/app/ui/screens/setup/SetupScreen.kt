package com.opendualspace.app.ui.screens.setup

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.opendualspace.app.ui.components.GlassmorphismCard
import com.opendualspace.app.ui.theme.DualSpaceTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class SetupPage(
    val icon: ImageVector,
    val title: String,
    val description: String
)

/**
 * Setup wizard screen shown on first launch.
 * Guides user through onboarding with animated pages.
 * Tapping "Let's Start" completes setup and navigates to Home.
 */
@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit
) {
    val extendedColors = DualSpaceTheme.extendedColors
    val scope = rememberCoroutineScope()

    val pages = listOf(
        SetupPage(
            icon = Icons.Filled.Security,
            title = "Create Isolated Space",
            description = "We create a secure isolated space on your device to run cloned apps with separate data and accounts."
        ),
        SetupPage(
            icon = Icons.Filled.ContentCopy,
            title = "Clone Your Apps",
            description = "Select any installed app and clone it with one tap. Use dual accounts for WhatsApp, Instagram, and more."
        ),
        SetupPage(
            icon = Icons.Filled.RocketLaunch,
            title = "Use Seamlessly",
            description = "Launch cloned apps just like regular apps. Freeze them when not in use to save battery and RAM."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    // Completion animation state
    var isCompleting by remember { mutableStateOf(false) }
    val completionScale by animateFloatAsState(
        targetValue = if (isCompleting) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "completion_scale"
    )

    // Background gradient animation
    val infiniteTransition = rememberInfiniteTransition(label = "bg_gradient")
    val gradientOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradient_offset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer { scaleX = completionScale; scaleY = completionScale }
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        extendedColors.gradientStart.copy(alpha = 0.08f),
                        MaterialTheme.colorScheme.background,
                        extendedColors.gradientEnd.copy(alpha = 0.05f)
                    ),
                    center = Offset(
                        gradientOffset * 800f + 200f,
                        gradientOffset * 1200f + 300f
                    ),
                    radius = 1200f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Title
            Text(
                text = "Open DualSpace",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Clone apps, run dual accounts",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Onboarding pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                SetupPageContent(page = pages[page])
            }

            // Page indicator dots
            Row(
                modifier = Modifier.padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pages.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "dot_width"
                    )
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action buttons
            if (pagerState.currentPage < pages.size - 1) {
                // Next button
                Button(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Next",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            } else {
                // "Let's Start" button — triggers provisioning
                val context = androidx.compose.ui.platform.LocalContext.current
                val workProfileManager = remember { com.opendualspace.app.domain.manager.WorkProfileManager(context) }
                val launcher = androidx.activity.compose.rememberLauncherForActivityResult(
                    contract = androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()
                ) { result ->
                    if (result.resultCode == android.app.Activity.RESULT_OK) {
                        isCompleting = true
                        scope.launch {
                            delay(300)
                            onSetupComplete()
                        }
                    } else {
                        isCompleting = false
                        android.widget.Toast.makeText(context, "Work Profile setup cancelled", android.widget.Toast.LENGTH_SHORT).show()
                    }
                }

                Button(
                    onClick = {
                        if (!isCompleting) {
                            if (workProfileManager.canProvisionWorkProfile()) {
                                isCompleting = true
                                launcher.launch(workProfileManager.createProvisioningIntent())
                            } else {
                                // Fallback if provisioning not possible
                                isCompleting = true
                                scope.launch {
                                    delay(300)
                                    onSetupComplete()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isCompleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    AnimatedContent(
                        targetState = isCompleting,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(200)) togetherWith
                            fadeOut(animationSpec = tween(200))
                        },
                        label = "button_content"
                    ) { completing ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            if (completing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Getting ready...",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Icon(Icons.Filled.RocketLaunch, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Let's Start",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // Skip link
            if (pagerState.currentPage < pages.size - 1) {
                TextButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(pages.size - 1)
                        }
                    },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text(
                        text = "Skip",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SetupPageContent(page: SetupPage) {
    // Icon bounce animation
    val infiniteTransition = rememberInfiniteTransition(label = "icon_bounce")
    val iconScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "icon_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated icon in glass card
        GlassmorphismCard(
            modifier = Modifier.size(140.dp),
            cornerRadius = 32.dp,
            enableLiquidAnimation = true
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    page.icon,
                    contentDescription = page.title,
                    modifier = Modifier
                        .size(64.dp)
                        .scale(iconScale),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}
