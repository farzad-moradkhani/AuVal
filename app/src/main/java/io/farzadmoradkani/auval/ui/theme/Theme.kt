package io.farzadmoradkani.auval.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Core dark palette. Gold and Charcoal stay defined in Color.kt and are reused as-is.
val DarkGold = Color(0xFFFFD86B)
val DarkBackground = Color(0xFF181818)
val DarkSurface = Color(0xFF242424)

private val PrimaryContainerLight = Color(0xFFFFE7A3)
private val SecondaryLight = Color(0xFF6F6658)
private val SecondaryContainerLight = Color(0xFFEDE1D2)
private val TertiaryLight = Color(0xFF7A5900)
private val TertiaryContainerLight = Color(0xFFFFDF9B)
private val SurfaceVariantLight = Color(0xFFEFE6D9)
private val OnSurfaceVariantLight = Color(0xFF6B6255)
private val OutlineLight = Color(0xFF8C8375)
private val OutlineVariantLight = Color(0xFFD9D0C2)
private val ErrorContainerLight = Color(0xFFFFDAD6)
private val OnErrorContainerLight = Color(0xFF410002)
private val SurfaceDimLight = Color(0xFFE3DED6)
private val SurfaceBrightLight = Color(0xFFFFFBF5)
private val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
private val SurfaceContainerLowLight = Color(0xFFFBF7F1)
private val SurfaceContainerLight = Color(0xFFF5F0E9)
private val SurfaceContainerHighLight = Color(0xFFEFEAE3)
private val SurfaceContainerHighestLight = Color(0xFFE9E4DC)

private val OnDarkPrimary = Color(0xFF3A2F00)
private val PrimaryContainerDark = Color(0xFF554400)
private val OnPrimaryContainerDark = Color(0xFFFFE7A3)
private val SecondaryDark = Color(0xFFD0C7B8)
private val SecondaryContainerDark = Color(0xFF4B463C)
private val OnSecondaryContainerDark = Color(0xFFECE1D3)
private val TertiaryDark = Color(0xFFE7C45E)
private val OnTertiaryDark = Color(0xFF3E2E00)
private val TertiaryContainerDark = Color(0xFF5A4600)
private val OnTertiaryContainerDark = Color(0xFFFFDF9B)
private val OnBackgroundDark = Color(0xFFF2EFE9)
private val SurfaceVariantDark = Color(0xFF4A453D)
private val OnSurfaceVariantDark = Color(0xFFD0C7B8)
private val OutlineDark = Color(0xFF9A9286)
private val OutlineVariantDark = Color(0xFF4A453D)
private val ErrorDark = Color(0xFFFFB4AB)
private val OnErrorDark = Color(0xFF690005)
private val ErrorContainerDark = Color(0xFF93000A)
private val OnErrorContainerDark = Color(0xFFFFDAD6)
private val InverseSurfaceDark = Color(0xFFF2EFE9)
private val InverseOnSurfaceDark = Color(0xFF313030)
private val InversePrimaryDark = Color(0xFF7A5F00)
private val SurfaceDimDark = Color(0xFF141414)
private val SurfaceBrightDark = Color(0xFF3A3A3A)
private val SurfaceContainerLowestDark = Color(0xFF0F0F0F)
private val SurfaceContainerLowDark = Color(0xFF1C1C1C)
private val SurfaceContainerDark = Color(0xFF202020)
private val SurfaceContainerHighDark = Color(0xFF2A2A2A)
private val SurfaceContainerHighestDark = Color(0xFF303030)

private val LightColorScheme = lightColorScheme(
    primary = Gold,
    onPrimary = Charcoal,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = Charcoal,

    secondary = SecondaryLight,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = Charcoal,

    tertiary = TertiaryLight,
    onTertiary = Color.White,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = Charcoal,

    background = Background,
    onBackground = Charcoal,
    surface = Surface,
    onSurface = Charcoal,

    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,

    error = Error,
    onError = Color.White,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,

    inverseSurface = Charcoal,
    inverseOnSurface = BoneWhite,
    inversePrimary = Gold,
    scrim = Color.Black,

    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkGold,
    onPrimary = OnDarkPrimary,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,

    secondary = SecondaryDark,
    onSecondary = Charcoal,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,

    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,

    background = DarkBackground,
    onBackground = OnBackgroundDark,
    surface = DarkSurface,
    onSurface = OnBackgroundDark,

    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark,
    outlineVariant = OutlineVariantDark,

    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,

    inverseSurface = InverseSurfaceDark,
    inverseOnSurface = InverseOnSurfaceDark,
    inversePrimary = InversePrimaryDark,
    scrim = Color.Black,

    surfaceDim = SurfaceDimDark,
    surfaceBright = SurfaceBrightDark,
    surfaceContainerLowest = SurfaceContainerLowestDark,
    surfaceContainerLow = SurfaceContainerLowDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    surfaceContainerHighest = SurfaceContainerHighestDark
)

@Composable
fun AuValTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
