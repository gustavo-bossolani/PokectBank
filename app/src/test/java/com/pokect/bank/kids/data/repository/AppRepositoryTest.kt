package com.pokect.bank.kids.data.repository

import com.pokect.bank.kids.data.models.MissionCategory
import com.pokect.bank.kids.data.models.MissionStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class AppRepositoryTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // === Seed Data Tests ===

    @Test
    fun `repository initializes with Lucas user`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val user = repo.getUser()

        assertEquals("Lucas", user.name)
        assertEquals(5, user.level)
        assertEquals(47.50, user.balance)
        assertEquals(100.0, user.goal)
        assertEquals(750, user.xp)
        assertEquals(1000, user.xpToNextLevel)
        assertEquals(4750, user.totalXp)
        assertEquals(12, user.streak)
        assertEquals(85, user.coins)
        assertEquals(1, user.selectedAvatar)
    }

    @Test
    fun `repository initializes with 4 missions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val missions = repo.getMissions()

        assertEquals(4, missions.size)
        assertEquals("m1", missions[0].id)
        assertEquals("m4", missions[3].id)
    }

    @Test
    fun `repository initializes with 3 goals`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val goals = repo.getGoals()

        assertEquals(3, goals.size)
        assertEquals("g1", goals[0].id)
        assertEquals("Videogame Novo", goals[0].title)
    }

    @Test
    fun `repository initializes with 4 rewards`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val rewards = repo.getRewards()

        assertEquals(4, rewards.size)
        assertEquals("r1", rewards[0].id)
        assertEquals("Escudo Mágico", rewards[0].title)
    }

    @Test
    fun `repository initializes with 5 family members`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val members = repo.getFamilyMembers()

        assertEquals(5, members.size)
        assertTrue(members.any { it.isCurrentUser })
    }

    @Test
    fun `repository initializes with 5 transactions`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val transactions = repo.getTransactions()

        assertEquals(5, transactions.size)
    }

    @Test
    fun `repository initializes with 1 weekly challenge`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val challenges = repo.getWeeklyChallenges()

        assertEquals(1, challenges.size)
        assertEquals("wc1", challenges[0].id)
        assertFalse(challenges[0].hasJoined)
    }

    // === Event Flow Tests ===

    @Test
    fun `deposit emits UserUpdated event`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()

        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.deposit(10.0)

        assertTrue(events.contains(RepositoryEvent.UserUpdated))
        job.cancel()
    }

    @Test
    fun `deposit emits TransactionsChanged event`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()

        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.deposit(10.0)

        assertTrue(events.contains(RepositoryEvent.TransactionsChanged))
        job.cancel()
    }

    @Test
    fun `createGoal emits GoalsChanged event`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()

        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.createGoal("Test Goal", "🎯", 100.0, 30)

        assertTrue(events.contains(RepositoryEvent.GoalsChanged))
        job.cancel()
    }

    @Test
    fun `redeemReward emits UserUpdated and RewardsChanged events`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()

        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.redeemReward("r1") // costs 50 coins, user has 85

        assertTrue(events.contains(RepositoryEvent.UserUpdated))
        assertTrue(events.contains(RepositoryEvent.RewardsChanged))
        job.cancel()
    }

    @Test
    fun `joinWeeklyChallenge emits RankingChanged event`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()

        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.joinWeeklyChallenge("wc1")

        assertTrue(events.contains(RepositoryEvent.RankingChanged))
        job.cancel()
    }

    // === Write Operation Tests ===

    @Test
    fun `deposit increases balance, coins, and xp`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialUser = repo.getUser()

        repo.deposit(10.0)

        val updatedUser = repo.getUser()
        assertEquals(initialUser.balance + 10.0, updatedUser.balance)
        assertEquals(initialUser.coins + 10, updatedUser.coins)
        assertEquals(initialUser.xp + 100, updatedUser.xp)
        assertEquals(initialUser.totalXp + 100, updatedUser.totalXp)
    }

    @Test
    fun `deposit rejects amount below 1`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialUser = repo.getUser()

        val result = repo.deposit(0.5)

        assertTrue(result.isFailure)
        val unchangedUser = repo.getUser()
        assertEquals(initialUser.balance, unchangedUser.balance)
    }

    @Test
    fun `createGoal appends new goal to list`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialCount = repo.getGoals().size

        repo.createGoal("New Goal", "🎯", 200.0, 60)

        assertEquals(initialCount + 1, repo.getGoals().size)
        val newGoal = repo.getGoals().last()
        assertEquals("New Goal", newGoal.title)
        assertEquals(0.0, newGoal.currentAmount)
    }

    @Test
    fun `redeemReward deducts coins and marks redeemed`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialCoins = repo.getUser().coins

        repo.redeemReward("r1") // costs 50

        val updatedUser = repo.getUser()
        assertEquals(initialCoins - 50, updatedUser.coins)

        val updatedRewards = repo.getRewards()
        val redeemed = updatedRewards.find { it.id == "r1" }
        assertNotNull(redeemed)
        assertTrue(redeemed.redeemed)
    }

    @Test
    fun `redeemReward fails with insufficient coins`() = runTest(testDispatcher) {
        val repo = AppRepository()
        // First redeem r1 (costs 50), then try r4 (costs 200)
        repo.redeemReward("r1")
        repo.redeemReward("r2") // costs 100, user now has 35
        repo.redeemReward("r3") // costs 75, user now has -140... wait

        // Actually let's just try to redeem something expensive after user has few coins
        // User starts with 85 coins. Redeem r2 (100) should fail.
        val repo2 = AppRepository()
        val result = repo2.redeemReward("r4") // costs 200, user has 85

        assertTrue(result.isFailure)
    }

    @Test
    fun `joinWeeklyChallenge sets hasJoined to true`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialChallenge = repo.getWeeklyChallenges().first()
        assertFalse(initialChallenge.hasJoined)

        repo.joinWeeklyChallenge("wc1")

        val updatedChallenge = repo.getWeeklyChallenges().first()
        assertTrue(updatedChallenge.hasJoined)
    }

    // === StateFlow Exposure Tests ===

    @Test
    fun `repository exposes user StateFlow`() = runTest(testDispatcher) {
        val repo = AppRepository()
        assertNotNull(repo.user.value)
        assertEquals("Lucas", repo.user.value.name)
    }

    @Test
    fun `repository exposes events SharedFlow`() = runTest(testDispatcher) {
        val repo = AppRepository()
        // Just verify the events flow exists and is not null
        assertNotNull(repo.events)
    }

    // === allocateToGoal Tests ===

    @Test
    fun `allocateToGoal deducts from user balance and adds to goal currentAmount`() = runTest(testDispatcher) {
        val repo = AppRepository()
        // Deposit first to increase balance
        repo.deposit(50.0)
        val initialUser = repo.getUser()
        val initialGoal = repo.getGoals().find { it.id == "g1" }!!

        val result = repo.allocateToGoal("g1", 20.0)

        assertTrue(result.isSuccess)
        val updatedUser = repo.getUser()
        val updatedGoal = repo.getGoals().find { it.id == "g1" }!!
        assertEquals(initialUser.balance - 20.0, updatedUser.balance)
        assertEquals(initialGoal.currentAmount + 20.0, updatedGoal.currentAmount)
    }

    @Test
    fun `allocateToGoal fails when goal not found`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val result = repo.allocateToGoal("nonexistent", 10.0)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `allocateToGoal fails when insufficient balance`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val user = repo.getUser()
        // Try to allocate more than user has
        val result = repo.allocateToGoal("g1", user.balance + 1000.0)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalStateException)
    }

    @Test
    fun `allocateToGoal fails when amount is zero`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val result = repo.allocateToGoal("g1", 0.0)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `allocateToGoal fails when amount is negative`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val result = repo.allocateToGoal("g1", -10.0)
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `allocateToGoal emits UserUpdated and GoalsChanged events`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()
        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.deposit(50.0) // ensure enough balance
        repo.allocateToGoal("g1", 10.0)

        assertTrue(events.contains(RepositoryEvent.UserUpdated))
        assertTrue(events.contains(RepositoryEvent.GoalsChanged))
        job.cancel()
    }

    // === Family Members Sync Tests ===

    @Test
    fun `deposit syncs user xp coins level to familyMembers`() = runTest(testDispatcher) {
        val repo = AppRepository()
        repo.deposit(50.0)

        val user = repo.getUser()
        val members = repo.getFamilyMembers()
        val currentUser = members.find { it.isCurrentUser }!!

        assertEquals(user.totalXp, currentUser.xp)
        assertEquals(user.coins, currentUser.coins)
        assertEquals(user.level, currentUser.level)
    }

    @Test
    fun `redeemReward syncs user xp coins level to familyMembers`() = runTest(testDispatcher) {
        val repo = AppRepository()
        repo.redeemReward("r1") // costs 50 coins

        val user = repo.getUser()
        val members = repo.getFamilyMembers()
        val currentUser = members.find { it.isCurrentUser }!!

        assertEquals(user.coins, currentUser.coins)
    }

    // === Weekly Challenge → Mission Bridge Tests ===

    @Test
    fun `joinWeeklyChallenge creates a mission from challenge`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val initialMissionCount = repo.getMissions().size

        repo.joinWeeklyChallenge("wc1")

        val missions = repo.getMissions()
        assertEquals(initialMissionCount + 1, missions.size)
        val newMission = missions.find { it.id == "m_challenge_wc1" }
        assertNotNull(newMission)
        assertTrue(newMission.title.startsWith("Desafio:"))
        assertEquals(MissionStatus.IN_PROGRESS, newMission.status)
        assertEquals(MissionCategory.SPECIAL, newMission.category)
    }

    @Test
    fun `joinWeeklyChallenge emits MissionsChanged event`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val events = mutableListOf<RepositoryEvent>()
        val job = launch(testDispatcher) {
            repo.events.collect { events.add(it) }
        }

        repo.joinWeeklyChallenge("wc1")

        assertTrue(events.contains(RepositoryEvent.MissionsChanged))
        job.cancel()
    }
}
