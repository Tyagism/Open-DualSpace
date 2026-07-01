package com.opendualspace.app.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.opendualspace.app.ui.screens.applist.AppListScreen
import com.opendualspace.app.ui.screens.home.HomeScreen
import com.opendualspace.app.ui.screens.settings.SettingsScreen
import com.opendualspace.app.ui.screens.setup.SetupScreen

/**
 * Navigation routes for the app.
 */
object Routes {
    const val SETUP = "setup"
    const val HOME = "home"
    const val APPS = "apps"
    const val SETTINGS = "settings"
}

/**
 * Main navigation graph with animated transitions.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    onSetupComplete: () -> Unit,
    onThemeToggle: (Boolean) -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(
                initialOffsetX = { it / 4 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(
                targetOffsetX = { -it / 4 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(
                initialOffsetX = { -it / 4 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(
                targetOffsetX = { it / 4 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Routes.SETUP) {
            SetupScreen(
                onSetupComplete = onSetupComplete
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onAddClone = {
                    navController.navigate(Routes.APPS)
                }
            )
        }

        composable(Routes.APPS) {
            AppListScreen()
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onThemeToggle = onThemeToggle
            )
        }
    }
}
