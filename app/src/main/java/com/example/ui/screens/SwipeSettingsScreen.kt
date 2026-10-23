package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BudSwipeSettings
import com.example.model.SwipeSpeedPreset
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseBorderSubtle
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.SupabaseSurface
import com.example.ui.theme.SupabaseSurfaceContainer
import com.example.ui.theme.SupabaseSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
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
            .background(SupabaseBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gesture Mechanics",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Fine-tune gesture distance, swipe speed, presets, and debounce timing.",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        // Speed Preset Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = "Preset Gesture Profiles",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Select a pre-configured speed & distance profile optimized for Reels & Shorts.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SwipeSpeedPreset.entries.forEach { preset ->
                            val selected = settings.speedPreset == preset
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectSpeedPreset(preset) },
                                label = { Text(preset.label, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SupabaseGreen.copy(alpha = 0.20f),
                                    selectedLabelColor = SupabaseGreen,
                                    containerColor = SupabaseSurfaceVariant,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = selected,
                                    borderColor = SupabaseBorder,
                                    selectedBorderColor = SupabaseGreen.copy(alpha = 0.5f)
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
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
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
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                text = "Swipe Travel Distance",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${(settings.swipeDistancePercent * 100).roundToInt()}% Screen",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyanNeon
                        )
                    }
                    Text(
                        text = "Vertical screen span of the automated swipe. 60-70% guarantees Reels transition smoothly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = settings.swipeDistancePercent,
                        onValueChange = onSetSwipeDistance,
                        valueRange = 0.35f..0.85f,
                        steps = 10,
                        colors = SliderDefaults.colors(
                            thumbColor = CyanNeon,
                            activeTrackColor = CyanNeon,
                            inactiveTrackColor = SupabaseSurfaceVariant
                        ),
                        modifier = Modifier.testTag("swipe_distance_slider")
                    )
                }
            }
        }

        // Swipe Duration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
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
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                text = "Swipe Duration / Speed",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${settings.swipeDurationMs} ms",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VioletElectric
                        )
                    }
                    Text(
                        text = "Speed of the touch gesture. Lower ms (150-220ms) produces crisp flicks that scroll immediately.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = settings.swipeDurationMs.toFloat(),
                        onValueChange = { onSetSwipeDuration(it.toLong()) },
                        valueRange = 120f..500f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = VioletElectric,
                            activeTrackColor = VioletElectric,
                            inactiveTrackColor = SupabaseSurfaceVariant
                        ),
                        modifier = Modifier.testTag("swipe_duration_slider")
                    )
                }
            }
        }

        // Gesture Cooldown (Debounce)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HourglassBottom, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.padding(start = 8.dp))
                            Text(
                                text = "Earbud Tap Debounce",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "${settings.cooldownMs} ms",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = SupabaseGreen
                        )
                    }
                    Text(
                        text = "Prevents accidental double-swipes from multi-tap hardware button bounce.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Slider(
                        value = settings.cooldownMs.toFloat(),
                        onValueChange = { onSetCooldown(it.toLong()) },
                        valueRange = 250f..1200f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = SupabaseGreen,
                            activeTrackColor = SupabaseGreen,
                            inactiveTrackColor = SupabaseSurfaceVariant
                        ),
                        modifier = Modifier.testTag("cooldown_slider")
                    )
                }
            }
        }

        // Toggles Card (Vibration & App Gate)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
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
                            Icon(Icons.Default.Vibration, contentDescription = null, tint = SupabaseGreen)
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text("Haptic Confirmation", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Vibrate phone lightly whenever an earbud tap executes", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                        Switch(
                            checked = settings.vibrationFeedback,
                            onCheckedChange = onSetVibration,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SupabaseGreen,
                                checkedTrackColor = SupabaseGreen.copy(alpha = 0.35f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SupabaseSurfaceContainer
                            ),
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
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = VioletElectric)
                            Column(modifier = Modifier.padding(start = 12.dp)) {
                                Text("Strict Foreground Lock", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Only swipe when Shorts/Reels apps are active on screen (Default: OFF for maximum reliability)", style = MaterialTheme.typography.bodySmall, color = TextMuted)
                            }
                        }
                        Switch(
                            checked = settings.requireTargetAppForeground,
                            onCheckedChange = onSetRequireForeground,
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = VioletElectric,
                                checkedTrackColor = VioletElectric.copy(alpha = 0.35f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SupabaseSurfaceContainer
                            ),
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
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("reset_defaults_button")
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.padding(start = 8.dp))
                Text("Restore Factory Calibration Defaults", style = MaterialTheme.typography.labelSmall)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

