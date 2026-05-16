package com.pokect.bank.kids.ui.screens.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokect.bank.kids.data.models.Transaction
import com.pokect.bank.kids.data.models.TransactionType
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankError
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnSurface
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSecondary
import com.pokect.bank.kids.ui.theme.PokectBankSuccess
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSurfaceVariant
import com.pokect.bank.kids.ui.theme.PokectBankTertiary
import com.pokect.bank.kids.util.formatCurrency
import com.pokect.bank.kids.util.formatDateLabel

private val defaultTypeEmojis = mapOf(
    TransactionType.DEPOSIT to "💰",
    TransactionType.WITHDRAW to "💸",
    TransactionType.REWARD to "🎁",
    TransactionType.MISSION to "⭐",
    TransactionType.GOAL to "🎯",
)

@Composable
fun TransactionHistoryCard(
    transactions: List<Transaction>,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = KidShapes.large,
        colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Histórico",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = PokectBankOnSurface
                    )
                )
                Text(
                    text = if (isExpanded) "Ver menos" else "Ver todas",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Normal,
                        color = PokectBankPrimary
                    ),
                    modifier = Modifier.clickable { onToggle() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (transactions.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "📭",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nenhuma transação ainda",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = PokectBankOnSurface
                        )
                    )
                    Text(
                        text = "Comece a economizar!",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Normal,
                            color = PokectBankMutedForeground
                        )
                    )
                }
            } else {
                Column {
                    // Always show first 3 (or fewer)
                    transactions.take(3).forEach { tx ->
                        TransactionRow(transaction = tx)
                    }

                    // Animate additional transactions when expanding
                    if (transactions.size > 3) {
                        AnimatedVisibility(
                            visible = isExpanded,
                            enter = expandVertically() + fadeIn(initialAlpha = 0.3f),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            Column {
                                transactions.drop(3).forEach { tx ->
                                    TransactionRow(transaction = tx)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: Transaction,
    modifier: Modifier = Modifier
) {
    val (bgColor, iconColor, amountColor) = getTransactionColors(transaction.type)
    val emoji = transaction.icon ?: defaultTypeEmojis[transaction.type] ?: "💵"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = emoji,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.size(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = PokectBankOnSurface
                )
            )
            Text(
                text = formatDateLabel(transaction.date),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Normal,
                    color = PokectBankMutedForeground
                )
            )
        }

        Text(
            text = if (transaction.amount >= 0) {
                "+${formatCurrency(transaction.amount)}"
            } else {
                formatCurrency(transaction.amount)
            },
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = amountColor
            )
        )
    }
}

private fun getTransactionColors(type: TransactionType): Triple<Color, Color, Color> = when (type) {
    TransactionType.DEPOSIT -> Triple(
        PokectBankSuccess.copy(alpha = 0.1f),
        PokectBankSuccess,
        PokectBankSuccess
    )
    TransactionType.WITHDRAW -> Triple(
        PokectBankError.copy(alpha = 0.1f),
        PokectBankError,
        PokectBankError
    )
    TransactionType.REWARD -> Triple(
        PokectBankSecondary.copy(alpha = 0.1f),
        PokectBankSecondary,
        PokectBankSuccess
    )
    TransactionType.MISSION -> Triple(
        PokectBankTertiary.copy(alpha = 0.1f),
        PokectBankTertiary,
        PokectBankSuccess
    )
    TransactionType.GOAL -> Triple(
        PokectBankPrimary.copy(alpha = 0.1f),
        PokectBankPrimary,
        PokectBankSuccess
    )
}
