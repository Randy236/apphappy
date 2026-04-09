package com.example.happyj.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightScheme = lightColorScheme(
    primary = HappyGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB8F0D8),
    secondary = HappyTextSecondary,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = HappyTextPrimary,
    onSurface = HappyTextPrimary,
)

@Composable
fun HappyjTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = Typography,
        content = content,
    )
}
