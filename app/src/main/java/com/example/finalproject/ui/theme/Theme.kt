package com.example.finalproject.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable


private val DarkColorScheme = darkColorScheme(
    primary = NoirPrimary,
    onPrimary = NoirOnPrimary,
    primaryContainer = NoirGray, // Dark container for chips/buttons
    onPrimaryContainer = NoirWhite,
    secondary = NoirGray,
    onSecondary = NoirOnPrimary,
    secondaryContainer = NoirDarkGray, // Slightly different dark
    onSecondaryContainer = NoirWhite,
    tertiary = NoirLightGray,
    onTertiary = NoirOnPrimary,
    background = NoirBackground,
    onBackground = NoirOnBackground,
    surface = NoirSurface,
    onSurface = NoirOnSurface,
    surfaceVariant = NoirGray,
    onSurfaceVariant = NoirOnSurface
)


@Composable
fun FinalProjectTheme(
    // Dynamic color is removed to enforce Noir aesthetics
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Enforce Noir (Dark) scheme always
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}