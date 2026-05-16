package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteTransition
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.ui.components.AvatarDisplay
import com.pokect.bank.kids.ui.components.AvatarSize
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnSurface
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant
import kotlin.math.roundToInt

@Composable
fun LevelCard(
    level: Int,
    levelTitle: String,
    currentXp: Int,
    xpToNextLevel: Int,
    xpProgressPercent: Float,
    totalXp: Int,
    streak: Int,
    badges: List<String>,
    selectedAvatar: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarDisplay(
                    level = level,
                    size = AvatarSize.LG,
                    showLevel = true
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Nível $level",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PokectBankOnSurface
                        )
                    )
                    Text(
                        text = levelTitle,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = PokectBankMutedForeground
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "XP Progresso",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = PokectBankMutedForeground
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShinyProgressBar(
                progress = xpProgressPercent / 100f,
                gradient = GradientPresets.gradientPrimary,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$currentXp / $xpToNextLevel XP",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankMutedForeground
                    )
                )
                Text(
                    text = "${xpProgressPercent.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn(
                    emoji = "⭐",
                    value = totalXp.toString(),
                    label = "XP Total",
                    modifier = Modifier.weight(1f)
                )
                StatColumn(
                    emoji = "🔥",
                    value = streak.toString(),
                    label = "Dias seguidos",
                    modifier = Modifier.weight(1f),
                    pulseEnabled = streak >= 7
                )
                StatColumn(
                    emoji = "🏆",
                    value = badges.size.toString(),
                    label = "Conquistas",
                    modifier = Modifier.weight(1f)
                )
            }

            if (badges.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Conquistas Recentes",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankMutedForeground
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                val visibleBadges = if (badges.size > 6) badges.take(6) else badges
                val overflowCount = badges.size - 6

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    visibleBadges.forEachIndexed { index, badge ->
                        BadgeChip(
                            emoji = badge,
                            index = index
                        )
                    }
                    if (overflowCount > 0) {
                        OverflowChip(count = overflowCount)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatColumn(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    pulseEnabled: Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        if (pulseEnabled) {
            PulsingEmoji(emoji = emoji)
        } else {
            Text(
                text = emoji,
                fontSize = 20.sp
            )
        }
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = PokectBankOnSurface
            )
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Normal,
                color = PokectBankMutedForeground
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PulsingEmoji(emoji: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "streakPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "streakPulseScale"
    )
    Text(
        text = emoji,
        fontSize = 20.sp,
        modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale)
    )
}

@Composable
private fun BadgeChip(
    emoji: String,
    index: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(index) {
        kotlinx.coroutines.delay(index * 100L)
        isVisible = true
    }
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (isVisible) 1f else 0f,
        label = "badgeEntrance"
    )
    var pressScale by remember { mutableFloatStateOf(1f) }

    // Reset pressed state after animation
    androidx.compose.runtime.LaunchedEffect(pressScale) {
        if (pressScale > 1f) {
            kotlinx.coroutines.delay(150)
            pressScale = 1f
        }
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .graphicsLayer(
                scaleX = scale * pressScale,
                scaleY = scale * pressScale
            )
            .clip(KidShapes.small)
            .background(PokectBankSurfaceVariant)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    pressScale = 1.2f
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = emoji,
            fontSize = 20.sp,
            modifier = Modifier.graphicsLayer(alpha = scale)
        )
    }
}

@Composable
private fun OverflowChip(count: Int) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(KidShapes.small)
            .background(PokectBankPrimary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        )
    }
}

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
            .background(PokectBankSurfaceVariant.copy(alpha = 0.5f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(KidShapes.small)
                .background(gradient)
        ) {
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
