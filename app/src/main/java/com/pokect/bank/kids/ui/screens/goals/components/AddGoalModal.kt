package com.pokect.bank.kids.ui.screens.goals.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pokect.bank.kids.ui.theme.GradientPresets
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankMuted
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground
import com.pokect.bank.kids.ui.theme.PokectBankOnPrimary
import com.pokect.bank.kids.ui.theme.PokectBankOnSurfaceVariant
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant
import kotlinx.coroutines.launch

private val deadlineOptions = listOf(7, 14, 30, 60, 90)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalModal(
    onDismiss: () -> Unit,
    onCreateGoal: (name: String, icon: String, targetAmount: Double, daysLeft: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    var nameState by remember { mutableStateOf("") }
    var emojiState by remember { mutableStateOf("\uD83C\uDFAE") }
    var valueState by remember { mutableStateOf("500") }
    var selectedDeadline by remember { mutableIntStateOf(30) }

    val parsedAmount = valueState
        .replace(',', '.')
        .toDoubleOrNull()
        ?: 0.0
    val isCtaEnabled = nameState.trim().isNotEmpty() && parsedAmount > 0

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
        ) {
            // Drag handle
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(48.dp)
                    .height(4.dp)
                    .clip(KidShapes.small)
                    .background(PokectBankMuted)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Close button
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

            // Title
            Text(
                text = "Nova Meta",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = PokectBankOnBackground
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Nome field
            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = { Text("Nome da meta") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Emoji picker
            EmojiPickerGrid(
                selectedEmoji = emojiState,
                onEmojiSelected = { emojiState = it },
                modifier = Modifier.padding(top = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Valor alvo field
            OutlinedTextField(
                value = valueState,
                onValueChange = { valueState = it.filter { c -> c.isDigit() || c == ',' || c == '.' } },
                label = { Text("Valor alvo (R$)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Prazo label
            Text(
                text = "Prazo",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = PokectBankOnSurfaceVariant
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Deadline chips
            val scrollState = rememberScrollState()
            Row(
                modifier = Modifier.horizontalScroll(scrollState),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                deadlineOptions.forEach { days ->
                    val isSelected = days == selectedDeadline
                    DeadlineChip(
                        days = days,
                        isSelected = isSelected,
                        onClick = { selectedDeadline = days }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CTA button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(KidShapes.medium)
                    .then(
                        if (isCtaEnabled) {
                            Modifier
                                .background(GradientPresets.gradientPrimary)
                                .clickable {
                                    val amount = valueState
                                        .replace(',', '.')
                                        .toDoubleOrNull() ?: 0.0
                                    if (nameState.trim().isNotEmpty() && amount > 0) {
                                        onCreateGoal(
                                            nameState.trim(),
                                            emojiState,
                                            amount,
                                            selectedDeadline
                                        )
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) onDismiss()
                                        }
                                    }
                                }
                        } else {
                            Modifier.background(PokectBankSurfaceVariant)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Criar Meta",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isCtaEnabled) PokectBankOnPrimary else PokectBankOnSurfaceVariant
                    )
                )
            }
        }
    }
}

@Composable
private fun DeadlineChip(
    days: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(KidShapes.small)
            .then(
                if (isSelected) {
                    Modifier.background(GradientPresets.gradientPrimary)
                } else {
                    Modifier.background(PokectBankSurfaceVariant)
                }
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$days dias",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PokectBankOnPrimary else PokectBankOnSurfaceVariant
            )
        )
    }
}
