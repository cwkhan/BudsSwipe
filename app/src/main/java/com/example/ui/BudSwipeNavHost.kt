package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.engine.BluetoothMonitor
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.DiagnosticsScreen
import com.example.ui.screens.EarbudConfigScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SwipeSettingsScreen
import com.example.ui.theme.CyanNeon

enum class AppDestination(val label: String, val icon: ImageVector, val tag: String) {
    HOME("Home", Icons.Default.Home, "nav_home"),
    EARBUDS("Earbuds", Icons.Default.Headphones, "nav_earbuds"),
    SWIPE("Swipe", Icons.Default.Speed, "nav_swipe"),
    DIAGNOSTICS("Diagnostics", Icons.Default.GraphicEq, "nav_diagnostics"),
    ABOUT("About", Icons.Default.Info, "nav_about")
}

@Composable
fun BudSwipeMainApp(
    viewModel: BudSwipeViewModel,
    settings: BudSwipeSettings,
    diagnosticState: DiagnosticState,
    bluetoothState: BluetoothMonitor.BluetoothState,
    modifier: Modifier = Modifier
) {
    var currentDestination by remember { mutableStateOf(AppDestination.HOME) }

    // Android system back button handler
    BackHandler(enabled = currentDestination != AppDestination.HOME) {
        currentDestination = AppDestination.HOME
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("bottom_nav_bar"),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                AppDestination.entries.forEach { destination ->
                    val isSelected = currentDestination == destination
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { currentDestination = destination },
                        icon = {
                            Icon(
                                imageVector = destination.icon,
                                contentDescription = destination.label
                            )
                        },
                        label = {
                            Text(
                                text = destination.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            indicatorColor = CyanNeon.copy(alpha = 0.25f)
                        ),
                        modifier = Modifier.testTag(destination.tag)
                    )
                }
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentDestination,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) { destination ->
            when (destination) {
                AppDestination.HOME -> {
                    HomeScreen(
                        settings = settings,
                        diagnosticState = diagnosticState,
                        bluetoothState = bluetoothState,
                        onToggleService = { ctx, enabled -> viewModel.toggleService(ctx, enabled) },
                        onSelectTargetApp = { viewModel.setTargetApp(it) },
                        onManualTestSwipe = { ctx, act, cb -> viewModel.performManualTestSwipe(ctx, act, cb) }
                    )
                }

                AppDestination.EARBUDS -> {
                    EarbudConfigScreen(
                        settings = settings,
                        diagnosticState = diagnosticState,
                        onSetLeftAction = { viewModel.setLeftBudAction(it) },
                        onSetRightAction = { viewModel.setRightBudAction(it) },
                        onSetPlayPauseAction = { viewModel.setPlayPauseAction(it) },
                        onStartCalibration = { viewModel.startCalibration(it) },
                        onCancelCalibration = { viewModel.cancelCalibration() },
                        onClearCalibration = { viewModel.clearCalibration() }
                    )
                }

                AppDestination.SWIPE -> {
                    SwipeSettingsScreen(
                        settings = settings,
                        onSetSwipeDistance = { viewModel.setSwipeDistance(it) },
                        onSetSwipeDuration = { viewModel.setSwipeDuration(it) },
                        onSetCooldown = { viewModel.setCooldown(it) },
                        onSetVibration = { viewModel.setVibration(it) },
                        onSetRequireForeground = { viewModel.setRequireForeground(it) },
                        onSetDarkTheme = { viewModel.setDarkTheme(it) },
                        onResetDefaults = { viewModel.resetToDefaults() }
                    )
                }

                AppDestination.DIAGNOSTICS -> {
                    DiagnosticsScreen(
                        diagnosticState = diagnosticState,
                        bluetoothState = bluetoothState,
                        onRefreshStatus = { viewModel.refreshStatus() },
                        onClearEvents = { viewModel.clearEventHistory() }
                    )
                }

                AppDestination.ABOUT -> {
                    AboutScreen()
                }
            }
        }
    }
}
