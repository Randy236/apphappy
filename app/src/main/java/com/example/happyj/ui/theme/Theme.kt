package com.example.happyj.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val LightScheme = lightColorScheme(
    primary = HappyGreen,
    onPrimary = Color.White,
    primaryContainer = HappyGreenLight,
    onPrimaryContainer = HappyGreenDark,
    secondary = HappyCoral,
    onSecondary = Color.White,
    secondaryContainer = HappyCoralSoft,
    onSecondaryContainer = Color(0xFF9A3412),
    tertiary = HappySky,
    onTertiary = Color.White,
    tertiaryContainer = HappySkySoft,
    onTertiaryContainer = Color(0xFF1E40AF),
    background = HappyBgTop,
    onBackground = HappyTextPrimary,
    surface = HappySurface,
    onSurface = HappyTextPrimary,
    surfaceVariant = HappySurfaceSoft,
    onSurfaceVariant = HappyTextSecondary,
    outline = HappyBorder,
    outlineVariant = HappyBorderLight,
    error = OcupadoRojo,
    onError = Color.White,
    errorContainer = OcupadoRojoBg,
    onErrorContainer = Color(0xFF991B1B),
)

private val HappyShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

@Composable
fun HappyjTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightScheme,
        typography = Typography,
        shapes = HappyShapes,
        content = content,
    )
}
