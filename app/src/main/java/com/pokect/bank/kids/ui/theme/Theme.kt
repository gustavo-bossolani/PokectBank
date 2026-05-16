package com.pokect.bank.kids.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
// ============================================================================
// PokectBankTheme — Single visual entry point (D-07)
// Light theme only. No dark theme for v1.
// ============================================================================
@Composable
fun PokectBankTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PokectBankLightColorScheme,
        typography = PokectBankTypography,
        shapes = KidShapes,
        content = content,
    )
}

// ============================================================================
// Animation contract — THEME-03
// These specs are documentation constants applied in later plans.
// Implementation happens in plan 01-02 (LoginScreen, AvatarDisplay).
// ============================================================================

/**
 * Animation specs used throughout the app. Six animation types defined here
 * for consistency. Implemented in composables across plan 01-02.
 *
 * Usage (conceptual — actual implementation uses Compose animation APIs):
 *
 * // float — Floating emoji background (login screen)
 * val infiniteTransition = rememberInfiniteTransition()
 * val floatY by infiniteTransition.animateFloat(
 *     initialValue = 0f,
 *     targetValue = -8.dp.toPx(),
 *     animationSpec = infiniteRepeatable(
 *         animation = tween(4000, easing = LinearEasing)
 *     )
 * )
 *
 * // pulse-glow — Sparkle scale effect
 * val pulseScale by infiniteTransition.animateFloat(
 *     initialValue = 1f,
 *     targetValue = 1.3f,
 *     animationSpec = infiniteRepeatable(
 *         animation = tween(1500),
 *         repeatMode = RepeatMode.Reverse
 *     )
 * )
 *
 * // bounce-in — Spring for piggy bank, button press, avatar
 * val bounceScale by animateFloatAsState(
 *     targetValue = if (isPressed) 0.95f else 1f,
 *     animationSpec = spring(
 *         dampingRatio = Spring.DampingRatioLowBouncy,
 *         stiffness = 300f
 *     )
 * )
 *
 * // coin-spin — Loading spinner rotation
 * val rotation by infiniteTransition.animateFloat(
 *     initialValue = 0f,
 *     targetValue = 360f,
 *     animationSpec = infiniteRepeatable(
 *         animation = tween(1000, easing = LinearEasing)
 *     )
 * )
 *
 * // wiggle — Decorative character oscillation
 * val wiggleX by infiniteTransition.animateFloat(
 *     initialValue = -4f,
 *     targetValue = 4f,
 *     animationSpec = infiniteRepeatable(
 *         animation = tween(600, easing = FastOutSlowInEasing),
 *         repeatMode = RepeatMode.Reverse
 *     )
 * )
 *
 * // confetti — Staggered particle launch (Phase 2)
 * // const val CONFETTI_LAUNCH_DURATION = 2000
 * // const val CONFETTI_PARTICLE_COUNT = 30
 * // Animatable scale/fade per particle with staggered start delays
 */
object AnimationSpecs {
    /** Floating emoji y-offset: 0f → -8dp over 4s, linear repeat */
    val float = tween<Float>(
        durationMillis = 4000,
        easing = LinearEasing,
    )

    /** Sparkle pulse: scale 1f → 1.3f over 1.5s */
    val pulseGlow = tween<Float>(
        durationMillis = 1500,
    )

    /** Bounce-in spring: DampingRatioLowBouncy, stiffness=300f */
    val bounceIn = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = 300f,
    )

    /** Coin spinner: full rotation over 1s */
    val coinSpin = tween<Float>(
        durationMillis = 1000,
        easing = LinearEasing,
    )

    /** Wiggle oscillation: ±4px, ±5° over 600ms */
    val wiggle = tween<Float>(
        durationMillis = 600,
        easing = FastOutSlowInEasing,
    )

    /** Screen transition: 300ms slide+fade */
    val screenTransition = tween<Float>(
        durationMillis = 300,
    )

    /** Confetti particle duration (Phase 2 integration) */
    const val CONFETTI_DURATION = 2000
    const val CONFETTI_PARTICLE_COUNT = 30
}
