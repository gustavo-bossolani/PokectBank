package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMuted
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground
import com.pokect.bank.kids.ui.theme.PokectBankOnPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.util.formatCurrency
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val quickAmounts = listOf(1.0, 2.0, 5.0, 10.0, 20.0, 50.0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepositModal(
    depositAmount: Double,
    isShowingSuccess: Boolean,
    currentBalance: Double,
    onAmountChange: (Double) -> Unit,
    onQuickSelect: (Double) -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = {
            scope.launch { sheetState.hide() }.invokeOnCompletion {
                if (!sheetState.isVisible) onDismiss()
            }
        },
        sheetState = sheetState,
        containerColor = PokectBankSurface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(4.dp)
                    .clip(KidShapes.small)
                    .background(PokectBankMuted)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) onDismiss()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = PokectBankOnBackground
                    )
                }
            }

            Text(
                text = "Guardar Moedinha",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PokectBankOnBackground
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isShowingSuccess) {
                SuccessContent(
                    depositAmount = depositAmount,
                    onConfirm = onConfirm
                )
            } else {
                NormalContent(
                    depositAmount = depositAmount,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement,
                    onQuickSelect = onQuickSelect,
                    onConfirm = onConfirm
                )
            }
        }
    }
}

@Composable
private fun NormalContent(
    depositAmount: Double,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onQuickSelect: (Double) -> Unit,
    onConfirm: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            var decrementScale by remember { mutableFloatStateOf(1f) }
            PressableCircleButton(
                onClick = onDecrement,
                onPressing = { scale -> decrementScale = scale },
                icon = Icons.Default.Remove,
                scale = decrementScale
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = formatCurrency(depositAmount),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PokectBankOnBackground
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.width(16.dp))

            var incrementScale by remember { mutableFloatStateOf(1f) }
            PressableCircleButton(
                onClick = onIncrement,
                onPressing = { scale -> incrementScale = scale },
                icon = Icons.Default.Add,
                scale = incrementScale
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val columns = 3
        val rows = 2
        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until columns) {
                    val index = row * columns + col
                    val amount = quickAmounts[index]
                    val isSelected = depositAmount == amount
                    QuickSelectChip(
                        amount = amount,
                        isSelected = isSelected,
                        onClick = { onQuickSelect(amount) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        var ctaScale by remember { mutableFloatStateOf(1f) }
        PressableCtaButton(
            text = "Guardar no Cofrinho!",
            onClick = onConfirm,
            onPressing = { scale -> ctaScale = scale },
            scale = ctaScale
        )
    }
}

@Composable
private fun SuccessContent(
    depositAmount: Double,
    @Suppress("UNUSED_PARAMETER") onConfirm: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = MaterialTheme.typography.displayLarge.fontSize,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Moedinha guardada!",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PokectBankOnBackground
            ),
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Text(
            text = "+${formatCurrency(depositAmount)}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = PokectBankSuccess
            )
        )
    }
}

@Composable
private fun QuickSelectChip(
    amount: Double,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isSelected) {
        GradientPresets.gradientPrimary
    } else {
        Brush.linearGradient(
            colors = listOf(PokectBankMuted, PokectBankMuted),
        )
    }

    val textColor = if (isSelected) PokectBankOnPrimary else PokectBankOnBackground

    Box(
        modifier = modifier
            .height(44.dp)
            .clip(KidShapes.small)
            .background(background)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "R$${amount.toInt()}",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = textColor
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PressableCircleButton(
    onClick: () -> Unit,
    onPressing: (Float) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    scale: Float
) {
    LaunchedEffect(scale) {
        if (scale < 1f) {
            delay(150)
            onPressing(1f)
        }
    }
    Box(
        modifier = Modifier
            .size(44.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(PokectBankMuted)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onPressing(0.95f)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PokectBankOnBackground
        )
    }
}

@Composable
private fun PressableCtaButton(
    text: String,
    onClick: () -> Unit,
    onPressing: (Float) -> Unit,
    scale: Float
) {
    LaunchedEffect(scale) {
        if (scale < 1f) {
            delay(150)
            onPressing(1f)
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .clip(KidShapes.medium)
            .background(GradientPresets.gradientSuccess)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    onPressing(0.95f)
                    onClick()
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = PokectBankOnPrimary
            )
        )
    }
}
