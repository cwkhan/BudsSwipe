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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.engine.GestureDispatcher
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.model.SwipeAction
import com.example.ui.components.WarningBanner
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GreenSuccess
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
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Earbud Mapping",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Configure gesture actions and calibrate Bluetooth tap detection.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Left & Right Action Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Left Earbud Action
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(CyanNeon.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("L", color = CyanNeon, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Left Earbud Tap Action",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SwipeAction.entries.forEach { action ->
                                FilterChip(
                                    selected = settings.leftBudAction == action,
                                    onClick = { onSetLeftAction(action) },
                                    label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = CyanNeon.copy(alpha = 0.2f),
                                        selectedLabelColor = CyanNeon
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
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    )

                    // Right Earbud Action
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(VioletElectric.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("R", color = VioletElectric, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Right Earbud Tap Action",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SwipeAction.entries.forEach { action ->
                                FilterChip(
                                    selected = settings.rightBudAction == action,
                                    onClick = { onSetRightAction(action) },
                                    label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = VioletElectric.copy(alpha = 0.2f),
                                        selectedLabelColor = VioletElectric
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.TouchApp,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Earbud Signal Calibration",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Test left and right earbuds independently to verify if your model outputs separate key codes.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            shape = RoundedCornerShape(12.dp),
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
                            shape = RoundedCornerShape(12.dp),
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
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Left Detected:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val lKey = diagnosticState.leftCalibratedKey
                                Text(
                                    text = if (lKey != null) KeyEvent.keyCodeToString(lKey) else "Not tested",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lKey != null) CyanNeon else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Right Key Result
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Right Detected:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val rKey = diagnosticState.rightCalibratedKey
                                Text(
                                    text = if (rKey != null) KeyEvent.keyCodeToString(rKey) else "Not tested",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rKey != null) VioletElectric else MaterialTheme.colorScheme.onSurfaceVariant
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
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GreenSuccess.copy(alpha = 0.15f))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = diagnosticState.calibrationMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GreenSuccess
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
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = onClearCalibration,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Clear Calibration Data", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Single Key Fallback Setting (for earbuds that only have 1 media code)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Fallback Action (Single-Signal Buds)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "When earbuds output KEYCODE_MEDIA_PLAY_PAUSE or generic headset hook:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(SwipeAction.SWIPE_UP, SwipeAction.SWIPE_DOWN, SwipeAction.NONE).forEach { action ->
                            FilterChip(
                                selected = settings.playPauseAction == action,
                                onClick = { onSetPlayPauseAction(action) },
                                label = { Text(action.label, style = MaterialTheme.typography.labelSmall) },
                                modifier = Modifier.testTag("play_pause_action_${action.name.lowercase()}")
                            )
                        }
                    }
                }
            }
        }

        // Realme & Earbud Remapping Tutorial Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "How to Configure Realme / Other Earbuds",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "1. Open your earbud app (Realme Link, HeyMelody, Galaxy Wearable, or Sony Connect).\n" +
                                "2. Navigate to Button / Gesture Settings.\n" +
                                "3. Set Left Earbud Double Tap -> 'Previous Track'.\n" +
                                "4. Set Right Earbud Double Tap -> 'Next Track'.\n" +
                                "5. BudSwipe will receive distinct KEYCODE_MEDIA_PREVIOUS and KEYCODE_MEDIA_NEXT events, granting 100% independent upward and downward swiping!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = MaterialTheme.typography.bodySmall.lineHeight
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
