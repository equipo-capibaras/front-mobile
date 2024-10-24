package io.capibaras.abcall.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import io.capibaras.abcall.R

val FiraSans = FontFamily(
    Font(R.font.fira_sans_regular, FontWeight.Normal),
    Font(R.font.fira_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.fira_sans_medium, FontWeight.Medium),
    Font(R.font.fira_sans_light, FontWeight.Light),
    Font(R.font.fira_sans_light_italic, FontWeight.Light, FontStyle.Italic)
)

val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        letterSpacing = 0.15.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        color = Blue700,
        letterSpacing = 0.4.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FiraSans,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.4.sp,
    ),
)

val linkText = TextStyle(
    fontFamily = FiraSans,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
)

val pillText = TextStyle(
    fontFamily = FiraSans,
    fontWeight = FontWeight.Medium,
    fontSize = 14.sp,
    letterSpacing = 0.1.sp,
)
