package com.example.model

import android.view.KeyEvent

/**
 * Swipe actions mapped to earbud taps.
 */
enum class SwipeAction(val label: String, val description: String) {
    SWIPE_UP("Swipe UP", "Next Reel / Short (scroll up)"),
    SWIPE_DOWN("Swipe DOWN", "Previous Reel / Short (scroll down)"),
    NONE("Disabled", "No action")
}

/**
 * Speed presets for gesture execution.
 */
enum class SwipeSpeedPreset(val label: String, val durationMs: Long, val distancePercent: Float) {
    FAST("Fast Flick", 160L, 0.52f),
    SMOOTH("Smooth Glide", 240L, 0.60f),
    DEEP("Deep Scroll", 340L, 0.75f),
    CUSTOM("Custom Settings", 240L, 0.60f)
}

/**
 * Target applications to restrict automated earbud swipes to.
 */
enum class TargetApp(val label: String, val shortLabel: String, val packageNames: List<String>) {
    ALL_SHORTS(
        "All Platforms",
        "All Apps",
        listOf(
            "com.instagram.android",
            "com.google.android.youtube",
            "com.facebook.katana",
            "com.facebook.lite",
            "com.zhiliaoapp.musically",
            "com.ss.android.ugc.trill"
        )
    ),
    YOUTUBE("YouTube Shorts", "YouTube", listOf("com.google.android.youtube")),
    INSTAGRAM("Instagram Reels", "Instagram", listOf("com.instagram.android")),
    FACEBOOK("Facebook Reels", "Facebook", listOf("com.facebook.katana", "com.facebook.lite")),
    TIKTOK("TikTok", "TikTok", listOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill")),
    ANY("Any App (Test Mode)", "Any App", emptyList())
}

/**
 * Official community & developer links.
 */
object CommunityLinks {
    const val GITHUB_URL = "https://github.com/cwkhan"
    const val DISCORD_URL = "https://dsc.gg/cwkhan"
    const val YOUTUBE_URL = "https://youtube.com/@cwkhan"
    const val DEVELOPER_HANDLE = "cwkhan"
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
    val targetApp: TargetApp = TargetApp.ALL_SHORTS,
    val leftBudAction: SwipeAction = SwipeAction.SWIPE_UP, // Default: Left -> Next Reel
    val rightBudAction: SwipeAction = SwipeAction.SWIPE_DOWN, // Default: Right -> Prev Reel
    val leftTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_NEXT,
    val rightTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_PREVIOUS,
    val playPauseAction: SwipeAction = SwipeAction.SWIPE_UP, // Fallback for single-code buds
    val speedPreset: SwipeSpeedPreset = SwipeSpeedPreset.SMOOTH,
    val swipeDistancePercent: Float = 0.60f, // 60% screen height
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
