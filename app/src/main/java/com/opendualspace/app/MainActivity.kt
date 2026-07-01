package com.opendualspace.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.opendualspace.app.data.preferences.AppPreferences
import com.opendualspace.app.ui.components.GlassBottomNavBar
import com.opendualspace.app.ui.navigation.NavGraph
import com.opendualspace.app.ui.navigation.Routes
import com.opendualspace.app.ui.theme.OpenDualSpaceTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Main entry point for Open DualSpace.
 * Handles navigation, theme state, and setup completion routing.
 */
class MainActivity : ComponentActivity() {

    private lateinit var preferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        // Splash screen
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferences = AppPreferences(this)

        // Read setup state synchronously for start destination (quick DataStore read)
        val setupCompleted = runBlocking { preferences.isSetupCompleted.first() }

        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            // Load theme preference
            LaunchedEffect(Unit) {
                isDarkMode = preferences.isDarkMode.first()
            }

            OpenDualSpaceTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""

                val startDestination = if (setupCompleted) Routes.HOME else Routes.SETUP

                // Show bottom nav only on main screens
                val showBottomNav = currentRoute in listOf(
                    Routes.HOME, Routes.APPS, Routes.SETTINGS
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        AnimatedVisibility(
                            visible = showBottomNav,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it })
                        ) {
                            GlassBottomNavBar(
                                selectedRoute = currentRoute,
                                onNavigate = { route ->
                                    if (route != currentRoute) {
                                        navController.navigate(route) {
                                            popUpTo(Routes.HOME) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                }
                            )
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NavGraph(
                            navController = navController,
                            startDestination = startDestination,
                            onSetupComplete = {
                                // Save setup as completed and navigate to Home
                                lifecycleScope.launch {
                                    preferences.setSetupCompleted(true)
                                }
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.SETUP) { inclusive = true }
                                    launchSingleTop = true
                                }
                            },
                            onThemeToggle = { dark ->
                                isDarkMode = dark
                            }
                        )
                    }
                }
            }
        }
    }
}
