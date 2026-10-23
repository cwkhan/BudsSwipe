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
    FAST("Fast Flick", 150L, 0.55f),
    SMOOTH("Smooth Glide", 200L, 0.62f),
    DEEP("Deep Scroll", 320L, 0.75f),
    CUSTOM("Custom Settings", 200L, 0.62f)
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
            "com.ss.android.ugc.trill",
            "com.snapchat.android",
            "com.budsweep.app"
        )
    ),
    YOUTUBE("YouTube Shorts", "YouTube", listOf("com.google.android.youtube", "com.budsweep.app")),
    INSTAGRAM("Instagram Reels", "Instagram", listOf("com.instagram.android", "com.budsweep.app")),
    FACEBOOK("Facebook Reels", "Facebook", listOf("com.facebook.katana", "com.facebook.lite", "com.budsweep.app")),
    TIKTOK("TikTok", "TikTok", listOf("com.zhiliaoapp.musically", "com.ss.android.ugc.trill", "com.budsweep.app")),
    ANY("Universal (Any App)", "Universal", emptyList())
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
    val leftBudAction: SwipeAction = SwipeAction.SWIPE_UP, // Default: Left -> Next Reel (Swipe UP)
    val rightBudAction: SwipeAction = SwipeAction.SWIPE_DOWN, // Default: Right -> Prev Reel (Swipe DOWN)
    val leftTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_PREVIOUS, // Standard Left Bud
    val rightTriggerKeyCode: Int = KeyEvent.KEYCODE_MEDIA_NEXT, // Standard Right Bud
    val playPauseAction: SwipeAction = SwipeAction.SWIPE_UP, // Fallback for single-code buds
    val speedPreset: SwipeSpeedPreset = SwipeSpeedPreset.SMOOTH,
    val swipeDistancePercent: Float = 0.62f, // 62% screen height
    val swipeDurationMs: Long = 200L, // 200ms crisp flick
    val cooldownMs: Long = 450L, // 450ms human debounce
    val vibrationFeedback: Boolean = true,
    val requireTargetAppForeground: Boolean = false, // Default false: Works everywhere out of the box!
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
    val isInTargetApp: Boolean = true,
    val lastEvent: MediaKeyEventRecord? = null,
    val recentEvents: List<MediaKeyEventRecord> = emptyList(),
    val leftCalibratedKey: Int? = null,
    val rightCalibratedKey: Int? = null,
    val areBudsDistinguishable: Boolean? = null,
    val calibrationMessage: String = "",
    val lastGestureStatus: String = "Idle",
    val totalSwipesDispatched: Int = 0
)

