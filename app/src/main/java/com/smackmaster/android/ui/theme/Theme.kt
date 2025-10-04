package com.smackmaster.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.smackmaster.android.config.ThemeConfig

private val PremiumLightColorScheme = lightColorScheme(
    primary = PremiumNeonYellow,
    onPrimary = PremiumDeepBlack,
    secondary = PremiumBlueOutline,
    tertiary = PremiumNeonGreen,
    background = PremiumDeepBlack,
    surface = PremiumDeepBlack.copy(alpha = 0.9f),
    onSurface = PremiumTextWhite,
    surfaceVariant = PremiumGlassTint,
    onSurfaceVariant = PremiumSubtextGray,
    error = PremiumCrimsonRed,
)

private val PremiumDarkColorScheme = darkColorScheme(
    primary = PremiumNeonYellow,
    onPrimary = PremiumDeepBlack,
    secondary = PremiumBlueOutline,
    tertiary = PremiumNeonGreen,
    background = PremiumDeepBlack,
    surface = PremiumDeepBlack.copy(alpha = 0.92f),
    onSurface = PremiumTextWhite,
    surfaceVariant = PremiumGlassTint,
    onSurfaceVariant = PremiumSubtextGray,
    error = PremiumCrimsonRed,
)

val MaterialTheme.glassSurfaceBrush: Brush
    @Composable
    get() = remember(colorScheme.surface, colorScheme.surfaceVariant) {
        Brush.verticalGradient(
            colors = listOf(
                colorScheme.surfaceVariant.copy(alpha = 0.75f),
                colorScheme.surfaceVariant.copy(alpha = 0.45f)
            )
        )
    }

val MaterialTheme.neonGlow: Color
    @Composable
    get() = PremiumGlow

@Composable
fun SmackMasterTheme(
    premiumOverride: Boolean? = null,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val enablePremium = premiumOverride ?: ThemeConfig.THEME_PREMIUM
    val colorScheme: ColorScheme = if (enablePremium) {
        if (useDarkTheme) PremiumDarkColorScheme else PremiumLightColorScheme
    } else {
        if (useDarkTheme) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PremiumTypography,
        shapes = PremiumShapes,
        content = content
    )
}
