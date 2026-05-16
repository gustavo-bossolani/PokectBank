package com.pokect.bank.kids.ui.screens.goals

import com.pokect.bank.kids.data.models.GoalColor
import com.pokect.bank.kids.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class GoalsViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads all goals from repository`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        val state = vm.uiState.value

        assertEquals(3, state.goals.size)
    }

    @Test
    fun `goals have required fields`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val goal = repo.getGoals().first()

        assertTrue(goal.id.isNotEmpty())
        assertTrue(goal.title.isNotEmpty())
        assertTrue(goal.icon.isNotEmpty())
        assertTrue(goal.targetAmount > 0)
        assertTrue(goal.currentAmount >= 0)
        assertTrue(goal.daysLeft >= 0)
    }

    @Test
    fun `GoalColor has 4 category values`() {
        assertEquals(4, GoalColor.entries.size)
        assertTrue(GoalColor.entries.contains(GoalColor.PRIMARY))
        assertTrue(GoalColor.entries.contains(GoalColor.SECONDARY))
        assertTrue(GoalColor.entries.contains(GoalColor.SUCCESS))
        assertTrue(GoalColor.entries.contains(GoalColor.WARNING))
    }

    @Test
    fun `progress percent calculates correctly for incomplete goals`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        val goal = vm.uiState.value.goals.first { it.currentAmount < it.targetAmount }

        val progress = goal.currentAmount / goal.targetAmount
        assertTrue(progress in 0.0..1.0)
    }

    @Test
    fun `progress percent is 1 for completed goals`() = runTest(testDispatcher) {
        val repo = AppRepository()

        val completedGoal = repo.getGoals().find { it.currentAmount >= it.targetAmount }

        if (completedGoal != null) {
            val progress = completedGoal.currentAmount / completedGoal.targetAmount
            assertEquals(1.0, progress, 0.01)
        }
    }

    @Test
    fun `showAddGoalModal updates state`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)

        assertFalse(vm.uiState.value.showAddGoalModal)

        vm.showAddGoalModal()

        assertTrue(vm.uiState.value.showAddGoalModal)
    }

    @Test
    fun `hideAddGoalModal updates state`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        vm.showAddGoalModal()

        vm.hideAddGoalModal()

        assertFalse(vm.uiState.value.showAddGoalModal)
    }

    @Test
    fun `createGoal adds new goal to list`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        val initialCount = vm.uiState.value.goals.size

        vm.createGoal(
            name = "Nova Meta",
            icon = "🎮",
            targetAmount = 1000.0,
            daysLeft = 30
        )

        // Wait for coroutine to complete
        kotlinx.coroutines.delay(100)

        assertEquals(initialCount + 1, vm.uiState.value.goals.size)
    }

    @Test
    fun `createGoal assigns correct properties to new goal`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)

        vm.createGoal(
            name = "Minha Meta",
            icon = "🚲",
            targetAmount = 500.0,
            daysLeft = 14
        )

        kotlinx.coroutines.delay(100)

        val newGoal = vm.uiState.value.goals.last()

        assertEquals("Minha Meta", newGoal.title)
        assertEquals("🚲", newGoal.icon)
        assertEquals(500.0, newGoal.targetAmount)
        assertEquals(0.0, newGoal.currentAmount)
        assertEquals(14, newGoal.daysLeft)
    }

    @Test
    fun `createGoal hides modal after creation`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        vm.showAddGoalModal()

        vm.createGoal("Test", "🎮", 100.0, 30)

        kotlinx.coroutines.delay(100)

        assertFalse(vm.uiState.value.showAddGoalModal)
    }

    @Test
    fun `createGoal cycles through GoalColor`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)

        val colorsUsed = mutableListOf<GoalColor>()
        repeat(5) {
            vm.createGoal("Meta $it", "🎮", 100.0, 30)
            kotlinx.coroutines.delay(100)
            colorsUsed.add(vm.uiState.value.goals.last().color)
        }

        assertTrue(colorsUsed.take(4).toSet().size >= 3)
    }

    // === User Sync Tests ===

    @Test
    fun `initial state includes user from repository`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)

        val state = vm.uiState.value
        assertNotNull(state.user)
        assertEquals("Lucas", state.user?.name)
    }

    @Test
    fun `allocateToGoal calls repository`() = runTest(testDispatcher) {
        val repo = AppRepository()
        // Deposit to have balance
        repo.deposit(50.0)
        val vm = GoalsViewModel(repo)
        vm.showAddGoalModal()

        vm.allocateToGoal("g1", 10.0)

        kotlinx.coroutines.delay(100)

        assertFalse(vm.uiState.value.showAddGoalModal)
        assertNull(vm.uiState.value.error)
    }

    @Test
    fun `allocateToGoal sets error on failure`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = GoalsViewModel(repo)
        vm.showAddGoalModal()

        vm.allocateToGoal("nonexistent", 10.0)

        kotlinx.coroutines.delay(100)

        assertNotNull(vm.uiState.value.error)
    }
}
