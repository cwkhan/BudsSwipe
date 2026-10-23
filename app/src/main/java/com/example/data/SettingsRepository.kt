package com.example.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.model.BudSwipeSettings
import com.example.model.SwipeAction
import com.example.model.SwipeSpeedPreset
import com.example.model.TargetApp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "budswipe_settings")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val SERVICE_ENABLED = booleanPreferencesKey("service_enabled")
        val TARGET_APP = stringPreferencesKey("target_app")
        val LEFT_BUD_ACTION = stringPreferencesKey("left_bud_action")
        val RIGHT_BUD_ACTION = stringPreferencesKey("right_bud_action")
        val LEFT_TRIGGER_KEY = intPreferencesKey("left_trigger_key")
        val RIGHT_TRIGGER_KEY = intPreferencesKey("right_trigger_key")
        val PLAY_PAUSE_ACTION = stringPreferencesKey("play_pause_action")
        val SPEED_PRESET = stringPreferencesKey("speed_preset")
        val SWIPE_DISTANCE = floatPreferencesKey("swipe_distance")
        val SWIPE_DURATION = longPreferencesKey("swipe_duration")
        val COOLDOWN_MS = longPreferencesKey("cooldown_ms")
        val VIBRATION = booleanPreferencesKey("vibration")
        val REQUIRE_FOREGROUND = booleanPreferencesKey("require_foreground")
        val DARK_THEME = booleanPreferencesKey("dark_theme")
    }

    val settingsFlow: Flow<BudSwipeSettings> = context.dataStore.data.map { preferences ->
        val targetAppString = preferences[PreferencesKeys.TARGET_APP] ?: TargetApp.ALL_SHORTS.name
        val targetApp = try {
            when (targetAppString) {
                "BOTH" -> TargetApp.ALL_SHORTS
                else -> TargetApp.valueOf(targetAppString)
            }
        } catch (_: Exception) {
            TargetApp.ALL_SHORTS
        }

        val leftAction = try {
            SwipeAction.valueOf(preferences[PreferencesKeys.LEFT_BUD_ACTION] ?: SwipeAction.SWIPE_UP.name)
        } catch (_: Exception) {
            SwipeAction.SWIPE_UP
        }

        val rightAction = try {
            SwipeAction.valueOf(preferences[PreferencesKeys.RIGHT_BUD_ACTION] ?: SwipeAction.SWIPE_DOWN.name)
        } catch (_: Exception) {
            SwipeAction.SWIPE_DOWN
        }

        val playPauseAction = try {
            SwipeAction.valueOf(preferences[PreferencesKeys.PLAY_PAUSE_ACTION] ?: SwipeAction.SWIPE_UP.name)
        } catch (_: Exception) {
            SwipeAction.SWIPE_UP
        }

        val speedPresetString = preferences[PreferencesKeys.SPEED_PRESET] ?: SwipeSpeedPreset.SMOOTH.name
        val speedPreset = try {
            SwipeSpeedPreset.valueOf(speedPresetString)
        } catch (_: Exception) {
            SwipeSpeedPreset.SMOOTH
        }

        BudSwipeSettings(
            isServiceEnabled = preferences[PreferencesKeys.SERVICE_ENABLED] ?: true,
            targetApp = targetApp,
            leftBudAction = leftAction,
            rightBudAction = rightAction,
            leftTriggerKeyCode = preferences[PreferencesKeys.LEFT_TRIGGER_KEY] ?: android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            rightTriggerKeyCode = preferences[PreferencesKeys.RIGHT_TRIGGER_KEY] ?: android.view.KeyEvent.KEYCODE_MEDIA_NEXT,
            playPauseAction = playPauseAction,
            speedPreset = speedPreset,
            swipeDistancePercent = preferences[PreferencesKeys.SWIPE_DISTANCE] ?: speedPreset.distancePercent,
            swipeDurationMs = preferences[PreferencesKeys.SWIPE_DURATION] ?: speedPreset.durationMs,
            cooldownMs = preferences[PreferencesKeys.COOLDOWN_MS] ?: 450L,
            vibrationFeedback = preferences[PreferencesKeys.VIBRATION] ?: true,
            requireTargetAppForeground = preferences[PreferencesKeys.REQUIRE_FOREGROUND] ?: false,
            darkTheme = preferences[PreferencesKeys.DARK_THEME] ?: true
        )
    }

    suspend fun updateSettings(settings: BudSwipeSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SERVICE_ENABLED] = settings.isServiceEnabled
            preferences[PreferencesKeys.TARGET_APP] = settings.targetApp.name
            preferences[PreferencesKeys.LEFT_BUD_ACTION] = settings.leftBudAction.name
            preferences[PreferencesKeys.RIGHT_BUD_ACTION] = settings.rightBudAction.name
            preferences[PreferencesKeys.LEFT_TRIGGER_KEY] = settings.leftTriggerKeyCode
            preferences[PreferencesKeys.RIGHT_TRIGGER_KEY] = settings.rightTriggerKeyCode
            preferences[PreferencesKeys.PLAY_PAUSE_ACTION] = settings.playPauseAction.name
            preferences[PreferencesKeys.SPEED_PRESET] = settings.speedPreset.name
            preferences[PreferencesKeys.SWIPE_DISTANCE] = settings.swipeDistancePercent
            preferences[PreferencesKeys.SWIPE_DURATION] = settings.swipeDurationMs
            preferences[PreferencesKeys.COOLDOWN_MS] = settings.cooldownMs
            preferences[PreferencesKeys.VIBRATION] = settings.vibrationFeedback
            preferences[PreferencesKeys.REQUIRE_FOREGROUND] = settings.requireTargetAppForeground
            preferences[PreferencesKeys.DARK_THEME] = settings.darkTheme
        }
    }

    suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
