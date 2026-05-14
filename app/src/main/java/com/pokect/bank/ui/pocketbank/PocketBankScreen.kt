package com.pokect.bank.ui.pocketbank

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketBankApp() {
    var uiState by remember { mutableStateOf(PocketBankUiState()) }
    var showDepositDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ola, ${uiState.userData.name}!", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Saldo ${formatCurrency(uiState.userData.balance)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
            )
        },
        bottomBar = {
            PocketBottomBar(
                activeTab = uiState.activeTab,
                onTabChange = { tab -> uiState = uiState.copy(activeTab = tab) },
            )
        },
    ) { innerPadding ->
        when (uiState.activeTab) {
            PocketTab.HOME -> HomeTab(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onDepositClick = { showDepositDialog = true },
                onToggleHistory = { uiState = uiState.copy(showHistory = !uiState.showHistory) },
                onOpenMissions = { uiState = uiState.copy(activeTab = PocketTab.MISSIONS) },
            )

            PocketTab.GOALS -> GoalsTab(uiState = uiState, modifier = Modifier.padding(innerPadding))
            PocketTab.MISSIONS -> MissionsTab(uiState = uiState, modifier = Modifier.padding(innerPadding))
            PocketTab.REWARDS -> RewardsTab(
                uiState = uiState,
                modifier = Modifier.padding(innerPadding),
                onRedeem = { rewardId -> uiState = redeemReward(uiState, rewardId) },
            )

            PocketTab.RANKING -> RankingTab(uiState = uiState, modifier = Modifier.padding(innerPadding))
        }
    }

    if (showDepositDialog) {
        DepositDialog(
            onDismiss = { showDepositDialog = false },
            onConfirm = { amount ->
                uiState = depositAmount(uiState, amount)
                showDepositDialog = false
            },
        )
    }
}

@Composable
private fun HomeTab(
    uiState: PocketBankUiState,
    modifier: Modifier = Modifier,
    onDepositClick: () -> Unit,
    onToggleHistory: () -> Unit,
    onOpenMissions: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            BalanceCard(
                balance = uiState.userData.balance,
                goal = uiState.userData.goal,
                onDepositClick = onDepositClick,
            )
        }

        item {
            LevelCard(userData = uiState.userData)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Missoes ativas", fontWeight = FontWeight.Bold)
                TextButton(onClick = onOpenMissions) {
                    Text("Ver todas")
                }
            }
        }

        items(uiState.missions.take(2), key = { it.id }) { mission ->
            MissionItem(mission = mission)
        }

        item {
            OutlinedButton(onClick = onToggleHistory, modifier = Modifier.fillMaxWidth()) {
                Text(if (uiState.showHistory) "Esconder historico" else "Ver historico")
            }
        }

        if (uiState.showHistory) {
            items(uiState.transactions, key = { it.id }) { transaction ->
                TransactionItem(transaction = transaction)
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun GoalsTab(uiState: PocketBankUiState, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Minhas metas", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        items(uiState.goals, key = { it.id }) { goal ->
            GoalItem(goal = goal)
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                tonalElevation = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
            ) {
                Text(
                    text = "Adicionar nova meta",
                    modifier = Modifier.padding(20.dp),
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun MissionsTab(uiState: PocketBankUiState, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text("Missoes", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = true, onClick = {}, label = { Text("Todas") })
                FilterChip(selected = false, onClick = {}, label = { Text("Diarias") })
                FilterChip(selected = false, onClick = {}, label = { Text("Semanais") })
            }
        }

        items(uiState.missions, key = { it.id }) { mission ->
            MissionItem(mission = mission)
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun RewardsTab(
    uiState: PocketBankUiState,
    modifier: Modifier = Modifier,
    onRedeem: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Premios", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                AssistChip(onClick = {}, label = { Text("Moedas: ${uiState.userData.coins}") })
            }
        }

        items(uiState.rewards, key = { it.id }) { reward ->
            RewardItem(reward = reward, userCoins = uiState.userData.coins, onRedeem = onRedeem)
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun RankingTab(uiState: PocketBankUiState, modifier: Modifier = Modifier) {
    val ranking = uiState.familyMembers.sortedByDescending { it.xp }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Text(
                "Ranking da familia",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }

        items(ranking, key = { it.id }) { member ->
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = if (member.isCurrentUser) "${member.name} (voce)" else member.name,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = "Nivel ${member.level} - XP ${member.xp}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text("Moedas ${member.coins}", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Desafio da semana", fontWeight = FontWeight.Bold)
                    Text(
                        "Quem economizar mais R$ 20 ganha 50 moedas extras.",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

@Composable
private fun BalanceCard(balance: Double, goal: Double, onDepositClick: () -> Unit) {
    val progress = (balance / goal).coerceIn(0.0, 1.0).toFloat()

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Meu cofrinho", fontWeight = FontWeight.Bold)
            Text(formatCurrency(balance), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("Meta: ${formatCurrency(goal)}", style = MaterialTheme.typography.bodySmall)
            Button(onClick = onDepositClick, modifier = Modifier.fillMaxWidth()) {
                Text("Guardar moedinha")
            }
        }
    }
}

@Composable
private fun LevelCard(userData: UserData) {
    Card(shape = RoundedCornerShape(24.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text("Nivel ${userData.level}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(getLevelTitle(userData.level), color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(
                progress = { (userData.xp.toFloat() / userData.xpToNextLevel.toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth(),
            )
            Text("${userData.xp} / ${userData.xpToNextLevel} XP", style = MaterialTheme.typography.bodySmall)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill(title = "XP total", value = userData.totalXp.toString())
                StatPill(title = "Streak", value = "${userData.streak} dias")
                StatPill(title = "Conquistas", value = userData.badges.size.toString())
            }
        }
    }
}

@Composable
private fun StatPill(title: String, value: String) {
    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun MissionItem(mission: Mission) {
    val progress = if (mission.maxProgress == 0) 0f else mission.progress.toFloat() / mission.maxProgress.toFloat()
    val statusColor = when (mission.status) {
        MissionStatus.LOCKED -> MaterialTheme.colorScheme.surfaceVariant
        MissionStatus.AVAILABLE -> MaterialTheme.colorScheme.secondaryContainer
        MissionStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primaryContainer
        MissionStatus.COMPLETED -> MaterialTheme.colorScheme.tertiaryContainer
    }

    Card(colors = CardDefaults.cardColors(containerColor = statusColor)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(mission.title, fontWeight = FontWeight.Bold)
                Text(mission.difficulty.name)
            }
            Text(mission.description, style = MaterialTheme.typography.bodySmall)

            if (mission.status == MissionStatus.IN_PROGRESS) {
                LinearProgressIndicator(progress = { progress.coerceIn(0f, 1f) }, modifier = Modifier.fillMaxWidth())
                Text("${mission.progress}/${mission.maxProgress}", style = MaterialTheme.typography.labelSmall)
            }

            Text("Recompensa: ${mission.xp} XP + ${mission.coins} moedas", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun GoalItem(goal: Goal) {
    val progress = (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(goal.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
            Text("${formatCurrency(goal.currentAmount)} de ${formatCurrency(goal.targetAmount)}")
            if (goal.daysLeft > 0) {
                Text("${goal.daysLeft} dias restantes", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("Meta concluida", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RewardItem(reward: Reward, userCoins: Int, onRedeem: (String) -> Unit) {
    val canAfford = userCoins >= reward.cost
    val progress = (userCoins.toFloat() / reward.cost.toFloat()).coerceIn(0f, 1f)

    Card {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(reward.title, fontWeight = FontWeight.Bold)
                if (reward.isNew) {
                    Surface(
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Text("NOVO", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }
            Text(reward.description, style = MaterialTheme.typography.bodySmall)
            Text("Custo: ${reward.cost} moedas")

            if (!canAfford) {
                LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth())
                Text("Faltam ${reward.cost - userCoins} moedas", style = MaterialTheme.typography.labelSmall)
            }

            Button(
                onClick = { onRedeem(reward.id) },
                modifier = Modifier.fillMaxWidth(),
                enabled = canAfford,
            ) {
                Text(if (canAfford) "Resgatar" else "Sem moedas suficientes")
            }
        }
    }
}

@Composable
private fun TransactionItem(transaction: Transaction) {
    val isNegative = transaction.amount < 0
    val transactionColor = if (isNegative) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.secondaryContainer
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = transactionColor,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(transaction.title, fontWeight = FontWeight.SemiBold)
                Text(transaction.date, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                text = (if (isNegative) "-" else "+") + formatCurrency(kotlin.math.abs(transaction.amount)),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun PocketBottomBar(activeTab: PocketTab, onTabChange: (PocketTab) -> Unit) {
    val tabs = listOf(
        PocketTab.HOME to "Home",
        PocketTab.GOALS to "Metas",
        PocketTab.MISSIONS to "Missoes",
        PocketTab.REWARDS to "Premios",
        PocketTab.RANKING to "Ranking",
    )

    NavigationBar {
        tabs.forEach { (tab, label) ->
            NavigationBarItem(
                selected = activeTab == tab,
                onClick = { onTabChange(tab) },
                icon = {
                    val shortLabel = label.take(1)
                    Surface(
                        modifier = Modifier.size(22.dp),
                        shape = RoundedCornerShape(999.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                    ) {
                        BoxCenterText(shortLabel)
                    }
                },
                label = { Text(label) },
            )
        }
    }
}

@Composable
private fun BoxCenterText(text: String) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = text, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun DepositDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amount by remember { mutableDoubleStateOf(5.0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guardar no cofrinho") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Escolha um valor para deposito")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(onClick = { amount = (amount - 1.0).coerceAtLeast(1.0) }) { Text("-") }
                    Text(formatCurrency(amount), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    OutlinedButton(onClick = { amount += 1.0 }) { Text("+") }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1.0, 2.0, 5.0, 10.0, 20.0).forEach { quickAmount ->
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = if (amount == quickAmount) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier
                                .clickable { amount = quickAmount }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                        ) {
                            Text(formatCurrency(quickAmount))
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(amount) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
    )
}

private fun getLevelTitle(level: Int): String {
    return when {
        level >= 20 -> "Mestre das financas"
        level >= 15 -> "Expert em economia"
        level >= 10 -> "Super poupador"
        level >= 7 -> "Guardiao do cofrinho"
        level >= 5 -> "Colecionador de moedas"
        level >= 3 -> "Pequeno investidor"
        else -> "Aprendiz financeiro"
    }
}

private fun formatCurrency(amount: Double): String {
    return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(amount)
}
