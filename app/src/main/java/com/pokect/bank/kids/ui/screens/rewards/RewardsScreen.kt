package com.pokect.bank.kids.ui.screens.rewards

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.pokect.bank.kids.data.models.Reward
import com.pokect.bank.kids.ui.screens.home.components.ConfettiOverlay
import com.pokect.bank.kids.ui.theme.AnimationSpecs
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMuted
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnSurface
import com.pokect.bank.kids.ui.theme.PokectBankOutlineVariant
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RewardsScreen(
    modifier: Modifier = Modifier,
    viewModel: RewardsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item(span = { GridItemSpan(2) }) {
                    CoinBalanceHeader(coins = uiState.coins)
                }
                items(uiState.rewards, key = { it.reward.id }) { rewardUi ->
                    RewardCard(
                        rewardUi = rewardUi,
                        userCoins = uiState.coins,
                        onRedeem = { viewModel.redeemReward(rewardUi.reward.id) }
                    )
                }
            }

            if (uiState.showRedemptionModal) {
                uiState.selectedReward?.let { reward ->
                    RedemptionModal(
                        reward = reward,
                        onConfirm = {
                            scope.launch {
                                viewModel.confirmRedemption()
                            }
                        },
                        onDismiss = { viewModel.dismissRedemptionModal() }
                    )
                }
            }

            ConfettiOverlay(
                isActive = uiState.showConfetti,
                onFinished = { viewModel.dismissConfetti() }
            )
        }
    }
}

@Composable
private fun CoinBalanceHeader(
    coins: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GradientPresets.gradientWarning)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "🪙",
                    fontSize = 32.sp,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = "$coins moedas",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Seu saldo disponível",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.White.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun RewardCard(
    rewardUi: RewardUi,
    userCoins: Int,
    onRedeem: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reward = rewardUi.reward
    val canAfford = rewardUi.canAfford
    val isRedeemed = reward.redeemed

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = if (canAfford && !isRedeemed) GradientPresets.gradientSuccess else Brush.linearGradient(listOf(PokectBankOutlineVariant, PokectBankOutlineVariant))
        )
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            Column {
                if (reward.isNew && !isRedeemed) {
                    NovoBadge(modifier = Modifier.align(Alignment.End))
                }

                RewardIconContainer(
                    icon = reward.icon,
                    canAfford = canAfford,
                    isRedeemed = isRedeemed
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isRedeemed) PokectBankOnSurface.copy(alpha = 0.5f) else PokectBankOnSurface
                    ),
                    maxLines = 1
                )

                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = PokectBankMutedForeground
                    ),
                    maxLines = 2,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (!canAfford && !isRedeemed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(KidShapes.small)
                            .background(PokectBankMuted)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(rewardUi.progressPercent.coerceAtMost(1f))
                                .height(8.dp)
                                .clip(KidShapes.small)
                                .background(GradientPresets.gradientPrimary)
                        )
                    }
                    Text(
                        text = "Faltam 🪙 ${reward.cost - userCoins}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = PokectBankMutedForeground
                        ),
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                RedeemButton(
                    canAfford = canAfford,
                    isRedeemed = isRedeemed,
                    cost = reward.cost,
                    onRedeem = onRedeem
                )
            }
        }
    }
}

@Composable
private fun NovoBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "novoPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "novoBadgeScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clip(KidShapes.extraLarge)
            .background(GradientPresets.gradientSecondary)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = "NOVO",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

@Composable
private fun RewardIconContainer(
    icon: String,
    canAfford: Boolean,
    isRedeemed: Boolean
) {
    val alpha = if (isRedeemed) 0.5f else 1f

    Box(
        modifier = Modifier
            .size(80.dp)
            .graphicsLayer(alpha = alpha),
        contentAlignment = Alignment.Center
    ) {
        val background = if (canAfford && !isRedeemed) {
            GradientPresets.gradientWarning
        } else {
            Brush.linearGradient(listOf(PokectBankMuted, PokectBankMuted))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(KidShapes.large)
                .background(background),
            contentAlignment = Alignment.Center
        ) {
            if (canAfford && !isRedeemed) {
                val infiniteTransition = rememberInfiniteTransition(label = "wiggle")
                val rotation by infiniteTransition.animateFloat(
                    initialValue = -3f,
                    targetValue = 3f,
                    animationSpec = infiniteRepeatable(
                        animation = AnimationSpecs.wiggle,
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "rewardWiggle"
                )
                Text(
                    text = icon,
                    fontSize = 32.sp,
                    modifier = Modifier.graphicsLayer(rotationZ = rotation)
                )
            } else {
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
            }
        }
    }
}

@Composable
private fun RedeemButton(
    canAfford: Boolean,
    isRedeemed: Boolean,
    cost: Int,
    onRedeem: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(scale) {
        if (scale < 1f) {
            delay(150)
            scale = 1f
        }
    }

    val backgroundBrush: Brush = when {
        isRedeemed -> Brush.linearGradient(listOf(PokectBankSuccess.copy(alpha = 0.2f), PokectBankSuccess.copy(alpha = 0.2f)))
        canAfford -> GradientPresets.gradientSuccess
        else -> Brush.linearGradient(listOf(PokectBankMuted, PokectBankMuted))
    }

    val textColor = when {
        isRedeemed -> PokectBankSuccess
        canAfford -> Color.White
        else -> PokectBankMutedForeground
    }

    val text = when {
        isRedeemed -> "✓ Resgatado"
        canAfford -> "Resgatar • 🪙 $cost"
        else -> "🪙 $cost"
    }

    val clickableModifier = if (canAfford && !isRedeemed) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = {
                scale = 0.95f
                onRedeem()
            }
        )
    } else {
        Modifier
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .scale(scale)
            .clip(KidShapes.medium)
            .background(backgroundBrush)
            .then(clickableModifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RedemptionModal(
    reward: Reward,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val dismissSheet: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = dismissSheet,
        sheetState = sheetState,
        containerColor = PokectBankSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                IconButton(
                    onClick = dismissSheet,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = PokectBankMutedForeground
                    )
                }
            }

            Text(
                text = reward.icon,
                fontSize = 48.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = reward.title,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = PokectBankOnSurface
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = reward.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = PokectBankMutedForeground
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            Text(
                text = "🪙 ${reward.cost}",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PokectBankSuccess
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Confirmar resgate?",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PokectBankOnSurface
                ),
                modifier = Modifier.padding(bottom = 24.dp)
            )

            var ctaScale by remember { mutableFloatStateOf(1f) }
            LaunchedEffect(ctaScale) {
                if (ctaScale < 1f) {
                    delay(150)
                    ctaScale = 1f
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .scale(ctaScale)
                    .clip(KidShapes.medium)
                    .background(GradientPresets.gradientSuccess)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            ctaScale = 0.95f
                            onConfirm()
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Confirmar",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Cancelar",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = PokectBankMutedForeground
                ),
                modifier = Modifier
                    .clickable(onClick = dismissSheet)
                    .padding(8.dp)
            )
        }
    }
}
