package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SettingsRepository
import com.example.engine.BluetoothMonitor
import com.example.engine.GestureDispatcher
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.model.SwipeAction
import com.example.model.SwipeSpeedPreset
import com.example.model.TargetApp
import com.example.service.BudSwipeAccessibilityService
import com.example.service.BudSwipeMediaSessionService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BudSwipeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SettingsRepository(application)
    private val bluetoothMonitor = BluetoothMonitor(application)

    val settings: StateFlow<BudSwipeSettings> = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = BudSwipeSettings()
    )

    val diagnosticState: StateFlow<DiagnosticState> = GestureDispatcher.diagnosticState

    private val _bluetoothState = MutableStateFlow(bluetoothMonitor.checkBluetoothStatus())
    val bluetoothState: StateFlow<BluetoothMonitor.BluetoothState> = _bluetoothState.asStateFlow()

    init {
        // Keep settings synced with GestureDispatcher
        viewModelScope.launch {
            settings.collect { current ->
                GestureDispatcher.updateSettings(current)
            }
        }
        refreshStatus()
    }

    fun refreshStatus() {
        val btState = bluetoothMonitor.checkBluetoothStatus()
        _bluetoothState.value = btState
        GestureDispatcher.updateBluetoothStatus(btState.isConnected, btState.connectedDeviceName)
        GestureDispatcher.updateAccessibilityStatus(BudSwipeAccessibilityService.isRunning)
    }

    fun toggleService(context: Context, enabled: Boolean) {
        viewModelScope.launch {
            val updated = settings.value.copy(isServiceEnabled = enabled)
            repository.updateSettings(updated)
            if (enabled) {
                BudSwipeMediaSessionService.start(context)
            } else {
                BudSwipeMediaSessionService.stop(context)
            }
        }
    }

    fun setTargetApp(targetApp: TargetApp) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(targetApp = targetApp))
        }
    }

    fun setLeftBudAction(action: SwipeAction) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(leftBudAction = action))
        }
    }

    fun setRightBudAction(action: SwipeAction) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(rightBudAction = action))
        }
    }

    fun setPlayPauseAction(action: SwipeAction) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(playPauseAction = action))
        }
    }

    fun setSpeedPreset(preset: SwipeSpeedPreset) {
        viewModelScope.launch {
            if (preset == SwipeSpeedPreset.CUSTOM) {
                repository.updateSettings(settings.value.copy(speedPreset = preset))
            } else {
                repository.updateSettings(
                    settings.value.copy(
                        speedPreset = preset,
                        swipeDurationMs = preset.durationMs,
                        swipeDistancePercent = preset.distancePercent
                    )
                )
            }
        }
    }

    fun setSwipeDistance(percent: Float) {
        viewModelScope.launch {
            repository.updateSettings(
                settings.value.copy(
                    swipeDistancePercent = percent,
                    speedPreset = SwipeSpeedPreset.CUSTOM
                )
            )
        }
    }

    fun setSwipeDuration(durationMs: Long) {
        viewModelScope.launch {
            repository.updateSettings(
                settings.value.copy(
                    swipeDurationMs = durationMs,
                    speedPreset = SwipeSpeedPreset.CUSTOM
                )
            )
        }
    }

    fun setCooldown(cooldownMs: Long) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(cooldownMs = cooldownMs))
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(vibrationFeedback = enabled))
        }
    }

    fun setRequireForeground(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(requireTargetAppForeground = enabled))
        }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateSettings(settings.value.copy(darkTheme = enabled))
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            repository.resetToDefaults()
        }
    }

    fun performManualTestSwipe(context: Context, action: SwipeAction, onComplete: (Boolean, String) -> Unit) {
        GestureDispatcher.performManualTestSwipe(context, action, onComplete)
    }

    fun startCalibration(isLeft: Boolean) {
        if (isLeft) {
            GestureDispatcher.isCalibratingLeft = true
            GestureDispatcher.isCalibratingRight = false
        } else {
            GestureDispatcher.isCalibratingLeft = false
            GestureDispatcher.isCalibratingRight = true
        }
    }

    fun cancelCalibration() {
        GestureDispatcher.isCalibratingLeft = false
        GestureDispatcher.isCalibratingRight = false
    }

    fun clearCalibration() {
        GestureDispatcher.clearCalibration()
    }

    fun clearEventHistory() {
        GestureDispatcher.clearEvents()
    }
}
