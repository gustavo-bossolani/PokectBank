package com.pokect.bank.kids.ui.theme

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset

// ============================================================================
// Primary palette
// ============================================================================
val PokectBankPrimary = Color(0xFF6366F1)          // Indigo 500 — Primary CTA
val PokectBankOnPrimary = Color(0xFFFFFFFF)
val PokectBankPrimaryContainer = Color(0xFFE0E7FF)
val PokectBankOnPrimaryContainer = Color(0xFF312E81)

val PokectBankSecondary = Color(0xFFEC4899)        // Pink 500 — Decorative
val PokectBankOnSecondary = Color(0xFFFFFFFF)
val PokectBankSecondaryContainer = Color(0xFFFCE7F3)
val PokectBankOnSecondaryContainer = Color(0xFF831843)

val PokectBankTertiary = Color(0xFFFBBF24)         // Amber 400 — Accent
val PokectBankOnTertiary = Color(0xFF4A3000)
val PokectBankTertiaryContainer = Color(0xFFFEF3C7)
val PokectBankOnTertiaryContainer = Color(0xFF78350F)

// ============================================================================
// Semantic colors
// ============================================================================
val PokectBankSuccess = Color(0xFF10B981)           // Emerald 500
val PokectBankOnSuccess = Color(0xFFFFFFFF)
val PokectBankWarning = Color(0xFFF97316)           // Orange 500
val PokectBankOnWarning = Color(0xFFFFFFFF)
val PokectBankError = Color(0xFFEF4444)             // Red 500
val PokectBankOnError = Color(0xFFFFFFFF)
val PokectBankErrorContainer = Color(0xFFFEE2E2)
val PokectBankOnErrorContainer = Color(0xFF7F1D1D)

// ============================================================================
// Neutral palette
// ============================================================================
val PokectBankBackground = Color(0xFFFAFAFA)
val PokectBankOnBackground = Color(0xFF1A1A2E)
val PokectBankSurface = Color(0xFFFFFFFF)
val PokectBankOnSurface = Color(0xFF1A1A2E)
val PokectBankSurfaceVariant = Color(0xFFF0F0F5)
val PokectBankOnSurfaceVariant = Color(0xFF6B7280)
val PokectBankOutline = Color(0xFFD1D5DB)
val PokectBankOutlineVariant = Color(0xFFE5E7EB)
val PokectBankMuted = Color(0xFFF0F0F5)
val PokectBankMutedForeground = Color(0xFF6B7280)

// ============================================================================
// Material 3 Light Color Scheme
// ============================================================================
val PokectBankLightColorScheme = lightColorScheme(
    primary = PokectBankPrimary,
    onPrimary = PokectBankOnPrimary,
    primaryContainer = PokectBankPrimaryContainer,
    onPrimaryContainer = PokectBankOnPrimaryContainer,
    secondary = PokectBankSecondary,
    onSecondary = PokectBankOnSecondary,
    secondaryContainer = PokectBankSecondaryContainer,
    onSecondaryContainer = PokectBankOnSecondaryContainer,
    tertiary = PokectBankTertiary,
    onTertiary = PokectBankOnTertiary,
    tertiaryContainer = PokectBankTertiaryContainer,
    onTertiaryContainer = PokectBankOnTertiaryContainer,
    background = PokectBankBackground,
    onBackground = PokectBankOnBackground,
    surface = PokectBankSurface,
    onSurface = PokectBankOnSurface,
    surfaceVariant = PokectBankSurfaceVariant,
    onSurfaceVariant = PokectBankOnSurfaceVariant,
    outline = PokectBankOutline,
    outlineVariant = PokectBankOutlineVariant,
    error = PokectBankError,
    onError = PokectBankOnError,
    errorContainer = PokectBankErrorContainer,
    onErrorContainer = PokectBankOnErrorContainer,
)

// ============================================================================
// Gradient Presets (all 135° top-left to bottom-right)
// ============================================================================
object GradientPresets {
    val gradientPrimary: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF6366F1), Color(0xFF818CF8)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )

    val gradientSecondary: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFFEC4899), Color(0xFFF472B6)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )

    val gradientSuccess: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFF10B981), Color(0xFF34D399)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )

    val gradientWarning: Brush = Brush.linearGradient(
        colors = listOf(Color(0xFFFBBF24), Color(0xFFF97316)),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )

    val gradientRainbow: Brush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF6366F1),
            Color(0xFFEC4899),
            Color(0xFFFBBF24),
            Color(0xFF10B981),
            Color(0xFF6366F1),
        ),
        start = Offset.Zero,
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
    )
}
