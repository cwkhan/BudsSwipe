package com.example.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent
import com.example.engine.GestureDispatcher
import com.example.model.SwipeAction

class BudSwipeAccessibilityService : AccessibilityService() {

    companion object {
        var instance: BudSwipeAccessibilityService? = null
            private set

        val isRunning: Boolean
            get() = instance != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        GestureDispatcher.updateAccessibilityStatus(true)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_WINDOWS_CHANGED
        ) {
            val pkg = event.packageName?.toString()
            if (!pkg.isNullOrEmpty() && !pkg.contains("accessibility", ignoreCase = true)) {
                GestureDispatcher.updateForegroundPackage(pkg)
            }
        }
    }

    override fun onInterrupt() {
        // Accessibility service interrupted
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

        val clampedDistance = distancePercent.coerceIn(0.20f, 0.85f)
        val clampedDuration = durationMs.coerceIn(100L, 800L)

        // Middle X coordinate with slight jitter avoidance
        val centerX = screenWidth * 0.5f

        val startY: Float
        val endY: Float

        if (action == SwipeAction.SWIPE_UP) {
            // Next Reel: finger starts lower and moves up
            startY = screenHeight * (0.5f + (clampedDistance * 0.5f))
            endY = screenHeight * (0.5f - (clampedDistance * 0.5f))
        } else {
            // Previous Reel: finger starts higher and moves down
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
                    onResult(true)
                }

                override fun onCancelled(gestureDescription: GestureDescription?) {
                    super.onCancelled(gestureDescription)
                    onResult(false)
                }
            },
            null
        )
    }
}
