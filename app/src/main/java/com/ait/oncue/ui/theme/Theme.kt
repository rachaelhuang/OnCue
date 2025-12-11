package com.ait.oncue.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.ait.oncue.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val InstrumentSans = FontFamily(
    Font(R.font.instrument_sans, FontWeight.Normal),
    Font(R.font.instrument_sans, FontWeight.Medium),
    Font(R.font.instrument_sans_italic, FontWeight.Normal),
    )

val AppTypography = _root_ide_package_.androidx.compose.material3.Typography(
    bodyMedium = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),

    bodyLarge = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
)

val OnCueBackground = Color(0xFF09090B) // hex #09090B
val OnCueDarkGold = Color(0xFFC9A86A)

// OnCue brand colors - orange to pink gradient
val OnCueOrange = Color(0xFFFF7A3D)
val OnCuePink = Color(0xFFFF4B91)
val OnCueDarkGray = Color(0xFF1A1A1A)
val OnCueMediumGray = Color(0xFF2A2A2A)

val OnCueMediumDarkGray = Color(0xFF202020)

val OnCueAlmostBlack = Color(0xFF0F0F12)
val OnCueDarkGrayVariant = Color(0xFF18181B)

val OnCueLightGray = Color(0xFF3A3A3A)
val OnCueTextGray = Color(0xFF9E9E9E)

// Gradient
val OnCueGradientColors = listOf(OnCueOrange, OnCuePink)

val OnCueGradient = Brush.horizontalGradient(
    colors = listOf(OnCueOrange, OnCuePink)
)

val GrayGradient = Brush.horizontalGradient(
    colors = listOf(OnCueDarkGrayVariant, OnCueAlmostBlack)
)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color.Black,
    background = OnCueBackground,
    onBackground = Color.White,
    surface = Color(0xFF0D0D0D),
    onSurface = Color.White,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

//private val LightColorScheme = lightColorScheme(
//    primary = Purple40,
//    secondary = PurpleGrey40,
//    tertiary = Pink40
//
//    /* Other default colors to override
//    background = Color(0xFFFFFBFE),
//    surface = Color(0xFFFFFBFE),
//    onPrimary = Color.White,
//    onSecondary = Color.White,
//    onTertiary = Color.White,
//    onBackground = Color(0xFF1C1B1F),
//    onSurface = Color(0xFF1C1B1F),
//    */
//)

@Composable
fun OnCueTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}