package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Accessibility
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.unit.sp
import com.example.engine.BluetoothMonitor
import com.example.model.DiagnosticState
import com.example.model.MediaKeyEventRecord
import com.example.ui.theme.PinkNeon
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
            .background(SupabaseBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Telemetry & Logs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = SupabaseGreen.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseGreen.copy(alpha = 0.35f))
                        ) {
                            Text(
                                text = "LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                color = SupabaseGreen,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "Real-time hardware media key telemetry & engine logs.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                OutlinedButton(
                    onClick = onRefreshStatus,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                    modifier = Modifier.testTag("refresh_diagnostics_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = TextPrimary, modifier = Modifier.size(15.dp))
                    Text(" Refresh", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = TextPrimary)
                }
            }
        }

        // Subsystems Health (Supabase Terminal Style)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Subsystem Health Check",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )

                    // Accessibility Engine
                    DiagnosticRow(
                        icon = Icons.Default.Accessibility,
                        title = "Accessibility Gesture Engine",
                        subtitle = if (diagnosticState.isAccessibilityEnabled) "Ready & Running • dispatchGesture active" else "Disabled in Android Settings",
                        isHealthy = diagnosticState.isAccessibilityEnabled
                    )

                    // Bluetooth Profile
                    DiagnosticRow(
                        icon = Icons.Default.Bluetooth,
                        title = "Bluetooth Audio Peripheral",
                        subtitle = bluetoothState.connectedDeviceName ?: "Disconnected",
                        isHealthy = bluetoothState.isConnected
                    )

                    // MediaSession Service
                    DiagnosticRow(
                        icon = Icons.Default.GraphicEq,
                        title = "MediaSession Foreground Core",
                        subtitle = if (diagnosticState.isMediaSessionActive) "Active • MediaButton focus held" else "Inactive",
                        isHealthy = diagnosticState.isMediaSessionActive
                    )

                    // Foreground App Detector
                    DiagnosticRow(
                        icon = Icons.Default.Smartphone,
                        title = "Foreground App Focus",
                        subtitle = "${diagnosticState.currentForegroundApp} (${if (diagnosticState.isInTargetApp) "In Target App" else "Filtered Out"})",
                        isHealthy = diagnosticState.isInTargetApp
                    )
                }
            }
        }

        // Quick System Shortcuts
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "System Shortcuts",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Accessibility", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = TextPrimary)
                        }

                        OutlinedButton(
                            onClick = {
                                context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS))
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Bluetooth", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = TextPrimary)
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
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("App Info", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Live Event Log Stream
        item {
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
                            .background(SupabaseGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Event Stream (${diagnosticState.recentEvents.size})",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = TextSecondary
                    )
                }

                if (diagnosticState.recentEvents.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onClearEvents,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                        modifier = Modifier.testTag("clear_events_button")
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                        Text(" Clear", style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace, color = TextMuted)
                    }
                }
            }
        }

        // Empty state or event items
        if (diagnosticState.recentEvents.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SupabaseSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Awaiting hardware media keys...",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = TextSecondary
                        )
                        Text(
                            text = "Tap your Bluetooth earbuds to stream raw key telemetry.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
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
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Troubleshooting Guide", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }

                    Text(
                        text = "• Event not received? Another media app (e.g. Spotify) might be holding audio focus. Turn BudSwipe off and on once to reclaim focus.\n\n" +
                                "• Gestures not firing? Ensure BudSwipe Reel Controller is enabled in Android Accessibility Settings.\n\n" +
                                "• Android 13/14 Restricted Settings? If accessibility is grayed out, open Settings > Apps > BudSwipe > 3 dots > 'Allow restricted settings'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
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
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(if (isHealthy) SupabaseGreen.copy(alpha = 0.12f) else PinkNeon.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isHealthy) SupabaseGreen else PinkNeon,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = if (isHealthy) TextSecondary else PinkNeon
            )
        }
        Icon(
            imageVector = if (isHealthy) Icons.Default.CheckCircle else Icons.Default.Error,
            contentDescription = null,
            tint = if (isHealthy) SupabaseGreen else PinkNeon,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun EventRecordCard(
    record: MediaKeyEventRecord,
    timeStr: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (record.wasHandledAsSwipe) SupabaseGreen.copy(alpha = 0.35f) else SupabaseBorderSubtle
        ),
        modifier = Modifier.fillMaxWidth()
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
                        color = if (record.wasHandledAsSwipe) SupabaseGreen else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${record.keyCode})",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = TextMuted
                    )
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextMuted,
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
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = TextSecondary
                )

                if (record.resultingAction != null) {
                    Text(
                        text = "➔ ${record.resultingAction.label}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        color = SupabaseGreen
                    )
                }
            }

            if (record.note.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.note,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = if (record.wasHandledAsSwipe) SupabaseGreen else TextMuted
                )
            }
        }
    }
}
