package com.example.ui.screens

import android.view.KeyEvent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.GestureDispatcher
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.model.SwipeAction
import com.example.ui.components.WarningBanner
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseBorderSubtle
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.SupabaseGreenDark
import com.example.ui.theme.SupabaseSurface
import com.example.ui.theme.SupabaseSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletElectric

@Composable
fun EarbudConfigScreen(
    settings: BudSwipeSettings,
    diagnosticState: DiagnosticState,
    onSetLeftAction: (SwipeAction) -> Unit,
    onSetRightAction: (SwipeAction) -> Unit,
    onSetPlayPauseAction: (SwipeAction) -> Unit,
    onStartCalibration: (isLeft: Boolean) -> Unit,
    onCancelCalibration: () -> Unit,
    onClearCalibration: () -> Unit,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hardware Mapping",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Configure gesture actions and calibrate Bluetooth tap detection",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                Box(
                    modifier = Modifier
                        .border(1.dp, SupabaseBorder, RoundedCornerShape(20.dp))
                        .background(SupabaseSurface)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "BLUETOOTH HID",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = SupabaseGreen,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Live Signal Reception Status Bar
        item {
            val last = diagnosticState.lastEvent
            val isA11yOn = diagnosticState.isAccessibilityEnabled

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (last != null && last.wasHandledAsSwipe)
                        SupabaseGreen.copy(alpha = 0.10f)
                    else
                        SupabaseSurface
                ),
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (last != null && last.wasHandledAsSwipe) SupabaseGreen.copy(alpha = 0.4f) else SupabaseBorder
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (last != null) SupabaseGreen else CyanNeon)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "LIVE HARDWARE TAP MONITOR",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = if (last != null) SupabaseGreen else TextSecondary,
                                fontSize = 10.sp
                            )
                        }

                        Text(
                            text = if (isA11yOn) "ENGINE READY" else "A11Y OFF",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (isA11yOn) SupabaseGreen else AmberWarning,
                            fontSize = 10.sp
                        )
                    }

                    if (last != null) {
                        Text(
                            text = "Signal: ${last.keyCodeName} • Action: ${last.resultingAction?.label ?: "None"}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = last.note,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = if (last.wasHandledAsSwipe) SupabaseGreen else TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        Text(
                            text = "Awaiting input • Tap your left or right earbud to test signal reception",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Left & Right Action Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Left Earbud Action
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon.copy(alpha = 0.15f))
                                    .border(1.dp, CyanNeon.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("L", color = CyanNeon, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Left Earbud Tap Action",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Triggered on KEYCODE_MEDIA_PREVIOUS or custom left map",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SwipeAction.entries.forEach { action ->
                                FilterChip(
                                    selected = settings.leftBudAction == action,
                                    onClick = { onSetLeftAction(action) },
                                    label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanNeon.copy(alpha = 0.15f),
                                        selectedLabelColor = CyanNeon,
                                        containerColor = SupabaseSurfaceVariant,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = settings.leftBudAction == action,
                                        borderColor = SupabaseBorder,
                                        selectedBorderColor = CyanNeon.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("left_action_${action.name.lowercase()}")
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(SupabaseBorderSubtle)
                    )

                    // Right Earbud Action
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(VioletElectric.copy(alpha = 0.15f))
                                    .border(1.dp, VioletElectric.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("R", color = VioletElectric, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Right Earbud Tap Action",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Triggered on KEYCODE_MEDIA_NEXT or custom right map",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SwipeAction.entries.forEach { action ->
                                FilterChip(
                                    selected = settings.rightBudAction == action,
                                    onClick = { onSetRightAction(action) },
                                    label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VioletElectric.copy(alpha = 0.15f),
                                        selectedLabelColor = VioletElectric,
                                        containerColor = SupabaseSurfaceVariant,
                                        labelColor = TextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = settings.rightBudAction == action,
                                        borderColor = SupabaseBorder,
                                        selectedBorderColor = VioletElectric.copy(alpha = 0.5f)
                                    ),
                                    modifier = Modifier.testTag("right_action_${action.name.lowercase()}")
                                )
                            }
                        }
                    }
                }
            }
        }

        // Live Calibration & Earbud Tap Detector
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = SupabaseGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Signal Keycode Calibration",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Listen for Bluetooth hardware keycodes sent by your earbud double/triple taps.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isCalibratingL = GestureDispatcher.isCalibratingLeft
                        val isCalibratingR = GestureDispatcher.isCalibratingRight

                        Button(
                            onClick = {
                                if (isCalibratingL) onCancelCalibration() else onStartCalibration(true)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCalibratingL) AmberWarning else CyanNeon
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calibrate_left_button")
                        ) {
                            Text(
                                text = if (isCalibratingL) "Listening... Tap L" else "Calibrate Left",
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }

                        Button(
                            onClick = {
                                if (isCalibratingR) onCancelCalibration() else onStartCalibration(false)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCalibratingR) AmberWarning else VioletElectric
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("calibrate_right_button")
                        ) {
                            Text(
                                text = if (isCalibratingR) "Listening... Tap R" else "Calibrate Right",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detected signal cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Key Result
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SupabaseSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorderSubtle),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "LEFT CODE:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                val lKey = diagnosticState.leftCalibratedKey
                                Text(
                                    text = if (lKey != null) KeyEvent.keyCodeToString(lKey) else "None",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (lKey != null) CyanNeon else TextSecondary
                                )
                            }
                        }

                        // Right Key Result
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SupabaseSurfaceVariant),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorderSubtle),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    "RIGHT CODE:",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                val rKey = diagnosticState.rightCalibratedKey
                                Text(
                                    text = if (rKey != null) KeyEvent.keyCodeToString(rKey) else "None",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (rKey != null) VioletElectric else TextSecondary
                                )
                            }
                        }
                    }

                    // Calibration Analysis Message
                    if (diagnosticState.calibrationMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        if (diagnosticState.areBudsDistinguishable == true) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SupabaseGreen.copy(alpha = 0.12f))
                                    .border(1.dp, SupabaseGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = diagnosticState.calibrationMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SupabaseGreen
                                    )
                                }
                            }
                        } else {
                            WarningBanner(
                                title = "Hardware Signal Analysis",
                                message = diagnosticState.calibrationMessage
                            )
                        }
                    }

                    if (diagnosticState.leftCalibratedKey != null || diagnosticState.rightCalibratedKey != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = onClearCalibration,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Reset Calibration", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        }
                    }
                }
            }
        }

        // Single Key Fallback Setting (for earbuds that only have 1 media code)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fallback Action (Single-Signal Earbuds)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "When earbuds output generic KEYCODE_MEDIA_PLAY_PAUSE or Headset Hook:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(SwipeAction.SWIPE_UP, SwipeAction.SWIPE_DOWN, SwipeAction.NONE).forEach { action ->
                            FilterChip(
                                selected = settings.playPauseAction == action,
                                onClick = { onSetPlayPauseAction(action) },
                                label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SupabaseGreen.copy(alpha = 0.15f),
                                    selectedLabelColor = SupabaseGreen,
                                    containerColor = SupabaseSurfaceVariant,
                                    labelColor = TextSecondary
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = settings.playPauseAction == action,
                                    borderColor = SupabaseBorder,
                                    selectedBorderColor = SupabaseGreen.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.testTag("play_pause_action_${action.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        // OEM Earbud Remapping Guide Card (Realme, OnePlus, Sony, Samsung)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = SupabaseGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to Configure Realme, OnePlus & Galaxy Buds",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SupabaseSurfaceVariant)
                            .border(1.dp, SupabaseBorderSubtle, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "1. Open your companion app (Realme Link, HeyMelody, Galaxy Wearable, or Sony Headphones).\n" +
                                    "2. Open Gesture / Touch Control settings.\n" +
                                    "3. Set Left Bud Double/Triple Tap -> 'Previous Track'.\n" +
                                    "4. Set Right Bud Double/Triple Tap -> 'Next Track'.\n" +
                                    "5. BudSwipe receives distinct PREVIOUS & NEXT events, enabling full bi-directional scroll control!",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

