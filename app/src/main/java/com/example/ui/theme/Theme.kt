package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AmoledColorScheme = darkColorScheme(
    primary = AccentOrange,
    background = AmoledBackground,
    surface = AmoledCard,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = AmoledCard,
    onSurfaceVariant = Color.LightGray,
    outline = DarkDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentOrange,
    background = DarkBackground,
    surface = DarkCard,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color.LightGray,
    outline = DarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = AccentOrange,
    background = LightBackground,
    surface = LightCard,
    onBackground = Color.Black,
    onSurface = Color.Black,
    surfaceVariant = LightCard,
    onSurfaceVariant = Color.DarkGray,
    outline = LightDivider
)

@Composable
fun ReadTrackerTheme(
    themeMode: Int = 1, // 0 = AMOLED, 1 = Dark, 2 = Light
    content: @Composable () -> Unit
) {
    val colorScheme = when (themeMode) {
        0 -> AmoledColorScheme
        2 -> LightColorScheme
        else -> DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
