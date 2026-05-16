package com.pokect.bank.kids.ui.screens.missions.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.R
import com.pokect.bank.kids.data.models.Mission
import com.pokect.bank.kids.data.models.MissionDifficulty
import com.pokect.bank.kids.data.models.MissionStatus
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankError
import com.pokect.bank.kids.ui.theme.PokectBankMuted
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground
import com.pokect.bank.kids.ui.theme.PokectBankOutlineVariant
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant
import com.pokect.bank.kids.ui.theme.PokectBankWarning

@Composable
fun MissionCard(
    mission: Mission,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
        border = if (mission.status == MissionStatus.COMPLETED) {
            BorderStroke(2.dp, PokectBankSuccess)
        } else {
            BorderStroke(1.dp, PokectBankOutlineVariant)
        }
    ) {
        Box {
            // Card content with optional dimming for locked state
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .then(
                        if (mission.status == MissionStatus.LOCKED) {
                            Modifier.alpha(0.6f)
                        } else {
                            Modifier
                        }
                    )
            ) {
                // Icon row: [emoji icon] Title [status badge]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icon container
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(KidShapes.medium)
                            .background(GradientPresets.gradientPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mission.icon,
                            fontSize = 20.sp
                        )
                    }

                    // Title
                    Text(
                        text = mission.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PokectBankOnBackground
                        ),
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .weight(1f)
                    )

                    // Status badge area (top-right)
                    StatusBadge(mission = mission)
                }

                // Description
                Text(
                    text = mission.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = PokectBankMutedForeground
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Difficulty chip
                DifficultyChip(
                    difficulty = mission.difficulty,
                    modifier = Modifier.padding(top = 8.dp)
                )

                // Progress bar (IN_PROGRESS only)
                if (mission.status == MissionStatus.IN_PROGRESS) {
                    val progressFraction = if (mission.maxProgress > 0) {
                        mission.progress.toFloat() / mission.maxProgress.toFloat()
                    } else 0f

                    Text(
                        text = stringResource(
                            R.string.missions_progress_label,
                            mission.progress,
                            mission.maxProgress
                        ),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = PokectBankMutedForeground
                        ),
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Gradient progress bar using Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp)
                            .padding(top = 8.dp)
                            .clip(KidShapes.extraSmall)
                            .background(PokectBankSurfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progressFraction.coerceIn(0f, 1f))
                                .fillMaxHeight()
                                .clip(KidShapes.extraSmall)
                                .background(GradientPresets.gradientPrimary)
                        )
                    }
                }

                // Rewards row
                Row(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.missions_reward_xp, mission.xp),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = stringResource(R.string.missions_reward_coins, mission.coins),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            // Lock overlay (LOCKED only)
            if (mission.status == MissionStatus.LOCKED) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(PokectBankMuted.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🔒", fontSize = 32.sp)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(mission: Mission) {
    when (mission.status) {
        MissionStatus.IN_PROGRESS -> {
            // 🔥 emoji with pulse animation
            val infiniteTransition = rememberInfiniteTransition(label = "fire-pulse")
            val scale by infiniteTransition.animateFloat(
                initialValue = 1.0f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1500),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "fire-scale"
            )
            Text(
                text = "🔥",
                fontSize = 20.sp,
                modifier = Modifier
                    .graphicsLayer { scaleX = scale; scaleY = scale }
            )
        }
        MissionStatus.COMPLETED -> {
            // Check badge with bounceIn animation
            AnimatedVisibility(
                visible = true,
                enter = scaleIn(
                    initialScale = 0.5f,
                    animationSpec = AnimationSpecs.bounceIn
                ) + fadeIn(animationSpec = AnimationSpecs.bounceIn)
            ) {
                Text("✅", fontSize = 24.sp)
            }
        }
        else -> {
            // No badge for AVAILABLE or LOCKED
        }
    }
}

@Composable
private fun DifficultyChip(
    difficulty: MissionDifficulty,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, labelRes) = when (difficulty) {
        MissionDifficulty.EASY -> Triple(
            PokectBankSuccess.copy(alpha = 0.2f),
            PokectBankSuccess,
            R.string.missions_difficulty_easy
        )
        MissionDifficulty.MEDIUM -> Triple(
            PokectBankWarning.copy(alpha = 0.2f),
            PokectBankWarning,
            R.string.missions_difficulty_medium
        )
        MissionDifficulty.HARD -> Triple(
            PokectBankError.copy(alpha = 0.2f),
            PokectBankError,
            R.string.missions_difficulty_hard
        )
    }

    Box(
        modifier = modifier
            .clip(KidShapes.extraLarge)
            .background(bgColor)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelLarge.copy(
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}
