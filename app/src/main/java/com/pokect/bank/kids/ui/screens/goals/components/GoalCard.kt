package com.pokect.bank.kids.ui.screens.goals.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.data.models.GoalColor
import com.pokect.bank.kids.data.models.SavingsGoal
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSecondary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant
import com.pokect.bank.kids.ui.theme.PokectBankTertiary
import com.pokect.bank.kids.util.formatCurrency

@Composable
fun GoalCard(
    goal: SavingsGoal,
    modifier: Modifier = Modifier
) {
    val progressPercent = if (goal.targetAmount > 0) {
        (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0)
    } else {
        0.0
    }
    val isCompleted = progressPercent >= 1.0

    val gradient = when (goal.color) {
        GoalColor.PRIMARY -> GradientPresets.gradientPrimary
        GoalColor.SECONDARY -> GradientPresets.gradientSecondary
        GoalColor.SUCCESS -> GradientPresets.gradientSuccess
        GoalColor.WARNING -> GradientPresets.gradientWarning
    }

    val decorativeColor = when (goal.color) {
        GoalColor.PRIMARY -> PokectBankPrimary
        GoalColor.SECONDARY -> PokectBankSecondary
        GoalColor.SUCCESS -> PokectBankSuccess
        GoalColor.WARNING -> PokectBankTertiary
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface)
    ) {
        Box(modifier = Modifier.clip(KidShapes.large)) {
            // Decorative circle (D-16)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(decorativeColor.copy(alpha = 0.2f))
            )

            // Card content
            Column(modifier = Modifier.padding(20.dp)) {
                // Icon row: [emoji icon] Title [celebration]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(KidShapes.medium)
                            .background(gradient),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = goal.icon,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Title
                    Text(
                        text = goal.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = PokectBankOnBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 8.dp)
                    )

                    // Celebration area (top-right)
                    if (isCompleted) {
                        CelebrationEmoji()
                    }
                }

                // Days remaining
                Text(
                    text = "📅 ${goal.daysLeft} dias restantes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PokectBankMutedForeground,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Spacer
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(16.dp))

                // Current amount (left) / Target amount (right)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatCurrency(goal.currentAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PokectBankOnBackground,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatCurrency(goal.targetAmount),
                        style = MaterialTheme.typography.bodyMedium,
                        color = PokectBankMutedForeground
                    )
                }

                // Spacer
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(8.dp))

                // Progress bar section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Progress bar with shine
                    ProgressBarWithShine(
                        progress = progressPercent.toFloat(),
                        gradient = gradient,
                        modifier = Modifier.weight(1f)
                    )

                    // Percentage text
                    Text(
                        text = "${(progressPercent * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PokectBankMutedForeground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Target emoji centered below progress bar
                Text(
                    text = goal.icon,
                    fontSize = 24.sp,
                    color = PokectBankMutedForeground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                // Completed celebration text
                if (isCompleted) {
                    Text(
                        text = "Meta alcançada!",
                        style = MaterialTheme.typography.labelLarge,
                        color = PokectBankSuccess,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgressBarWithShine(
    progress: Float,
    gradient: Brush,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shine")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
        ),
        label = "shimmer"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(PokectBankSurfaceVariant.copy(alpha = 0.5f))
    ) {
        // Gradient fill with shine overlay
        Box(
            modifier = Modifier
                .fillMaxWidth(progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(gradient)
                .drawBehind {
                    val shimmerWidth = size.width * 0.3f
                    val x = shimmerOffset * (size.width + shimmerWidth) - shimmerWidth
                    drawRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.White.copy(alpha = 0.3f),
                                Color.Transparent
                            ),
                            startX = x,
                            endX = x + shimmerWidth
                        ),
                        size = size
                    )
                }
        )
    }
}

@Composable
private fun CelebrationEmoji() {
    val infiniteTransition = rememberInfiniteTransition(label = "celebration")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Text(
        text = "🎉",
        fontSize = 20.sp,
        modifier = Modifier.graphicsLayer {
            rotationZ = rotation
            scaleX = scale
            scaleY = scale
        }
    )
}
