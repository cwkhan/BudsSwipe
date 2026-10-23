package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.engine.BluetoothMonitor
import com.example.model.BudSwipeSettings
import com.example.model.DiagnosticState
import com.example.model.SwipeAction
import com.example.model.SwipeSpeedPreset
import com.example.model.TargetApp
import com.example.ui.components.StatusPill
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.SupabaseBg
import com.example.ui.theme.SupabaseBorder
import com.example.ui.theme.SupabaseBorderSubtle
import com.example.ui.theme.SupabaseGreen
import com.example.ui.theme.SupabaseGreenGlow
import com.example.ui.theme.SupabaseSurface
import com.example.ui.theme.SupabaseSurfaceContainer
import com.example.ui.theme.SupabaseSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VioletElectric

@Composable
fun HomeScreen(
    settings: BudSwipeSettings,
    diagnosticState: DiagnosticState,
    bluetoothState: BluetoothMonitor.BluetoothState,
    onToggleService: (Context, Boolean) -> Unit,
    onSelectTargetApp: (TargetApp) -> Unit,
    onSelectSpeedPreset: (SwipeSpeedPreset) -> Unit,
    onManualTestSwipe: (Context, SwipeAction, (Boolean, String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var testFeedbackMessage by remember { mutableStateOf<String?>(null) }
    var testFeedbackSuccess by remember { mutableStateOf(true) }

    fun launchTargetApp(packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
            } else {
                val storeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
                context.startActivity(storeIntent)
            }
        } catch (_: Exception) {
            // Handled
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SupabaseBg)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Supabase Topbar Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .border(1.dp, SupabaseBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_budswipe_mark),
                            contentDescription = "BudSwipe Official Logo",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BudSwipe",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                color = SupabaseGreen.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseGreen.copy(alpha = 0.35f))
                            ) {
                                Text(
                                    text = "PRO v1.2",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = SupabaseGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = "Supabase Engine • Hands-Free Short Video Control",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                StatusPill(
                    label = if (settings.isServiceEnabled) "ONLINE" else "STANDBY",
                    isActive = settings.isServiceEnabled,
                    activeColor = SupabaseGreen
                )
            }
        }

        // Supabase Hero Card (Master Switch + Glow)
        item {
            val cardBorderColor by animateColorAsState(
                targetValue = if (settings.isServiceEnabled) SupabaseGreen.copy(alpha = 0.5f) else SupabaseBorder,
                label = "masterBorder"
            )

            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorderColor, RoundedCornerShape(14.dp))
                    .testTag("master_service_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (settings.isServiceEnabled)
                                Brush.radialGradient(
                                    colors = listOf(SupabaseGreenGlow, Color.Transparent),
                                    radius = 700f
                                )
                            else
                                Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (settings.isServiceEnabled)
                                            SupabaseGreen.copy(alpha = 0.15f)
                                        else
                                            SupabaseSurfaceContainer
                                    )
                                    .border(
                                        1.dp,
                                        if (settings.isServiceEnabled) SupabaseGreen.copy(alpha = 0.4f) else SupabaseBorder,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PowerSettingsNew,
                                    contentDescription = null,
                                    tint = if (settings.isServiceEnabled) SupabaseGreen else TextMuted,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "BudSwipe Gesture Core",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (settings.isServiceEnabled)
                                        "Telemetry Active • Intercepting Earbud Taps"
                                    else
                                        "Paused • Switch ON to enable hands-free swipes",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (settings.isServiceEnabled) SupabaseGreen else TextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = settings.isServiceEnabled,
                            onCheckedChange = { onToggleService(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = SupabaseGreen,
                                checkedTrackColor = SupabaseGreen.copy(alpha = 0.35f),
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = SupabaseSurfaceContainer
                            ),
                            modifier = Modifier.testTag("master_service_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Supabase-style Terminal status banner
                    Surface(
                        color = SupabaseSurfaceVariant,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = null,
                                    tint = SupabaseGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "core: ${if (settings.isServiceEnabled) "armed" else "idle"} | app: ${settings.targetApp.shortLabel.lowercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            }

                            Text(
                                text = "${settings.swipeDurationMs}ms / ${(settings.swipeDistancePercent * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = SupabaseGreen
                            )
                        }
                    }
                }
            }
        }

        // Subsystem Matrix (Accessibility + Bluetooth)
        item {
            Text(
                text = "System Subsystems",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(2.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Accessibility Service
                Card(
                    colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (diagnosticState.isAccessibilityEnabled)
                                            SupabaseGreen.copy(alpha = 0.12f)
                                        else
                                            PinkNeon.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccessibilityNew,
                                    contentDescription = null,
                                    tint = if (diagnosticState.isAccessibilityEnabled) SupabaseGreen else PinkNeon,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Accessibility Engine",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (diagnosticState.isAccessibilityEnabled)
                                        "Ready • dispatchGesture active"
                                    else
                                        "Required • Tap to enable in Settings",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (diagnosticState.isAccessibilityEnabled) SupabaseGreen else PinkNeon
                                )
                            }
                        }

                        if (!diagnosticState.isAccessibilityEnabled) {
                            Button(
                                onClick = {
                                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = PinkNeon),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("enable_accessibility_button")
                            ) {
                                Text("Enable", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Active",
                                tint = SupabaseGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Bluetooth Hardware Status
                Card(
                    colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (bluetoothState.isConnected)
                                            Color(0xFF38BDF8).copy(alpha = 0.12f)
                                        else
                                            SupabaseSurfaceContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Bluetooth,
                                    contentDescription = null,
                                    tint = if (bluetoothState.isConnected) Color(0xFF38BDF8) else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Bluetooth Audio",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = bluetoothState.connectedDeviceName ?: "No earbuds connected",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (bluetoothState.isConnected) Color(0xFF38BDF8) else TextMuted
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier.testTag("bluetooth_settings_button")
                        ) {
                            Text("Pair / Check", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                        }
                    }
                }
            }
        }

        // Supported Apps Matrix (YouTube, IG, FB, TikTok)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Platform Routing Matrix",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Text(
                    text = settings.targetApp.shortLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = SupabaseGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "BudSwipe only simulates swipes when a selected platform is in the foreground:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Row 1 of Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TargetApp.entries.take(3).forEach { app ->
                    val selected = settings.targetApp == app
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectTargetApp(app) },
                        label = { Text(app.shortLabel, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SupabaseGreen.copy(alpha = 0.20f),
                            selectedLabelColor = SupabaseGreen,
                            containerColor = SupabaseSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) SupabaseGreen.copy(alpha = 0.5f) else SupabaseBorder
                        ),
                        modifier = Modifier.testTag("target_app_${app.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Row 2 of Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TargetApp.entries.drop(3).forEach { app ->
                    val selected = settings.targetApp == app
                    FilterChip(
                        selected = selected,
                        onClick = { onSelectTargetApp(app) },
                        label = { Text(app.shortLabel, style = MaterialTheme.typography.labelSmall, fontFamily = FontFamily.Monospace) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF38BDF8).copy(alpha = 0.20f),
                            selectedLabelColor = Color(0xFF38BDF8),
                            containerColor = SupabaseSurface,
                            labelColor = TextSecondary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selected,
                            borderColor = if (selected) Color(0xFF38BDF8).copy(alpha = 0.5f) else SupabaseBorder
                        ),
                        modifier = Modifier.testTag("target_app_${app.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 1-Tap App Test Launchers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilledTonalButton(
                    onClick = { launchTargetApp("com.google.android.youtube") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SupabaseSurfaceContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SmartDisplay, contentDescription = null, tint = Color(0xFFFF0033), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("YouTube", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                }

                FilledTonalButton(
                    onClick = { launchTargetApp("com.instagram.android") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SupabaseSurfaceContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.FlashOn, contentDescription = null, tint = Color(0xFFE1306C), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reels", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                }

                FilledTonalButton(
                    onClick = { launchTargetApp("com.facebook.katana") },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = SupabaseSurfaceContainer),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = Color(0xFF1877F2), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Facebook", style = MaterialTheme.typography.labelSmall, color = TextPrimary)
                }
            }
        }

        // Gesture Speed Profiles
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = SupabaseGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gesture Speed Preset",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Calibrate swipe velocity for Shorts & Reels algorithms:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                    borderColor = if (selected) SupabaseGreen.copy(alpha = 0.5f) else SupabaseBorderSubtle
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Manual Swipe Test Pad
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = SupabaseGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Interactive Gesture Simulator",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = "Verify that Android Accessibility simulates touch flick gestures cleanly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                onManualTestSwipe(context, SwipeAction.SWIPE_UP) { ok, msg ->
                                    testFeedbackSuccess = ok
                                    testFeedbackMessage = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SupabaseGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_swipe_up_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Swipe UP (Next)", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }

                        Button(
                            onClick = {
                                onManualTestSwipe(context, SwipeAction.SWIPE_DOWN) { ok, msg ->
                                    testFeedbackSuccess = ok
                                    testFeedbackMessage = msg
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SupabaseSurfaceContainer),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("test_swipe_down_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Swipe DOWN", color = TextPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    AnimatedVisibility(visible = testFeedbackMessage != null) {
                        testFeedbackMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (testFeedbackSuccess) SupabaseGreen.copy(alpha = 0.12f) else PinkNeon.copy(alpha = 0.12f)
                                    )
                                    .border(
                                        1.dp,
                                        if (testFeedbackSuccess) SupabaseGreen.copy(alpha = 0.35f) else PinkNeon.copy(alpha = 0.35f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (testFeedbackSuccess) Icons.Default.CheckCircle else Icons.Default.Warning,
                                        contentDescription = null,
                                        tint = if (testFeedbackSuccess) SupabaseGreen else PinkNeon,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontFamily = FontFamily.Monospace,
                                        color = if (testFeedbackSuccess) SupabaseGreen else PinkNeon
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Supabase Metrics Dashboard HUD
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SupabaseSurface),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SupabaseBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${diagnosticState.totalSwipesDispatched}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = SupabaseGreen
                        )
                        Text(
                            text = "SWIPES FIRED",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(SupabaseBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${settings.swipeDurationMs}ms",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF38BDF8)
                        )
                        Text(
                            text = "FLICK TIME",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(26.dp)
                            .background(SupabaseBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${settings.cooldownMs}ms",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = VioletElectric
                        )
                        Text(
                            text = "DEBOUNCE",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
