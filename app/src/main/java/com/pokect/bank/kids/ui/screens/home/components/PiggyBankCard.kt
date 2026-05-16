package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.util.formatCurrency
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun PiggyBankCard(
    balance: Double,
    goal: Double,
    goalProgressPercent: Float,
    depositAmount: Double,
    onDepositClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                shape = KidShapes.large
            )
            .clip(KidShapes.large)
            .background(GradientPresets.gradientSecondary),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
    ) {
        Box {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Card title
                Text(
                    text = "Meu Cofrinho",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Balance display
                Text(
                    text = formatCurrency(balance),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    ),
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Goal progress section
                GoalProgressSection(
                    goal = goal,
                    goalProgressPercent = goalProgressPercent
                )

                Spacer(modifier = Modifier.height(20.dp))

                // CTA Button
                DepositCtaButton(
                    depositAmount = depositAmount,
                    onDepositClick = onDepositClick
                )
            }

            // Floating coin emojis around card edges
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp)
            ) {
                FloatingCoins()
            }
        }
    }
}

// ============================================================================
// Goal Progress Section with custom progress bar and shine animation
// ============================================================================
@Composable
private fun GoalProgressSection(
    goal: Double,
    goalProgressPercent: Float
) {
    // Progress bar label
    Text(
        text = "Progresso da Meta",
        style = MaterialTheme.typography.bodyMedium.copy(
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.8f)
        ),
    )

    Spacer(modifier = Modifier.height(8.dp))

    // Custom Box-based progress bar with shine sweep
    ShinyProgressBar(
        progress = goalProgressPercent / 100f,
        gradient = GradientPresets.gradientSuccess,
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
    )

    Spacer(modifier = Modifier.height(4.dp))

    // Percentage text (right-aligned) and meta label
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Meta: ${formatCurrency(goal)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.7f)
            ),
        )
        Text(
            text = "${goalProgressPercent.toInt()}%",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.9f)
            ),
        )
    }
}

// ============================================================================
// Shiny Progress Bar — custom Box-based bar with gradient fill + shine sweep
// ============================================================================
@Composable
private fun ShinyProgressBar(
    progress: Float,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shineOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shineOffset"
    )

    Box(
        modifier = modifier
            .clip(KidShapes.small)
            .background(Color.White.copy(alpha = 0.2f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(KidShapes.small)
                .background(gradient)
        ) {
            // Shine overlay — translucent white gradient sweep
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = shineOffset * size.width
                    }
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.4f),
                                Color.Transparent,
                            ),
                            startX = 0f,
                            endX = 100f,
                        )
                    )
            )
        }
    }
}

// ============================================================================
// Floating Coin Emojis — 3 coins with staggered floating animation
// ============================================================================
@Composable
private fun FloatingCoins() {
    // Coin positions relative to card edges (positioned near right side)
    val coinConfigs = listOf(
        CoinConfig(offsetX = 0.dp, offsetY = 30.dp, delayMs = 0L),
        CoinConfig(offsetX = 16.dp, offsetY = 80.dp, delayMs = 600L),
        CoinConfig(offsetX = (-4).dp, offsetY = 130.dp, delayMs = 1200L),
    )

    coinConfigs.forEach { config ->
        FloatingCoin(
            offsetX = config.offsetX,
            offsetY = config.offsetY,
            delayMs = config.delayMs
        )
    }
}

private data class CoinConfig(
    val offsetX: androidx.compose.ui.unit.Dp,
    val offsetY: androidx.compose.ui.unit.Dp,
    val delayMs: Long
)

@Composable
private fun FloatingCoin(
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    delayMs: Long
) {
    val floatTransition = rememberInfiniteTransition(label = "floatCoin")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val rotation by floatTransition.animateFloat(
        initialValue = -15f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatRotation"
    )

    Text(
        text = "\uD83E\uDE99", // 🪙
        fontSize = 20.sp,
        modifier = Modifier
            .offset(
                x = offsetX,
                y = offsetY
            )
            .graphicsLayer {
                translationY = floatY
                rotationZ = rotation
            }
            .semantics { contentDescription = "" }, // decorative
    )
}

// ============================================================================
// Deposit CTA Button — dual-state: enabled (gradient + pulse) / disabled (gray)
// ============================================================================
@Composable
private fun DepositCtaButton(
    depositAmount: Double,
    onDepositClick: () -> Unit
) {
    val isEnabled = depositAmount > 0

    // Spring scale animation for press feedback (LoginButton pattern)
    var isPressed by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = 300f
        ),
        label = "depositButtonScale"
    )

    // Reset pressed state after animation completes
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }

    // Pulse glow animation — rememberInfiniteTransition must be called unconditionally
    val pulseTransition = rememberInfiniteTransition(label = "pulseGlow")
    val pulseAnim by pulseTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = AnimationSpecs.pulseGlow.durationMillis,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )
    val pulseAlpha = if (isEnabled) pulseAnim else 0f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (isEnabled) 8.dp else 0.dp,
                shape = KidShapes.medium
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = 8f + pulseAlpha * 8f
            }
            .clip(KidShapes.medium)
            .then(
                if (isEnabled) Modifier.background(GradientPresets.gradientPrimary)
                else Modifier.background(Color.White.copy(alpha = 0.4f))
            )
            .clickable(
                enabled = isEnabled,
                onClick = {
                    isPressed = true
                    onDepositClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Guardar Moedinha",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = if (isEnabled) Color.White else Color.White.copy(alpha = 0.6f)
            ),
        )
    }
}
