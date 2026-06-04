package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NeonBlue,
    secondary = CosmicPurple,
    tertiary = LimeGreen,
    background = DarkBackground,
    surface = CardSurface,
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.Black,
    onBackground = WhitePure,
    onSurface = WhitePure,
    surfaceVariant = Color(0xFF201654),
    onSurfaceVariant = WhitePure,
    error = EmberOrange,
    onError = Color.White,
    outline = NeonBlue
)

@Composable
fun ScienceYear5Theme(
    darkTheme: Boolean = true, // Force gamified stellar dark mode by default
    content: @Composable () -> Unit
) {
    // We bypass dynamic schemas to ensure the kids' Neon Blue, Cosmic Purple and Lime Green are always active
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
