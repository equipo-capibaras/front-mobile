package io.capibaras.abcall.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val success: Color,
    val onSuccess: Color,
    val pill: Color,
    val outlineButtonContent: Color,
)

val LightCustomColors = CustomColors(
    success = Color(0xFF218838),
    onSuccess = Color.White,
    pill = Blue400,
    outlineButtonContent = Blue500,
)

val DarkCustomColors = CustomColors(
    success = Color(0xFFADD5AD),
    onSuccess = Color(0xFF0A500C),
    pill = Blue500,
    outlineButtonContent = Color.White
)

val LocalCustomColors = compositionLocalOf { LightCustomColors }