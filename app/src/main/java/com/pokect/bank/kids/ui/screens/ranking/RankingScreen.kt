package com.pokect.bank.kids.ui.screens.ranking

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokect.bank.kids.data.models.FamilyMember
import com.pokect.bank.kids.data.models.WeeklyChallenge
import com.pokect.bank.kids.ui.components.AvatarDisplay
import com.pokect.bank.kids.ui.components.AvatarSize
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMuted
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnSurface
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankWarning
import java.time.Duration
import java.time.Instant

@Composable
fun RankingScreen(
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            item {
                Text(
                    text = "🏆 Ranking da Família",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = PokectBankOnSurface
                )
            }

            if (uiState.members.isNotEmpty()) {
                item {
                    PodiumSection(top3 = uiState.members.take(3))
                }
            }

            items(uiState.members.drop(3)) { member ->
                RankingListItem(member = member)
            }

            item {
                uiState.weeklyChallenge?.let { challenge ->
                    WeeklyChallengeCard(
                        challenge = challenge,
                        onJoin = { viewModel.joinWeeklyChallenge(challenge.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PodiumSection(
    top3: List<RankingMember>,
    modifier: Modifier = Modifier
) {
    if (top3.size < 3) return

    val first = top3[0]
    val second = top3[1]
    val third = top3[2]

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom,
        modifier = modifier.fillMaxWidth()
    ) {
        PodiumColumn(member = second.member, position = 2, height = 120.dp, delayMs = 100)
        PodiumColumn(member = first.member, position = 1, height = 160.dp, delayMs = 0, isFirst = true)
        PodiumColumn(member = third.member, position = 3, height = 100.dp, delayMs = 200)
    }
}

@Composable
private fun PodiumColumn(
    member: FamilyMember,
    position: Int,
    height: androidx.compose.ui.unit.Dp,
    delayMs: Int,
    isFirst: Boolean = false,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMs.toLong())
        visible = true
    }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = AnimationSpecs.bounceIn,
        label = "podiumBounceIn"
    )

    val background = when (position) {
        1 -> GradientPresets.gradientWarning
        2 -> Brush.linearGradient(listOf(PokectBankMuted, PokectBankMuted))
        3 -> Brush.linearGradient(listOf(PokectBankWarning.copy(alpha = 0.2f), PokectBankWarning.copy(alpha = 0.2f)))
        else -> Brush.linearGradient(listOf(PokectBankMuted, PokectBankMuted))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .scale(scale)
            .width(100.dp)
            .height(height)
            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            .background(background)
            .padding(8.dp)
    ) {
        if (isFirst) {
            FloatingCrown()
        }

        AvatarDisplay(
            level = member.level,
            size = AvatarSize.MD,
            showLevel = false
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "${position}°",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (position == 1) Color.White else PokectBankOnSurface
        )

        Text(
            text = member.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (position == 1) Color.White else PokectBankOnSurface,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun FloatingCrown(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "crownFloat")
    val translateY by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "crownTranslateY"
    )

    Text(
        text = "👑",
        fontSize = 24.sp,
        modifier = modifier.graphicsLayer { translationY = translateY }
    )
}

@Composable
private fun RankingListItem(
    member: RankingMember,
    modifier: Modifier = Modifier
) {
    val borderColor = if (member.isCurrentUser) PokectBankPrimary else Color.Transparent
    val backgroundColor = if (member.isCurrentUser) PokectBankPrimary.copy(alpha = 0.1f) else PokectBankSurface

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = Brush.linearGradient(listOf(borderColor, borderColor))
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "${member.position}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PokectBankMutedForeground,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )

            AvatarDisplay(
                level = member.member.level,
                size = AvatarSize.SM,
                showLevel = false
            )

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(
                        text = member.member.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokectBankOnSurface
                    )
                    if (member.isCurrentUser) {
                        Text(
                            text = " (você)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = PokectBankPrimary
                        )
                    }
                }

                Text(
                    text = "⭐ ${member.member.xp} XP  🔥 ${member.member.streak} dias",
                    fontSize = 12.sp,
                    color = PokectBankMutedForeground
                )
            }

            Text(
                text = "🪙 ${member.member.coins}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = PokectBankOnSurface
            )
        }
    }
}

@Composable
private fun WeeklyChallengeCard(
    challenge: WeeklyChallenge,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf(Instant.now()) }

    LaunchedEffect(challenge.endDate) {
        while (true) {
            kotlinx.coroutines.delay(60_000)
            currentTime = Instant.now()
        }
    }

    val timeLeft = Duration.between(currentTime, challenge.endDate)
    val days = timeLeft.toDays()
    val hours = timeLeft.toHours() % 24

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(GradientPresets.gradientPrimary)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = challenge.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PokectBankOnSurface
            )

            Text(
                text = challenge.description,
                fontSize = 14.sp,
                color = PokectBankMutedForeground,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "⭐ ${challenge.rewardXp} XP  🪙 ${challenge.rewardCoins} moedas",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PokectBankPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = "$days dias $hours horas",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = PokectBankPrimary,
                modifier = Modifier.padding(top = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (challenge.hasJoined) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(KidShapes.medium)
                        .background(PokectBankSuccess.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓ Participando",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PokectBankSuccess
                    )
                }
            } else {
                PressableCtaButton(
                    text = "Participar",
                    onClick = onJoin
                )
            }
        }
    }
}

@Composable
private fun PressableCtaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(scale) {
        if (scale < 1f) {
            kotlinx.coroutines.delay(150)
            scale = 1f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .scale(scale)
            .clip(KidShapes.medium)
            .background(GradientPresets.gradientPrimary)
            .clickable(onClick = {
                scale = 0.95f
                onClick()
            }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
