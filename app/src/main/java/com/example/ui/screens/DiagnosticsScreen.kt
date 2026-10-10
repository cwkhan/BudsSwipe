package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.engine.BluetoothMonitor
import com.example.model.DiagnosticState
import com.example.model.MediaKeyEventRecord
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.VioletElectric
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DiagnosticsScreen(
    diagnosticState: DiagnosticState,
    bluetoothState: BluetoothMonitor.BluetoothState,
    onRefreshStatus: () -> Unit,
    onClearEvents: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val timeFormatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
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
                        text = "Diagnostics & Events",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Live telemetry of Bluetooth events and background services.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = onRefreshStatus,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("refresh_diagnostics_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", modifier = Modifier.size(16.dp))
                    Text(" Refresh", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Service Subsystem Health Cards
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Subsystem Health", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    // Accessibility Engine
                    DiagnosticRow(
                        icon = Icons.Default.Accessibility,
                        title = "Accessibility Swipe Engine",
                        subtitle = if (diagnosticState.isAccessibilityEnabled) "Ready & Running (dispatchGesture active)" else "Disabled in Android Settings",
                        isHealthy = diagnosticState.isAccessibilityEnabled
                    )

                    // Bluetooth Profile
                    DiagnosticRow(
                        icon = Icons.Default.Bluetooth,
                        title = "Bluetooth Audio Profile",
                        subtitle = bluetoothState.connectedDeviceName ?: "Disconnected",
                        isHealthy = bluetoothState.isConnected
                    )

                    // MediaSession Service
                    DiagnosticRow(
                        icon = Icons.Default.GraphicEq,
                        title = "MediaSession Foreground Service",
                        subtitle = if (diagnosticState.isMediaSessionActive) "Active (MediaButton focus held)" else "Inactive",
                        isHealthy = diagnosticState.isMediaSessionActive
                    )

                    // Foreground App Detector
                    DiagnosticRow(
                        icon = Icons.Default.Smartphone,
                        title = "Foreground App Focus",
                        subtitle = "${diagnosticState.currentForegroundApp} (${if (diagnosticState.isInTargetApp) "Target Active" else "Filtered"})",
                        isHealthy = diagnosticState.isInTargetApp
                    )
                }
            }
        }

        // Quick Settings Shortcuts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Quick System Settings", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Accessibility", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bluetooth", style = MaterialTheme.typography.labelSmall)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", context.packageName, null)
                                    }
                                    context.startActivity(intent)
                                } catch (_: Exception) {
                                    context.startActivity(Intent(Settings.ACTION_SETTINGS))
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("App Info", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Live Event Log Stream Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CyanNeon)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hardware Key Event Log",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (diagnosticState.recentEvents.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearEvents,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("clear_events_button")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                        Text(" Clear", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }

        // Live Event Log List
        if (diagnosticState.recentEvents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No media events received yet",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Tap your Bluetooth earbud or test button to see live telemetry.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(diagnosticState.recentEvents, key = { it.id }) { record ->
                EventRecordCard(record = record, timeStr = timeFormatter.format(Date(record.timestamp)))
            }
        }

        // Troubleshooting Guide
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = CyanNeon, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Troubleshooting Guide", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    Text(
                        text = "• Event not received? Another music app (like Spotify) may be hogging media button focus. Close running audio apps or press play/pause once in BudSwipe to reclaim focus.\n\n" +
                                "• Swipe not happening? Verify that BudSwipe Reel Controller is toggled ON under Android Accessibility Settings.\n\n" +
                                "• App stops swiping after 10 minutes? Disable Android Battery Optimization for BudSwipe so Android OS does not pause background monitoring.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DiagnosticRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isHealthy: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (isHealthy) GreenSuccess.copy(alpha = 0.15f) else PinkNeon.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHealthy) GreenSuccess else PinkNeon,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = if (isHealthy) MaterialTheme.colorScheme.onSurfaceVariant else PinkNeon
            )
        }
        Icon(
            imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isHealthy) GreenSuccess else PinkNeon,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun EventRecordCard(
    record: MediaKeyEventRecord,
    timeStr: String
) {
    val statusColor = if (record.wasHandledAsSwipe) GreenSuccess else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.dp,
                if (record.wasHandledAsSwipe) GreenSuccess.copy(alpha = 0.3f) else Color.Transparent,
                RoundedCornerShape(12.dp)
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.keyCodeName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (record.wasHandledAsSwipe) CyanNeon else MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${record.keyCode})",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${record.actionType} • ${record.sourceDevice}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (record.resultingAction != null) {
                    Text(
                        text = record.resultingAction.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = CyanNeon
                    )
                }
            }

            if (record.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = statusColor
                )
            }
        }
    }
}
