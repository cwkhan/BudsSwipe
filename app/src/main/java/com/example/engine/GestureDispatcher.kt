package com.example.engine

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.KeyEvent
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.model.MediaKeyEventRecord
import com.example.model.SwipeAction
import com.example.model.TargetApp
import com.example.service.BudSwipeAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

object GestureDispatcher {

    private val scope = CoroutineScope(Dispatchers.Main)

    private val _diagnosticState = MutableStateFlow(DiagnosticState())
    val diagnosticState: StateFlow<DiagnosticState> = _diagnosticState.asStateFlow()

    private var currentSettings: BudSwipeSettings = BudSwipeSettings()
    private var lastSwipeTime: Long = 0L

    // For calibration mode
    var isCalibratingLeft: Boolean = false
    var isCalibratingRight: Boolean = false

    fun updateSettings(settings: BudSwipeSettings) {
        currentSettings = settings
    }

    fun updateAccessibilityStatus(isEnabled: Boolean) {
        _diagnosticState.update { it.copy(isAccessibilityEnabled = isEnabled) }
    }

    fun updateBluetoothStatus(isConnected: Boolean, deviceName: String?) {
        _diagnosticState.update {
            it.copy(
                isBluetoothConnected = isConnected,
                connectedDeviceName = deviceName
            )
        }
    }

    fun updateForegroundPackage(packageName: String) {
        val inTarget = if (currentSettings.targetApp == TargetApp.ANY) {
            true
        } else {
            currentSettings.targetApp.packageNames.any { pkg ->
                packageName.equals(pkg, ignoreCase = true) ||
                        packageName.contains(pkg, ignoreCase = true) ||
                        (pkg.contains("youtube", ignoreCase = true) && packageName.contains("youtube", ignoreCase = true)) ||
                        (pkg.contains("instagram", ignoreCase = true) && packageName.contains("instagram", ignoreCase = true)) ||
                        (pkg.contains("facebook", ignoreCase = true) && packageName.contains("facebook", ignoreCase = true)) ||
                        (pkg.contains("musically", ignoreCase = true) && (packageName.contains("musically", ignoreCase = true) || packageName.contains("tiktok", ignoreCase = true)))
            }
        }

        _diagnosticState.update {
            it.copy(
                currentForegroundApp = packageName,
                isInTargetApp = inTarget
            )
        }
    }

    fun updateMediaSessionStatus(isActive: Boolean) {
        _diagnosticState.update { it.copy(isMediaSessionActive = isActive) }
    }

    /**
     * Central entry point for all hardware and Bluetooth media button key events.
     */
    fun onKeyEventReceived(context: Context, event: KeyEvent, source: String = "MediaButtonReceiver"): Boolean {
        val keyCode = event.keyCode
        val action = event.action
        val keyName = KeyEvent.keyCodeToString(keyCode)
        val actionName = if (action == KeyEvent.ACTION_DOWN) "ACTION_DOWN" else "ACTION_UP"
        val now = System.currentTimeMillis()

        // Handle calibration if user is actively in calibration wizard
        if (isCalibratingLeft && action == KeyEvent.ACTION_DOWN) {
            handleCalibration(isLeft = true, keyCode = keyCode, keyName = keyName)
            return true
        }
        if (isCalibratingRight && action == KeyEvent.ACTION_DOWN) {
            handleCalibration(isLeft = false, keyCode = keyCode, keyName = keyName)
            return true
        }

        // Only trigger swipes on ACTION_DOWN to avoid duplicate swipes on release
        if (action != KeyEvent.ACTION_DOWN) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = null,
                note = "Ignored ACTION_UP (Key Release)"
            )
            return false
        }

        // Check if service is enabled
        if (!currentSettings.isServiceEnabled) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = null,
                note = "BudSwipe master switch is OFF"
            )
            return false
        }

        // Check debouncing / cooldown
        val timeSinceLast = now - lastSwipeTime
        if (timeSinceLast < currentSettings.cooldownMs) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = null,
                note = "Debounced (Cooldown: ${currentSettings.cooldownMs - timeSinceLast}ms left)"
            )
            return false
        }

        // Check foreground target app filter if required
        if (currentSettings.requireTargetAppForeground && !_diagnosticState.value.isInTargetApp) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = null,
                note = "Ignored: Current app '${_diagnosticState.value.currentForegroundApp}' is not ${currentSettings.targetApp.label}"
            )
            return false
        }

        // Map keycode to swipe action
        val swipeAction = resolveActionForKeyCode(keyCode)

        if (swipeAction == SwipeAction.NONE) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = null,
                note = "Key '$keyName' is unmapped or set to Disabled"
            )
            return false
        }

        // Check if Accessibility Service is bound and ready
        val serviceInstance = BudSwipeAccessibilityService.instance
        if (serviceInstance == null) {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = swipeAction,
                note = "Accessibility Service is not enabled in Android Settings!"
            )
            _diagnosticState.update { it.copy(lastGestureStatus = "Failed: Accessibility Service Disabled") }
            return false
        }

        // Execute swipe gesture
        lastSwipeTime = now
        val dispatched = serviceInstance.performSwipe(
            action = swipeAction,
            distancePercent = currentSettings.swipeDistancePercent,
            durationMs = currentSettings.swipeDurationMs
        ) { success ->
            _diagnosticState.update {
                it.copy(
                    lastGestureStatus = if (success) "Dispatched ${swipeAction.label}" else "Gesture Failed",
                    totalSwipesDispatched = if (success) it.totalSwipesDispatched + 1 else it.totalSwipesDispatched
                )
            }
        }

        if (dispatched) {
            if (currentSettings.vibrationFeedback) {
                triggerHapticFeedback(context)
            }
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = true,
                resultingAction = swipeAction,
                note = "Success: Executed ${swipeAction.label}"
            )
            return true
        } else {
            recordEvent(
                keyCode = keyCode,
                keyCodeName = keyName,
                actionType = actionName,
                sourceDevice = source,
                wasHandled = false,
                resultingAction = swipeAction,
                note = "Gesture dispatch failed in system"
            )
            return false
        }
    }

    /**
     * Resolves key code to configured SwipeAction.
     */
    fun resolveActionForKeyCode(keyCode: Int): SwipeAction {
        return when (keyCode) {
            currentSettings.leftTriggerKeyCode -> currentSettings.leftBudAction
            currentSettings.rightTriggerKeyCode -> currentSettings.rightBudAction
            KeyEvent.KEYCODE_MEDIA_NEXT -> {
                // Default fallback if not overridden
                if (currentSettings.rightTriggerKeyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
                    currentSettings.rightBudAction
                } else {
                    currentSettings.leftBudAction
                }
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                // Default fallback if not overridden
                if (currentSettings.leftTriggerKeyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
                    currentSettings.leftBudAction
                } else {
                    currentSettings.rightBudAction
                }
            }
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                currentSettings.playPauseAction
            }
            else -> SwipeAction.NONE
        }
    }

    /**
     * Manual swipe test invoked from UI buttons.
     */
    fun performManualTestSwipe(context: Context, action: SwipeAction, onComplete: (Boolean, String) -> Unit) {
        val service = BudSwipeAccessibilityService.instance
        if (service == null) {
            onComplete(false, "Accessibility Service is not enabled. Please enable it in Settings.")
            return
        }

        val success = service.performSwipe(
            action = action,
            distancePercent = currentSettings.swipeDistancePercent,
            durationMs = currentSettings.swipeDurationMs
        ) { ok ->
            if (ok) {
                if (currentSettings.vibrationFeedback) {
                    triggerHapticFeedback(context)
                }
                _diagnosticState.update {
                    it.copy(
                        lastGestureStatus = "Manual test: ${action.label} completed",
                        totalSwipesDispatched = it.totalSwipesDispatched + 1
                    )
                }
                onComplete(true, "${action.label} gesture completed successfully!")
            } else {
                onComplete(false, "Gesture was cancelled or rejected by system.")
            }
        }

        if (!success) {
            onComplete(false, "Failed to dispatch gesture. Ensure screen is unlocked.")
        }
    }

    private fun handleCalibration(isLeft: Boolean, keyCode: Int, keyName: String) {
        if (isLeft) {
            isCalibratingLeft = false
            val rightKey = _diagnosticState.value.rightCalibratedKey
            val distinguishable = if (rightKey != null) rightKey != keyCode else null
            val msg = buildCalibrationAdvice(leftKey = keyCode, rightKey = rightKey)

            _diagnosticState.update {
                it.copy(
                    leftCalibratedKey = keyCode,
                    areBudsDistinguishable = distinguishable,
                    calibrationMessage = msg
                )
            }
        } else {
            isCalibratingRight = false
            val leftKey = _diagnosticState.value.leftCalibratedKey
            val distinguishable = if (leftKey != null) leftKey != keyCode else null
            val msg = buildCalibrationAdvice(leftKey = leftKey, rightKey = keyCode)

            _diagnosticState.update {
                it.copy(
                    rightCalibratedKey = keyCode,
                    areBudsDistinguishable = distinguishable,
                    calibrationMessage = msg
                )
            }
        }
    }

    private fun buildCalibrationAdvice(leftKey: Int?, rightKey: Int?): String {
        if (leftKey == null || rightKey == null) {
            return "Tap the other earbud to complete calibration comparison."
        }
        return if (leftKey != rightKey) {
            "Stereo separation detected! Left (${KeyEvent.keyCodeToString(leftKey)}) and Right (${KeyEvent.keyCodeToString(rightKey)}) send distinct signals. Full independent control is active."
        } else {
            val keyStr = KeyEvent.keyCodeToString(leftKey)
            "Notice: Both earbuds send the identical event ($keyStr). Android cannot distinguish which physical bud was tapped. Tip: In your earbud app (e.g. Realme Link), customize Left earbud to 'Previous Track' and Right earbud to 'Next Track' for independent swiping."
        }
    }

    fun clearCalibration() {
        _diagnosticState.update {
            it.copy(
                leftCalibratedKey = null,
                rightCalibratedKey = null,
                areBudsDistinguishable = null,
                calibrationMessage = ""
            )
        }
    }

    private fun recordEvent(
        keyCode: Int,
        keyCodeName: String,
        actionType: String,
        sourceDevice: String,
        wasHandled: Boolean,
        resultingAction: SwipeAction?,
        note: String
    ) {
        val record = MediaKeyEventRecord(
            keyCode = keyCode,
            keyCodeName = keyCodeName,
            actionType = actionType,
            sourceDevice = sourceDevice,
            wasHandledAsSwipe = wasHandled,
            resultingAction = resultingAction,
            note = note
        )

        _diagnosticState.update { state ->
            val updatedList = listOf(record) + state.recentEvents.take(19)
            state.copy(
                lastEvent = record,
                recentEvents = updatedList
            )
        }
    }

    fun clearEvents() {
        _diagnosticState.update { it.copy(lastEvent = null, recentEvents = emptyList()) }
    }

    @Suppress("DEPRECATION")
    private fun triggerHapticFeedback(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val effect = VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK)
                vibratorManager?.vibrate(CombinedVibration.createParallel(effect))
            } else {
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator?.vibrate(35)
                }
            }
        } catch (_: Exception) {
            // Haptic error ignored safely
        }
    }
}
