package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.BudSwipeSettings
import com.example.model.SwipeSpeedPreset
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.VioletElectric
import kotlin.math.roundToInt

@Composable
fun SwipeSettingsScreen(
    settings: BudSwipeSettings,
    onSelectSpeedPreset: (SwipeSpeedPreset) -> Unit,
    onSetSwipeDistance: (Float) -> Unit,
    onSetSwipeDuration: (Long) -> Unit,
    onSetCooldown: (Long) -> Unit,
    onSetVibration: (Boolean) -> Unit,
    onSetRequireForeground: (Boolean) -> Unit,
    onSetDarkTheme: (Boolean) -> Unit,
    onResetDefaults: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gesture Mechanics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Fine-tune gesture distance, swipe speed, presets, and debounce timing.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Speed Preset Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                        Text(
                            text = "  Preset Gesture Profiles",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "Select a pre-configured speed & distance profile optimized for Reels & Shorts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SwipeSpeedPreset.entries.forEach { preset ->
                            val selected = settings.speedPreset == preset
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectSpeedPreset(preset) },
                                label = { Text(preset.label, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = CyanNeon.copy(alpha = 0.25f),
                                    selectedLabelColor = CyanNeon
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Swipe Distance
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Straighten, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Text(
                                text = "  Swipe Distance",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${(settings.swipeDistancePercent * 100).roundToInt()}% of Screen",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Text(
                        text = "Vertical travel distance of the automated swipe. Higher values guarantee Reels transition cleanly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = settings.swipeDistancePercent,
                        onValueChange = onSetSwipeDistance,
                        valueRange = 0.35f..0.85f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon
                        ),
                        modifier = Modifier.testTag("swipe_distance_slider")
                    )
                }
            }
        }

        // Swipe Duration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = VioletElectric, modifier = Modifier.size(20.dp))
                            Text(
                                text = "  Swipe Speed / Duration",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${settings.swipeDurationMs} ms",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = VioletElectric
                        )
                    }
                    Text(
                        text = "Duration of the touch gesture. Lower ms produces faster flicks (ideal for Instagram / YouTube / Facebook).",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = settings.swipeDurationMs.toFloat(),
                        onValueChange = { onSetSwipeDuration(it.toLong()) },
                        valueRange = 120f..500f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = VioletElectric,
                            activeTrackColor = VioletElectric
                        ),
                        modifier = Modifier.testTag("swipe_duration_slider")
                    )
                }
            }
        }

        // Gesture Cooldown (Debounce)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                            Text(
                                text = "  Tap Cooldown",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "${settings.cooldownMs} ms",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanNeon
                        )
                    }
                    Text(
                        text = "Prevents accidental double-swipes from rapid hardware events or multiple earbud button bounces.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Slider(
                        value = settings.cooldownMs.toFloat(),
                        onValueChange = { onSetCooldown(it.toLong()) },
                        valueRange = 300f..1800f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon
                        ),
                        modifier = Modifier.testTag("cooldown_slider")
                    )
                }
            }
        }

        // Toggles Card (Vibration & App Gate)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Vibration Feedback
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text("Haptic Feedback", style = MaterialTheme.typography.titleSmall)
                                Text("Vibrate phone briefly on each successful swipe", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.vibrationFeedback,
                            onCheckedChange = onSetVibration,
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanNeon),
                            modifier = Modifier.testTag("vibration_toggle")
                        )
                    }

                    // Require Target App in Foreground
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text("Foreground Lock", style = MaterialTheme.typography.titleSmall)
                                Text("Only swipe when a supported platform is on screen", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Switch(
                            checked = settings.requireTargetAppForeground,
                            onCheckedChange = onSetRequireForeground,
                            colors = SwitchDefaults.colors(checkedThumbColor = VioletElectric),
                            modifier = Modifier.testTag("foreground_lock_toggle")
                        )
                    }
                }
            }
        }

        // Restore Defaults
        item {
            OutlinedButton(
                onClick = onResetDefaults,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_defaults_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                Text("  Restore Default Settings")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
