package com.pokect.bank.kids.ui.screens.goals

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokect.bank.R
import com.pokect.bank.kids.ui.screens.goals.components.AddGoalModal
import com.pokect.bank.kids.ui.screens.goals.components.GoalCard
import com.pokect.bank.kids.ui.theme.KidShapes
import com.pokect.bank.kids.ui.theme.PokectBankBackground
import com.pokect.bank.kids.ui.theme.PokectBankMutedForeground
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground
import com.pokect.bank.kids.ui.theme.PokectBankOutline
import com.pokect.bank.kids.ui.theme.PokectBankPrimary
import com.pokect.bank.kids.ui.theme.PokectBankSurface
import com.pokect.bank.kids.ui.theme.PokectBankSuccess

@Composable
fun GoalsScreen(
    viewModel: GoalsViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = PokectBankBackground
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(PokectBankBackground)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Header
            item {
                Text(
                    text = stringResource(R.string.goals_screen_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = PokectBankOnBackground,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Balance header card
            item {
                Card(
                    shape = KidShapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Seu cofrinho",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PokectBankMutedForeground,
                            fontWeight = FontWeight.Medium
                        )
                        val balanceText = uiState.user?.let {
                            "R$ %.2f".format(it.balance)
                        } ?: "Carregando..."
                        Text(
                            text = balanceText,
                            style = MaterialTheme.typography.titleLarge,
                            color = PokectBankPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (uiState.goals.isEmpty()) {
                // Empty state
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎯",
                                fontSize = 48.sp
                            )
                            Text(
                                text = stringResource(R.string.goals_empty_heading),
                                style = MaterialTheme.typography.headlineMedium,
                                color = PokectBankOnBackground,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                            Text(
                                text = stringResource(R.string.goals_empty_body),
                                style = MaterialTheme.typography.bodyMedium,
                                color = PokectBankMutedForeground,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            } else {
                // Goal cards
                items(uiState.goals, key = { it.id }) { goal ->
                    Column {
                        GoalCard(
                            goal = goal,
                            modifier = Modifier.animateItem()
                        )
                        // Allocate balance button — shows when user has balance > 0
                        if (uiState.user?.balance ?: 0.0 > 0.0) {
                            Button(
                                onClick = {
                                    val balance = uiState.user?.balance ?: 0.0
                                    viewModel.allocateToGoal(goal.id, balance)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PokectBankSuccess
                                ),
                                shape = KidShapes.medium
                            ) {
                                Text(
                                    text = "Resgatar para esta meta",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            // "Add new goal" card — always shown as last item
            item {
                Card(
                    shape = KidShapes.large,
                    colors = CardDefaults.cardColors(containerColor = PokectBankSurface),
                    modifier = Modifier
                        .drawBehind {
                            drawRoundRect(
                                color = PokectBankOutline,
                                size = size,
                                cornerRadius = CornerRadius(24f, 24f),
                                style = Stroke(
                                    width = 2.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                                )
                            )
                        }
                        .clip(KidShapes.large)
                        .clickable { viewModel.showAddGoalModal() }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 48.sp,
                            color = PokectBankPrimary
                        )
                        Text(
                            text = stringResource(R.string.add_goal_card_text),
                            style = MaterialTheme.typography.bodyMedium,
                            color = PokectBankPrimary,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Goal Modal
    if (uiState.showAddGoalModal) {
        AddGoalModal(
            onDismiss = { viewModel.hideAddGoalModal() },
            onCreateGoal = { name, icon, targetAmount, daysLeft ->
                viewModel.createGoal(name, icon, targetAmount, daysLeft)
            }
        )
    }
}
