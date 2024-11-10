package io.capibaras.abcall.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color

data class CustomColors(
    val success: Color,
    val onSuccess: Color,
    val pill: Color,
    val outlineButtonContent: Color,
    val warning: Color,
    val onWarning: Color,
    val neutral: Color
)

val LightCustomColors = CustomColors(
    success = Color(0xFC218838),
    onSuccess = Color.White,
    pill = Blue400,
    outlineButtonContent = Blue500,
    warning = Color(0xFCCC4D00),
    onWarning = Color.White,
    neutral = Color(0xFC637984)
)

val DarkCustomColors = CustomColors(
    success = Color(0xFCADD5AD),
    onSuccess = Color(0xFC0A500C),
    pill = Blue700,
    outlineButtonContent = Color.White,
    warning = Color(0xFFEEB289),
    onWarning = Color(0xFC7A3300),
    neutral = Color(0xFC637984)
)

val LocalCustomColors = compositionLocalOf { LightCustomColors }