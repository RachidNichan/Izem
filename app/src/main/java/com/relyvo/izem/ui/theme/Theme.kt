package com.relyvo.izem.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = IzemOrange,
    onPrimary = Color.White,
    primaryContainer = IzemOrangeContainer,
    onPrimaryContainer = OnIzemOrangeContainer,

    secondary = IzemAmber,
    onSecondary = Color.Black,
    secondaryContainer = IzemAmberContainer,

    tertiary = IzemBlue,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onSurface = DarkGray,
    outlineVariant = Color.LightGray
)

private val DarkColorScheme = darkColorScheme(
    primary = IzemOrange,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF5D4037),
    onPrimaryContainer = Color(0xFFFFCCBC),

    secondary = IzemAmber,
    background = Color(0xFF0F0F0F),
    surface = Color(0xFF1E1E1E)
)

@Composable
fun IzemTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}