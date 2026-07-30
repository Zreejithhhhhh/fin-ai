package com.moneymoment.ai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    primaryContainer = AccentBlue.copy(alpha = 0.15f),
    secondary = AccentPurple,
    onSecondary = TextPrimary,
    secondaryContainer = AccentPurple.copy(alpha = 0.15f),
    tertiary = AccentCyan,
    background = DarkSurface,
    onBackground = TextPrimary,
    surface = DarkSurfaceVariant,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = TextSecondary,
    outline = DarkOutline,
    error = AccentRed,
    onError = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = AccentBlue,
    onPrimary = TextPrimary,
    primaryContainer = AccentBlue.copy(alpha = 0.1f),
    secondary = AccentPurple,
    onSecondary = TextPrimary,
    secondaryContainer = AccentPurple.copy(alpha = 0.1f),
    tertiary = AccentCyan,
    background = LightSurface,
    onBackground = DarkSurface,
    surface = LightSurfaceVariant,
    onSurface = DarkSurface,
    surfaceVariant = LightSurfaceContainer,
    onSurfaceVariant = DarkSurface.copy(alpha = 0.6f),
    outline = LightOutline,
    error = AccentRed,
    onError = TextPrimary
)

@Composable
fun MoneyMomentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MoneyMomentTypography,
        content = content
    )
}
