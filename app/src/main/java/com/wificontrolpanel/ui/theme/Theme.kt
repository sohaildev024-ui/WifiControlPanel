package com.wificontrolpanel.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6),
    primaryContainer = Color(0xFF1565C0),
    secondary = Color(0xFF4DD0E1),
    secondaryContainer = Color(0xFF006064),
    tertiary = Color(0xFF81C784),
    tertiaryContainer = Color(0xFF2E7D32),
    surface = Color(0xFF121212),
    surfaceContainerHighest = Color(0xFF1E1E1E),
    background = Color(0xFF0A0A0A),
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onSurface = Color(0xFFFFFFFF),
    outline = Color(0xFF333333),
    error = Color(0xFFEF5350),
    errorContainer = Color(0xFFB71C1C),
    inverseSurface = Color(0xFFE0E0E0),
    inversePrimary = Color(0xFF1565C0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1565C0),
    primaryContainer = Color(0xFFBBDEFB),
    secondary = Color(0xFF006064),
    secondaryContainer = Color(0xFFB2EBF2),
    tertiary = Color(0xFF2E7D32),
    tertiaryContainer = Color(0xFFC8E6C9),
    surface = Color(0xFFFFFFFF),
    surfaceContainerHighest = Color(0xFFF5F5F5),
    background = Color(0xFFFAFAFA),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    outline = Color(0xFFBDBDBD),
    error = Color(0xFFC62828),
    errorContainer = Color(0xFFFFEBEE),
    inverseSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF64B5F6)
)

@Composable
fun WifiControlPanelTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

object Typography {
    val displayLarge = androidx.compose.material3.Typography.Default.displayLarge
    val displayMedium = androidx.compose.material3.Typography.Default.displayMedium
    val displaySmall = androidx.compose.material3.Typography.Default.displaySmall
    val headlineLarge = androidx.compose.material3.Typography.Default.headlineLarge
    val headlineMedium = androidx.compose.material3.Typography.Default.headlineMedium
    val headlineSmall = androidx.compose.material3.Typography.Default.headlineSmall
    val titleLarge = androidx.compose.material3.Typography.Default.titleLarge
    val titleMedium = androidx.compose.material3.Typography.Default.titleMedium
    val titleSmall = androidx.compose.material3.Typography.Default.titleSmall
    val bodyLarge = androidx.compose.material3.Typography.Default.bodyLarge
    val bodyMedium = androidx.compose.material3.Typography.Default.bodyMedium
    val bodySmall = androidx.compose.material3.Typography.Default.bodySmall
    val labelLarge = androidx.compose.material3.Typography.Default.labelLarge
    val labelMedium = androidx.compose.material3.Typography.Default.labelMedium
    val labelSmall = androidx.compose.material3.Typography.Default.labelSmall
}