package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import com.example.engine.GestureDispatcher
import com.example.model.SwipeAction

class BudSwipeAccessibilityService : AccessibilityService() {

    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        var instance: BudSwipeAccessibilityService? = null
            private set

        val isRunning: Boolean
            get() = instance != null

        fun isAccessibilityEnabledInSystem(context: Context): Boolean {
            val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
            val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            return enabledServices.any {
                it.resolveInfo.serviceInfo.packageName == context.packageName
            }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this

        try {
            val info = serviceInfo ?: AccessibilityServiceInfo()
            info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED or AccessibilityEvent.TYPE_WINDOWS_CHANGED
            info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
            serviceInfo = info
        } catch (_: Exception) {
            // Handled
        }

        GestureDispatcher.updateAccessibilityStatus(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {
            val pkg = event.packageName?.toString()
            if (!pkg.isNullOrEmpty() &&
                !pkg.equals("android", ignoreCase = true) &&
                !pkg.equals("com.android.systemui", ignoreCase = true) &&
                !pkg.contains("accessibility", ignoreCase = true) &&
                !pkg.contains("inputmethod", ignoreCase = true)
            ) {
                GestureDispatcher.updateForegroundPackage(pkg)
            }
        }
    }

    /**
     * Intercept hardware media keys and Bluetooth earbud events at the OS level.
     */
    override fun onKeyEvent(event: KeyEvent): Boolean {
        val keyCode = event.keyCode

        // List of keys we care about
        val isMediaOrVolumeKey = when (keyCode) {
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD,
            KeyEvent.KEYCODE_MEDIA_REWIND,
            KeyEvent.KEYCODE_VOLUME_UP,
            KeyEvent.KEYCODE_VOLUME_DOWN -> true
            else -> {
                // Also check if matches custom calibrated keys
                val diag = GestureDispatcher.diagnosticState.value
                keyCode == diag.leftCalibratedKey || keyCode == diag.rightCalibratedKey
            }
        }

        if (isMediaOrVolumeKey) {
            val handled = GestureDispatcher.onKeyEventReceived(
                context = this,
                event = event,
                source = "AccessibilityService.onKeyEvent"
            )

            // If handled on ACTION_DOWN, or if it's the companion ACTION_UP, consume it
            if (handled || (event.action == KeyEvent.ACTION_UP && isMediaOrVolumeKey)) {
                return true
            }
        }

        return super.onKeyEvent(event)
    }

    override fun onInterrupt() {
        // Interrupted by system
    }

    override fun onDestroy() {
        instance = null
        GestureDispatcher.updateAccessibilityStatus(false)
        super.onDestroy()
    }

    /**
     * Dispatches simulated vertical swipe gesture using Android AccessibilityService dispatchGesture API.
     */
    fun performSwipe(
        action: SwipeAction,
        distancePercent: Float,
        durationMs: Long,
        onResult: (Boolean) -> Unit
    ): Boolean {
        if (action == SwipeAction.NONE) {
            onResult(false)
            return false
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val screenHeight = displayMetrics.heightPixels.toFloat()

        if (screenWidth <= 0 || screenHeight <= 0) {
            onResult(false)
            return false
        }

        val clampedDistance = distancePercent.coerceIn(0.25f, 0.85f)
        val clampedDuration = durationMs.coerceIn(120L, 500L)

        // Middle X coordinate with safe margin away from edges (50% center)
        val centerX = screenWidth * 0.50f

        val startY: Float
        val endY: Float

        if (action == SwipeAction.SWIPE_UP) {
            // Next Reel: finger starts at bottom/center (75% height) and moves UP to (25% height)
            startY = screenHeight * (0.5f + (clampedDistance * 0.5f))
            endY = screenHeight * (0.5f - (clampedDistance * 0.5f))
        } else {
            // Previous Reel: finger starts at top/center (25% height) and moves DOWN to (75% height)
            startY = screenHeight * (0.5f - (clampedDistance * 0.5f))
            endY = screenHeight * (0.5f + (clampedDistance * 0.5f))
        }

        val path = Path().apply {
            moveTo(centerX, startY)
            lineTo(centerX, endY)
        }

        val stroke = GestureDescription.StrokeDescription(path, 0L, clampedDuration)
        val gesture = GestureDescription.Builder().addStroke(stroke).build()

        return dispatchGesture(
            gesture,
            object : GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    mainHandler.post { onResult(true) }
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    mainHandler.post { onResult(false) }
                }
            },
            null
        )
    }
}

