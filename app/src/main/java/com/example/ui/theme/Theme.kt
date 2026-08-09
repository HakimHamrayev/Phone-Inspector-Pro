package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PolishPrimaryDark,
    primaryContainer = PolishPrimaryContainerDark,
    onPrimaryContainer = PolishPrimaryContainer,
    secondary = PolishSecondary,
    tertiary = PolishTertiary,
    background = PolishBackgroundDark,
    surface = PolishSurfaceDark,
    surfaceVariant = PolishSurfaceVariantDark,
    outline = PolishOutlineDark,
    onPrimary = PolishBackgroundDark,
    onSecondary = PolishSurfaceLight,
    onBackground = PolishTextPrimaryDark,
    onSurface = PolishTextPrimaryDark,
    onSurfaceVariant = PolishTextSecondaryDark,
    error = PolishError
)

private val LightColorScheme = lightColorScheme(
    primary = PolishPrimary,
    primaryContainer = PolishPrimaryContainer,
    onPrimaryContainer = PolishOnPrimaryContainer,
    secondary = PolishSecondary,
    tertiary = PolishTertiary,
    background = PolishBackgroundLight,
    surface = PolishSurfaceLight,
    surfaceVariant = PolishSurfaceVariantLight,
    outline = PolishOutlineLight,
    onPrimary = PolishSurfaceLight,
    onSecondary = PolishSurfaceLight,
    onBackground = PolishTextPrimaryLight,
    onSurface = PolishTextPrimaryLight,
    onSurfaceVariant = PolishTextSecondaryLight,
    error = PolishError
)

@Composable
fun PhoneInspectorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
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

