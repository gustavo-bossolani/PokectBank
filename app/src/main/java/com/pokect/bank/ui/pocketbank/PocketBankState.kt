package com.pokect.bank.ui.pocketbank

enum class PocketTab {
    HOME,
    GOALS,
    MISSIONS,
    REWARDS,
    RANKING,
}

data class UserData(
    val name: String,
    val level: Int,
    val balance: Double,
    val goal: Double,
    val xp: Int,
    val xpToNextLevel: Int,
    val totalXp: Int,
    val streak: Int,
    val coins: Int,
    val badges: List<String>,
)

data class Mission(
    val id: String,
    val title: String,
    val description: String,
    val xp: Int,
    val coins: Int,
    val progress: Int,
    val maxProgress: Int,
    val status: MissionStatus,
    val difficulty: MissionDifficulty,
)

enum class MissionStatus {
    LOCKED,
    AVAILABLE,
    IN_PROGRESS,
    COMPLETED,
}

enum class MissionDifficulty {
    EASY,
    MEDIUM,
    HARD,
}

data class Goal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val daysLeft: Int,
)

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val cost: Int,
    val isNew: Boolean,
)

data class FamilyMember(
    val id: String,
    val name: String,
    val level: Int,
    val coins: Int,
    val xp: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false,
)

enum class TransactionType {
    DEPOSIT,
    WITHDRAW,
    REWARD,
    MISSION,
    GOAL,
}

data class Transaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val amount: Double,
    val date: String,
)

data class PocketBankUiState(
    val activeTab: PocketTab = PocketTab.HOME,
    val showHistory: Boolean = false,
    val userData: UserData = sampleUserData,
    val missions: List<Mission> = sampleMissions,
    val goals: List<Goal> = sampleGoals,
    val rewards: List<Reward> = sampleRewards,
    val familyMembers: List<FamilyMember> = sampleFamilyMembers,
    val transactions: List<Transaction> = sampleTransactions,
)

fun depositAmount(state: PocketBankUiState, amount: Double): PocketBankUiState {
    val safeAmount = amount.coerceAtLeast(0.0)
    return state.copy(
        userData = state.userData.copy(
            balance = state.userData.balance + safeAmount,
            xp = state.userData.xp + (safeAmount * 10).toInt(),
            coins = state.userData.coins + safeAmount.toInt(),
        ),
        transactions = listOf(
            Transaction(
                id = "deposit-${state.transactions.size + 1}",
                type = TransactionType.DEPOSIT,
                title = "Deposito no cofrinho",
                amount = safeAmount,
                date = "Agora",
            ),
        ) + state.transactions,
    )
}

fun redeemReward(state: PocketBankUiState, rewardId: String): PocketBankUiState {
    val reward = state.rewards.firstOrNull { it.id == rewardId } ?: return state
    if (state.userData.coins < reward.cost) return state

    return state.copy(
        userData = state.userData.copy(coins = state.userData.coins - reward.cost),
        transactions = listOf(
            Transaction(
                id = "reward-${state.transactions.size + 1}",
                type = TransactionType.REWARD,
                title = "Resgate: ${reward.title}",
                amount = -reward.cost.toDouble(),
                date = "Agora",
            ),
        ) + state.transactions,
    )
}

val sampleUserData = UserData(
    name = "Lucas",
    level = 5,
    balance = 47.50,
    goal = 100.0,
    xp = 750,
    xpToNextLevel = 1000,
    totalXp = 4750,
    streak = 12,
    coins = 85,
    badges = listOf("Saver", "Streak", "Target", "Family"),
)

val sampleMissions = listOf(
    Mission(
        id = "1",
        title = "Poupador da semana",
        description = "Guarde dinheiro por 7 dias seguidos",
        xp = 100,
        coins = 20,
        progress = 5,
        maxProgress = 7,
        status = MissionStatus.IN_PROGRESS,
        difficulty = MissionDifficulty.MEDIUM,
    ),
    Mission(
        id = "2",
        title = "Primeira meta",
        description = "Complete sua primeira meta de economia",
        xp = 150,
        coins = 30,
        progress = 0,
        maxProgress = 1,
        status = MissionStatus.AVAILABLE,
        difficulty = MissionDifficulty.EASY,
    ),
    Mission(
        id = "3",
        title = "Super economizador",
        description = "Guarde R$ 50 no cofrinho",
        xp = 200,
        coins = 50,
        progress = 47,
        maxProgress = 50,
        status = MissionStatus.IN_PROGRESS,
        difficulty = MissionDifficulty.HARD,
    ),
    Mission(
        id = "4",
        title = "Mestre dos cofrinhos",
        description = "Alcance o nivel 10",
        xp = 500,
        coins = 100,
        progress = 0,
        maxProgress = 1,
        status = MissionStatus.LOCKED,
        difficulty = MissionDifficulty.HARD,
    ),
)

val sampleGoals = listOf(
    Goal("1", "Bicicleta nova", 100.0, 47.5, 30),
    Goal("2", "Videogame", 200.0, 25.0, 60),
    Goal("3", "Presente para mamae", 50.0, 50.0, 0),
)

val sampleRewards = listOf(
    Reward("1", "Adesivo especial", "Decore seu cofrinho", 30, isNew = true),
    Reward("2", "Tema espacial", "Novo tema para o app", 50, isNew = false),
    Reward("3", "Pet virtual", "Um amigo para acompanhar", 100, isNew = true),
    Reward("4", "Chapeu de mago", "Acessorio para avatar", 75, isNew = false),
)

val sampleFamilyMembers = listOf(
    FamilyMember("1", "Maria", level = 8, coins = 150, xp = 6200, streak = 21),
    FamilyMember("2", "Lucas", level = 5, coins = 85, xp = 4750, streak = 12, isCurrentUser = true),
    FamilyMember("3", "Pedro", level = 4, coins = 60, xp = 3100, streak = 5),
    FamilyMember("4", "Ana", level = 3, coins = 45, xp = 2200, streak = 8),
    FamilyMember("5", "Joao", level = 2, coins = 30, xp = 1100, streak = 3),
)

val sampleTransactions = listOf(
    Transaction("1", TransactionType.DEPOSIT, "Mesada semanal", 10.0, "Hoje, 14:30"),
    Transaction("2", TransactionType.MISSION, "Missao completada", 5.0, "Ontem, 18:00"),
    Transaction("3", TransactionType.DEPOSIT, "Dinheiro do vovo", 20.0, "12/05/2026"),
    Transaction("4", TransactionType.REWARD, "Resgate de premio", -15.0, "10/05/2026"),
    Transaction("5", TransactionType.DEPOSIT, "Guardei do lanche", 2.5, "08/05/2026"),
)

