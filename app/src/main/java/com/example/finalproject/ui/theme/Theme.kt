package com.example.finalproject.ui.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = SteamBlueLight,
    onPrimary = SteamOnPrimary,
    secondary = SteamAccent,
    onSecondary = SteamOnPrimary,
    tertiary = SteamAccentLight,
    onTertiary = SteamOnPrimary,
    background = SteamBackground,
    onBackground = SteamOnBackground,
    surface = SteamSurface,
    onSurface = SteamOnSurface,
    surfaceVariant = SteamSurfaceVariant,
    onSurfaceVariant = SteamOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = SteamBlue,
    onPrimary = SteamOnPrimary,
    secondary = SteamAccentDark,
    onSecondary = SteamOnPrimary,
    tertiary = SteamAccent,
    onTertiary = SteamOnPrimary,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF1C1B1F)
)

@Composable
fun FinalProjectTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}