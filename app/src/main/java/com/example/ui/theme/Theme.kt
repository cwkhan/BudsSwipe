package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SupabaseDarkColorScheme = darkColorScheme(
    primary = SupabaseGreen,
    onPrimary = Color(0xFF042F1A),
    primaryContainer = SupabaseGreenDark,
    onPrimaryContainer = Color(0xFF6EE7B7),
    secondary = CyanNeon,
    onSecondary = Color(0xFF001A24),
    secondaryContainer = Color(0xFF0C4A6E),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = VioletElectric,
    onTertiary = Color.White,
    background = SupabaseBg,
    onBackground = TextPrimary,
    surface = SupabaseSurface,
    onSurface = TextPrimary,
    surfaceVariant = SupabaseSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    surfaceContainer = SupabaseSurfaceContainer,
    outline = SupabaseBorder
)

private val SupabaseLightColorScheme = lightColorScheme(
    primary = SupabaseGreenHover,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF065F46),
    secondary = BlueElectric,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = Color(0xFF1E40AF),
    tertiary = VioletElectric,
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = Color(0xFF111827),
    surface = LightSurface,
    onSurface = Color(0xFF111827),
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = Color(0xFF4B5563),
    outline = LightOutline
)

@Composable
fun BudSwipeTheme(
    darkTheme: Boolean = true, // Default to sleek Supabase Dark aesthetic
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> SupabaseDarkColorScheme
        else -> SupabaseLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
