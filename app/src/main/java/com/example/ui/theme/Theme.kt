package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val HijrahLightColorScheme = lightColorScheme(
    primary = HijrahBrown,
    onPrimary = HijrahWhite,
    primaryContainer = HijrahDarkBrown,
    onPrimaryContainer = HijrahWhite,
    secondary = HijrahRust,
    onSecondary = HijrahWhite,
    secondaryContainer = HijrahRustTint,
    onSecondaryContainer = HijrahDarkBrown,
    tertiary = HijrahGold,
    onTertiary = HijrahDarkBrown,
    tertiaryContainer = HijrahGoldTint,
    onTertiaryContainer = HijrahDarkBrown,
    background = HijrahWhite,
    onBackground = HijrahDarkBrown,
    surface = HijrahWhite,
    onSurface = HijrahDarkBrown,
    surfaceVariant = HijrahSurfaceTinted,
    onSurfaceVariant = HijrahTextSecondary,
    outline = HijrahBorderSubtle,
    outlineVariant = HijrahSurfaceSoftBrown,
    error = HijrahErrorRust,
    onError = HijrahWhite
)

@Composable
fun HijrahNetwerkTheme(
    content: @Composable () -> Unit
) {
    // Dynamic color bewust uitgeschakeld zodat de strikte Hijrah Netwerk-huisstijl
    // (#FFFFFF, #684A25, #4F3A23, #DF842C, #C9A227) altijd behouden blijft.
    MaterialTheme(
        colorScheme = HijrahLightColorScheme,
        typography = HijrahTypography,
        shapes = HijrahShapes,
        content = content
    )
}
