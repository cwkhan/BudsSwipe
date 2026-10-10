package com.example.model

import android.view.KeyEvent

/**
 * Swipe actions mapped to earbud taps.
 */
enum class SwipeAction(val label: String, val description: String) {
    SWIPE_UP("Swipe UP", "Next Reel (scroll up)"),
    SWIPE_DOWN("Swipe DOWN", "Previous Reel (scroll down)"),
    NONE("Disabled", "No action")
}

/**
 * Target applications to restrict automated earbud swipes to.
 */
enum class TargetApp(val label: String, val packageNames: List<String>) {
    INSTAGRAM("Instagram Only", listOf("com.instagram.android")),
    FACEBOOK("Facebook Only", listOf("com.facebook.katana", "com.facebook.lite")),
    BOTH("Both (IG & FB)", listOf("com.instagram.android", "com.facebook.katana", "com.facebook.lite")),
    ANY("Any App (Test Mode)", emptyList())
}

/**
 * Record of a detected hardware / Bluetooth media key event.
 */
data class MediaKeyEventRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val keyCode: Int,
    val keyCodeName: String,
    val actionType: String, // "ACTION_DOWN" or "ACTION_UP"
    val timestamp: Long = System.currentTimeMillis(),
    val sourceDevice: String = "Bluetooth Audio / Media Key",
    val wasHandledAsSwipe: Boolean = false,
    val resultingAction: SwipeAction? = null,
    val note: String = ""
)

/**
 * App settings persisted via DataStore.
 */
data class BudSwipeSettings(
    val isServiceEnabled: Boolean = true,
    val targetApp: TargetApp = TargetApp.BOTH,
    val leftBudAction: SwipeAction = SwipeAction.SWIPE_UP, // Default: Left -> Next Reel
    val rightBudAction: SwipeAction = SwipeAction.SWIPE_DOWN, // Default: Right -> Prev Reel
    val leftTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_NEXT, // Or Previous
    val rightTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_PREVIOUS, // Or Next
    val playPauseAction: SwipeAction = SwipeAction.SWIPE_UP, // Fallback for single-code buds
    val swipeDistancePercent: Float = 0.55f, // 55% screen height
    val swipeDurationMs: Long = 240L, // 240ms duration
    val cooldownMs: Long = 700L, // 700ms cooldown to prevent double taps
    val vibrationFeedback: Boolean = true,
    val requireTargetAppForeground: Boolean = true,
    val darkTheme: Boolean = true
)

/**
 * Real-time diagnostic and calibration state.
 */
data class DiagnosticState(
    val isAccessibilityEnabled: Boolean = false,
    val isBluetoothConnected: Boolean = false,
    val connectedDeviceName: String? = null,
    val isMediaSessionActive: Boolean = false,
    val currentForegroundApp: String = "None",
    val isInTargetApp: Boolean = false,
    val lastEvent: MediaKeyEventRecord? = null,
    val recentEvents: List<MediaKeyEventRecord> = emptyList(),
    val leftCalibratedKey: Int? = null,
    val rightCalibratedKey: Int? = null,
    val areBudsDistinguishable: Boolean? = null,
    val calibrationMessage: String = "",
    val lastGestureStatus: String = "Idle",
    val totalSwipesDispatched: Int = 0
)
