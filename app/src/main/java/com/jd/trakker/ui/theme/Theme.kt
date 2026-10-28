package com.jd.trakker.ui.theme

import android.os.Build
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.jd.trakker.R

enum class ThemeType {
    AUTO,
    DARK,
    LIGHT
}

@StringRes
fun ThemeType.toStringRes(): Int {
    return when(this) {
        ThemeType.AUTO  -> R.string.theme_auto
        ThemeType.DARK  -> R.string.theme_dark
        ThemeType.LIGHT -> R.string.theme_light
    }
}

@Composable
fun getPrimaryAndSecondaryColors(theme: ThemeType, dynColor: Boolean): Pair<Color, Color> {
    val context = LocalContext.current
    val useDynamicColors = dynColor && isDynamicColorAvailable()

    val light   = if(useDynamicColors) dynamicLightColorScheme(context) else lightScheme
    val dark    = if(useDynamicColors) dynamicDarkColorScheme(context) else darkScheme
    return when(theme) {
        ThemeType.AUTO -> Pair(light.primary, dark.primary)
        ThemeType.DARK -> Pair(dark.primary, dark.secondary)
        ThemeType.LIGHT -> Pair(light.primary, light.secondary)
    }
}

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

fun isDynamicColorAvailable(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
}

@Composable
fun TrakkerTheme(
    type: ThemeType = ThemeType.AUTO,
    useDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    // To satisfy IDE i cant call my function
    val safeUseDynamicColor = useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colorScheme = when(type) {
        ThemeType.AUTO -> when {
            safeUseDynamicColor -> if(isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            else                -> if(isSystemInDarkTheme()) darkScheme else lightScheme
        }
        ThemeType.DARK -> when {
            safeUseDynamicColor -> dynamicDarkColorScheme(context)
            else                -> darkScheme
        }
        ThemeType.LIGHT -> when {
            safeUseDynamicColor -> dynamicLightColorScheme(context)
            else                -> lightScheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme, typography = AppTypography, content = content
    )
}