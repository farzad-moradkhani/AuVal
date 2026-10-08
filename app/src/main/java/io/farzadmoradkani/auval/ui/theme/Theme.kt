package io.farzadmoradkani.auval.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val DarkGold = Color(0xFFFFD86B)
val DarkBackground = Color(0xFF181818)
val DarkSurface = Color(0xFF242424)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGold,
    onPrimary = Color(0xFF3A2F00),

    secondary = Color(0xFFD0C7B8),
    onSecondary = Color(0xFF383838),

    background = DarkBackground,
    onBackground = Color.White,

    surface = DarkSurface,
    onSurface = Color.White,

    error = Color(0xFFFFB4AB)
)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Charcoal,

    secondary = Charcoal,
    onSecondary = Color.White,

    background = Background,
    onBackground = Charcoal,

    surface = Surface,
    onSurface = Charcoal,

    error = Error
)

@Composable
fun AuValTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
//@Composable
//fun AuValTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    // Dynamic color is available on Android 12+
//    dynamicColor: Boolean = false,
//    content: @Composable () -> Unit
//) {
//    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }
//
//        darkTheme -> DarkColorScheme
//        else -> LightColorScheme
//    }
//
//    MaterialTheme(
//        colorScheme = colorScheme,
//        typography = Typography,
//        content = content
//    )
//}