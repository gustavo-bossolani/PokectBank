package com.pokect.bank.kids.data.repository

import com.pokect.bank.kids.data.models.FamilyMember
import com.pokect.bank.kids.data.models.GoalColor
import com.pokect.bank.kids.data.models.Mission
import com.pokect.bank.kids.data.models.MissionCategory
import com.pokect.bank.kids.data.models.MissionDifficulty
import com.pokect.bank.kids.data.models.MissionStatus
import com.pokect.bank.kids.data.models.Reward
import com.pokect.bank.kids.data.models.SavingsGoal
import com.pokect.bank.kids.data.models.Transaction
import com.pokect.bank.kids.data.models.TransactionType
import com.pokect.bank.kids.data.models.User
import com.pokect.bank.kids.data.models.WeeklyChallenge
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.CancellationException
import java.time.Duration
import java.time.Instant
import android.util.Log

class AppRepository {

    // === In-memory state ===
    private val _user = MutableStateFlow(createSeedUser())
    val user: StateFlow<User> = _user.asStateFlow()

    private val _missions = MutableStateFlow(createSeedMissions())
    val missions: StateFlow<List<Mission>> = _missions.asStateFlow()

    private val _goals = MutableStateFlow(createSeedGoals())
    val goals: StateFlow<List<SavingsGoal>> = _goals.asStateFlow()

    private val _rewards = MutableStateFlow(createSeedRewards())
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    private val _familyMembers = MutableStateFlow(createSeedFamilyMembers())
    val familyMembers: StateFlow<List<FamilyMember>> = _familyMembers.asStateFlow()

    private val _transactions = MutableStateFlow(createSeedTransactions())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _weeklyChallenges = MutableStateFlow(createSeedWeeklyChallenges())
    val weeklyChallenges: StateFlow<List<WeeklyChallenge>> = _weeklyChallenges.asStateFlow()

    // === Event broadcast ===
    private val _events = MutableSharedFlow<RepositoryEvent>(
        extraBufferCapacity = 16,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<RepositoryEvent> = _events.asSharedFlow()

    private fun broadcastEvent(event: RepositoryEvent) {
        if (!_events.tryEmit(event)) {
            Log.w("AppRepository", "Dropped $event — buffer full")
        }
    }

    // === Thread safety ===
    // Lock ordering (MUST be enforced by all methods acquiring multiple mutexes):
    //   1. userMutex  →  2. rewardsMutex
    // Never acquire rewardsMutex before userMutex — doing so risks deadlock.
    // Currently only redeemReward() uses both mutexes.
    private val userMutex = Mutex()
    private val goalsMutex = Mutex()
    private val challengesMutex = Mutex()
    private val rewardsMutex = Mutex()
    private val transactionsMutex = Mutex()

    // === Read operations ===
    suspend fun getUser(): User = user.value
    suspend fun getMissions(): List<Mission> = missions.value
    suspend fun getGoals(): List<SavingsGoal> = goals.value
    suspend fun getRewards(): List<Reward> = rewards.value
    suspend fun getFamilyMembers(): List<FamilyMember> = familyMembers.value
    suspend fun getTransactions(): List<Transaction> = transactions.value
    suspend fun getWeeklyChallenges(): List<WeeklyChallenge> = weeklyChallenges.value

    // === Write operations ===

    private fun syncCurrentUserToFamilyMembers() {
        val user = _user.value
        _familyMembers.update { members ->
            members.map {
                if (it.isCurrentUser) it.copy(xp = user.totalXp, coins = user.coins, level = user.level) else it
            }
        }
    }

    suspend fun deposit(amount: Double): Result<Unit> {
        return userMutex.withLock {
            if (amount < 1.0) return@withLock Result.failure(IllegalArgumentException("Deposit amount must be at least 1.0"))
            if (amount > 10000.0) return@withLock Result.failure(IllegalArgumentException("Deposit amount exceeds maximum"))
            val current = _user.value
            _user.update {
                current.copy(
                    balance = current.balance + amount,
                    coins = current.coins + amount.toInt(),
                    xp = current.xp + (amount * 10).toInt(),
                    totalXp = current.totalXp + (amount * 10).toInt()
                )
            }
            Result.success(Unit)
        }.also { result ->
            if (result.isSuccess) {
                val now = java.time.LocalDate.now()
                transactionsMutex.withLock {
                    _transactions.update { transactions ->
                        transactions + Transaction(
                            id = "t_${java.util.UUID.randomUUID()}",
                            type = TransactionType.DEPOSIT,
                            title = "Guardou moedinhas",
                            amount = amount,
                            date = now.toString(),
                            icon = "💰"
                        )
                    }
                }
                broadcastEvent(RepositoryEvent.UserUpdated)
                broadcastEvent(RepositoryEvent.TransactionsChanged)
            }
            syncCurrentUserToFamilyMembers()
        }
    }

    suspend fun createGoal(
        title: String,
        icon: String,
        targetAmount: Double,
        daysLeft: Int
    ): Result<Unit> {
        return goalsMutex.withLock {
            try {
                require(title.isNotBlank()) { "Title cannot be empty" }
                require(icon.isNotBlank()) { "Icon cannot be empty" }
                require(targetAmount > 0.0) { "Target amount must be positive" }
                require(daysLeft > 0) { "Days left must be positive" }
                val newGoal = SavingsGoal(
                    id = "g_${java.util.UUID.randomUUID()}",
                    title = title.trim(),
                    icon = icon,
                    targetAmount = targetAmount,
                    currentAmount = 0.0,
                    daysLeft = daysLeft,
                    color = GoalColor.entries[_goals.value.size % GoalColor.entries.size]
                )
                _goals.update { it + newGoal }
                broadcastEvent(RepositoryEvent.GoalsChanged)
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun redeemReward(rewardId: String): Result<Unit> {
        // Read reward outside locks — StateFlow snapshot is safe for lookup
        val reward = _rewards.value.find { it.id == rewardId }
            ?: return Result.failure(IllegalArgumentException("Reward not found: $rewardId"))

        // Always acquire userMutex first, then rewardsMutex (documented lock ordering)
        return userMutex.withLock {
            rewardsMutex.withLock {
                val current = _user.value
                if (current.coins < reward.cost) {
                    return@withLock Result.failure(IllegalStateException("Insufficient coins"))
                }

                _user.update { current ->
                    current.copy(coins = current.coins - reward.cost)
                }
                _rewards.update { rewards ->
                    rewards.map { r ->
                        if (r.id == rewardId) r.copy(redeemed = true) else r
                    }
                }
                Result.success(Unit)
            }
        }.also { result ->
            if (result.isSuccess) {
                // Transaction write uses its own mutex after user/rewards locks release
                val now = java.time.LocalDate.now()
                transactionsMutex.withLock {
                    _transactions.update { transactions ->
                        transactions + Transaction(
                            id = "t_${java.util.UUID.randomUUID()}",
                            type = TransactionType.REWARD,
                            title = "Resgatou: ${reward.title}",
                            amount = (-reward.cost).toDouble(),
                            date = now.toString(),
                            icon = reward.icon
                        )
                    }
                }

                broadcastEvent(RepositoryEvent.UserUpdated)
                broadcastEvent(RepositoryEvent.RewardsChanged)
                broadcastEvent(RepositoryEvent.TransactionsChanged)
            }
            syncCurrentUserToFamilyMembers()
        }
    }

    suspend fun allocateToGoal(goalId: String, amount: Double): Result<Unit> {
        if (amount <= 0) return Result.failure(IllegalArgumentException("Amount must be greater than 0"))

        return goalsMutex.withLock {
            val currentGoals = _goals.value
            val goal = currentGoals.find { it.id == goalId }
                ?: return@withLock Result.failure(IllegalArgumentException("Goal not found: $goalId"))

            val user = _user.value
            if (user.balance < amount) {
                return@withLock Result.failure(IllegalStateException("Insufficient balance"))
            }

            _goals.update { goals ->
                goals.map { g ->
                    if (g.id == goalId) g.copy(currentAmount = g.currentAmount + amount) else g
                }
            }
            _user.update { it.copy(balance = it.balance - amount) }

            broadcastEvent(RepositoryEvent.GoalsChanged)
            broadcastEvent(RepositoryEvent.UserUpdated)
            Result.success(Unit)
        }
    }

    suspend fun joinWeeklyChallenge(challengeId: String): Result<Unit> {
        return challengesMutex.withLock {
            try {
                val currentFamilyMember = _familyMembers.value.find { it.isCurrentUser }
                val currentUserId = currentFamilyMember?.id ?: "fm1"
                val challenges = _weeklyChallenges.value
                val targetChallenge = challenges.find { it.id == challengeId }
                    ?: return@withLock Result.failure(IllegalArgumentException("Challenge not found: $challengeId"))
                if (targetChallenge.hasJoined) {
                    return@withLock Result.failure(IllegalStateException("Already joined this challenge"))
                }
                val updated = challenges.map { challenge ->
                    if (challenge.id == challengeId) {
                        val newParticipants = if (challenge.participants.contains(currentUserId)) {
                            challenge.participants
                        } else {
                            challenge.participants + currentUserId
                        }
                        challenge.copy(hasJoined = true, participants = newParticipants)
                    } else {
                        challenge
                    }
                }
                _weeklyChallenges.value = updated

                // Create a mission from the challenge
                val newMission = Mission(
                    id = "m_challenge_${challengeId}",
                    title = "Desafio: ${targetChallenge.title}",
                    description = targetChallenge.description,
                    xp = targetChallenge.rewardXp,
                    coins = targetChallenge.rewardCoins,
                    progress = 0,
                    maxProgress = 1,
                    status = MissionStatus.IN_PROGRESS,
                    icon = "⚡",
                    difficulty = MissionDifficulty.MEDIUM,
                    category = MissionCategory.SPECIAL
                )
                _missions.update { it + newMission }

                broadcastEvent(RepositoryEvent.MissionsChanged)
                broadcastEvent(RepositoryEvent.RankingChanged)
                Result.success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // === Seed data (moved from MockData) ===

    private fun createSeedUser() = User(
        name = "Lucas", level = 5, balance = 47.50, goal = 100.0,
        xp = 750, xpToNextLevel = 1000, totalXp = 4750,
        streak = 12, coins = 85,
        badges = listOf("🏆", "⭐", "🎯", "💰", "🔥", "📚", "🎮", "🌟"),
        selectedAvatar = 1
    )

    private fun createSeedMissions() = listOf(
        Mission(
            id = "m1", title = "Guardar R\$5 hoje", description = "Faça um depósito de pelo menos R\$5",
            xp = 50, coins = 10, progress = 0, maxProgress = 5, status = MissionStatus.AVAILABLE,
            icon = "🎯", difficulty = MissionDifficulty.EASY, category = MissionCategory.DAILY
        ),
        Mission(
            id = "m2", title = "Sequência de 7 dias", description = "Mantenha uma streak de 7 dias seguidos economizando",
            xp = 150, coins = 30, progress = 3, maxProgress = 7, status = MissionStatus.IN_PROGRESS,
            icon = "🔥", difficulty = MissionDifficulty.MEDIUM, category = MissionCategory.WEEKLY
        ),
        Mission(
            id = "m3", title = "Meta semanal", description = "Atinga 50% da sua meta de economia esta semana",
            xp = 100, coins = 20, progress = 35, maxProgress = 50, status = MissionStatus.IN_PROGRESS,
            icon = "🎯", difficulty = MissionDifficulty.MEDIUM, category = MissionCategory.WEEKLY
        ),
        Mission(
            id = "m4", title = "Super economizador", description = "Economize R\$100 no total",
            xp = 300, coins = 50, progress = 47, maxProgress = 100, status = MissionStatus.IN_PROGRESS,
            icon = "🚀", difficulty = MissionDifficulty.HARD, category = MissionCategory.SPECIAL
        )
    )

    private fun createSeedGoals() = listOf(
        SavingsGoal(
            id = "g1", title = "Videogame Novo", icon = "🎮", targetAmount = 500.0,
            currentAmount = 47.50, daysLeft = 45, color = GoalColor.PRIMARY
        ),
        SavingsGoal(
            id = "g2", title = "Bicicleta", icon = "🚲", targetAmount = 350.0,
            currentAmount = 350.0, daysLeft = 0, color = GoalColor.SUCCESS
        ),
        SavingsGoal(
            id = "g3", title = "Livro de Aventuras", icon = "📚", targetAmount = 80.0,
            currentAmount = 47.50, daysLeft = 12, color = GoalColor.SECONDARY
        )
    )

    private fun createSeedRewards() = listOf(
        Reward(id = "r1", title = "Escudo Mágico", description = "Protege sua economia por 7 dias", icon = "🛡️", cost = 50, isNew = true, redeemed = false),
        Reward(id = "r2", title = "Power-up 2x", description = "Dobra o XP da próxima missão", icon = "⚡", cost = 100, isNew = false, redeemed = false),
        Reward(id = "r3", title = "Dia de Folga", description = "Mantém sua streak por 1 dia sem depósito", icon = "🏖️", cost = 75, isNew = true, redeemed = false),
        Reward(id = "r4", title = "Baú de Moedas", description = "Ganha 50 moedas extras", icon = "💎", cost = 200, isNew = false, redeemed = false)
    )

    private fun createSeedFamilyMembers() = listOf(
        FamilyMember(id = "fm1", name = "Lucas", level = 5, coins = 85, xp = 4750, streak = 12, isCurrentUser = true),
        FamilyMember(id = "fm2", name = "Sofia", level = 6, coins = 120, xp = 5800, streak = 18, isCurrentUser = false),
        FamilyMember(id = "fm3", name = "Pedro", level = 3, coins = 45, xp = 2100, streak = 5, isCurrentUser = false),
        FamilyMember(id = "fm4", name = "Mamãe", level = 8, coins = 250, xp = 9200, streak = 30, isCurrentUser = false),
        FamilyMember(id = "fm5", name = "Papai", level = 7, coins = 180, xp = 7500, streak = 22, isCurrentUser = false)
    )

    private fun createSeedTransactions() = listOf(
        Transaction(id = "t1", type = TransactionType.DEPOSIT, title = "Guardou moedinhas", amount = 10.0, date = "2026-05-13", icon = "💰"),
        Transaction(id = "t2", type = TransactionType.MISSION, title = "Missão: Guardar R\$5", amount = 5.0, date = "2026-05-12", icon = "🎯"),
        Transaction(id = "t3", type = TransactionType.GOAL, title = "Meta: Videogame", amount = -20.0, date = "2026-05-10", icon = "🎮"),
        Transaction(id = "t4", type = TransactionType.REWARD, title = "Resgatou: Escudo Mágico", amount = -50.0, date = "2026-05-08", icon = "🛡️"),
        Transaction(id = "t5", type = TransactionType.DEPOSIT, title = "Guardou moedinhas", amount = 15.0, date = "2026-05-05", icon = "💰")
    )

    private fun createSeedWeeklyChallenges() = listOf(
        WeeklyChallenge(
            id = "wc1",
            title = "Economia da Semana",
            description = "Guarde pelo menos R\$20 esta semana para ganhar recompensas extras",
            rewardXp = 200,
            rewardCoins = 40,
            endDate = Instant.now().plus(Duration.ofDays(7)),
            participants = listOf("fm2", "fm4"),
            hasJoined = false
        )
    )
}
