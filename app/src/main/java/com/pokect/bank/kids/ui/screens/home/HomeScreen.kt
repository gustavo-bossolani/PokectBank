package com.pokect.bank.kids.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pokect.bank.kids.ui.screens.home.components.ConfettiOverlay
import com.pokect.bank.kids.ui.screens.home.components.DepositModal
import com.pokect.bank.kids.ui.screens.home.components.LevelCard
import com.pokect.bank.kids.ui.screens.home.components.MissionSummaryCard
import com.pokect.bank.kids.ui.screens.home.components.PiggyBankCard
import com.pokect.bank.kids.ui.screens.home.components.TransactionHistoryCard
import com.pokect.bank.kids.ui.theme.PokectBankBackground
import com.pokect.bank.kids.ui.theme.PokectBankOnBackground

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMissions: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(PokectBankBackground)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = if (uiState.userName.isNotBlank()) "Olá, ${uiState.userName}!" else "Olá!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = PokectBankOnBackground
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            PiggyBankCard(
                balance = uiState.balance,
                goal = uiState.goal,
                goalProgressPercent = uiState.getGoalProgressPercent(),
                depositAmount = uiState.depositAmount,
                onDepositClick = { viewModel.showDepositModal() },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LevelCard(
                level = uiState.level,
                levelTitle = uiState.getLevelTitle(),
                currentXp = uiState.currentXp,
                xpToNextLevel = uiState.xpToNextLevel,
                xpProgressPercent = uiState.getXpProgressPercent(),
                totalXp = uiState.totalXp,
                streak = uiState.streak,
                badges = uiState.badges,
                selectedAvatar = uiState.selectedAvatar,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            MissionSummaryCard(
                missions = uiState.missions,
                onMissionTap = { onNavigateToMissions() },
                onViewAllTap = { onNavigateToMissions() },
                modifier = Modifier.padding(bottom = 16.dp)
            )

            TransactionHistoryCard(
                transactions = uiState.transactions,
                isExpanded = uiState.isHistoryExpanded,
                onToggle = { viewModel.toggleHistory() },
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (uiState.showConfetti) {
            ConfettiOverlay(
                isActive = true,
                onFinished = { viewModel.onConfettiFinished() }
            )
        }
    }

    if (uiState.showDepositModal) {
        DepositModal(
            depositAmount = uiState.depositAmount,
            isShowingSuccess = uiState.depositSuccess,
            currentBalance = uiState.balance,
            onAmountChange = { viewModel.updateDepositAmount(it) },
            onQuickSelect = { viewModel.selectQuickAmount(it) },
            onIncrement = { viewModel.incrementDepositAmount() },
            onDecrement = { viewModel.decrementDepositAmount() },
            onConfirm = { viewModel.deposit(uiState.depositAmount) },
            onDismiss = { viewModel.hideDepositModal() }
        )
    }
}
