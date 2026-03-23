package com.example.notesapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Color(0xFF6750A4),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFFF5F5F5),
    surface = Color(0xFFFFFFFF),
    onPrimary = Color.White,
    onSurface = Color.Black
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFD0BCFF),
    secondary = Color(0xFF03DAC5),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = Color.Black,
    onSurface = Color.White
)

@Composable
fun NotesAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}