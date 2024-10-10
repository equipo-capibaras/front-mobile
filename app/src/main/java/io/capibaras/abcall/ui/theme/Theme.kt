package io.capibaras.abcall.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue700,
    secondary = Blue500,
    tertiary = Blue400,
    background = Blue950,
    surface = Blue900,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Blue50,
    onSurface = Blue50,
    outline = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Blue300,
    tertiary = Blue200,
    background = Blue50,
    surface = Blue100,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Blue700,
    onBackground = Blue800,
    onSurface = Blue900,
    outline = Blue500,
)

@Composable
fun ABCallTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customColors = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(LocalCustomColors provides customColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
