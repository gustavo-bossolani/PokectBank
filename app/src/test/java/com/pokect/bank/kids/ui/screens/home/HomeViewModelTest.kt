package com.pokect.bank.kids.ui.screens.home

import com.pokect.bank.kids.data.models.MissionStatus
import com.pokect.bank.kids.data.repository.AppRepository
import com.pokect.bank.kids.util.formatCurrency
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

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

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
    fun `initial state matches repository seed data`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val state = vm.uiState.value

        assertEquals("Lucas", state.userName)
        assertEquals(47.50, state.balance)
        assertEquals(5, state.level)
        assertEquals(4750, state.totalXp)
        assertEquals(12, state.streak)
        assertEquals(85, state.coins)
        assertEquals(1, state.selectedAvatar)
    }

    @Test
    fun `deposit adds amount to balance`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val initialBalance = vm.uiState.value.balance

        vm.deposit(10.0)

        kotlinx.coroutines.delay(500)

        assertEquals(initialBalance + 10.0, vm.uiState.value.balance)
    }

    @Test
    fun `deposit increases XP and coins`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val initialXp = vm.uiState.value.totalXp
        val initialCoins = vm.uiState.value.coins

        vm.deposit(10.0)

        kotlinx.coroutines.delay(500)

        assertTrue(vm.uiState.value.totalXp > initialXp)
        assertTrue(vm.uiState.value.coins > initialCoins)
    }

    @Test
    fun `deposit triggers confetti`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)

        vm.deposit(10.0)

        kotlinx.coroutines.delay(500)

        assertTrue(vm.uiState.value.showConfetti)
    }

    @Test
    fun `increment deposit amount increases by 1`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val initial = vm.uiState.value.depositAmount

        vm.incrementDepositAmount()

        assertEquals(initial + 1.0, vm.uiState.value.depositAmount)
    }

    @Test
    fun `decrement deposit amount decreases by 1 with minimum of 1`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        vm.updateDepositAmount(5.0)

        vm.decrementDepositAmount()
        assertEquals(4.0, vm.uiState.value.depositAmount)

        vm.updateDepositAmount(1.0)
        vm.decrementDepositAmount()
        assertEquals(1.0, vm.uiState.value.depositAmount)
    }

    @Test
    fun `select quick amount replaces current value`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        vm.updateDepositAmount(15.0)

        vm.selectQuickAmount(5.0)

        assertEquals(5.0, vm.uiState.value.depositAmount)
    }

    @Test
    fun `update deposit amount rejects values below 1`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        vm.updateDepositAmount(5.0)

        vm.updateDepositAmount(0.5)

        assertEquals(5.0, vm.uiState.value.depositAmount)
    }

    @Test
    fun `show and hide deposit modal`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)

        vm.showDepositModal()
        assertTrue(vm.uiState.value.showDepositModal)

        vm.hideDepositModal()
        assertTrue(!vm.uiState.value.showDepositModal)
    }

    @Test
    fun `toggle history expands and collapses`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val initial = vm.uiState.value.isHistoryExpanded

        vm.toggleHistory()
        assertEquals(!initial, vm.uiState.value.isHistoryExpanded)

        vm.toggleHistory()
        assertEquals(initial, vm.uiState.value.isHistoryExpanded)
    }

    @Test
    fun `get goal progress percent returns correct value`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val state = vm.uiState.value

        val expected = ((state.balance / state.goal * 100).toFloat()).coerceIn(0f, 100f)
        assertEquals(expected, state.getGoalProgressPercent(), 0.1f)
    }

    @Test
    fun `get goal progress percent handles zero goal`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val state = vm.uiState.value.copy(goal = 0.0)

        assertEquals(0f, state.getGoalProgressPercent())
    }

    @Test
    fun `get level title returns correct titles`() {
        assertEquals("Aprendiz Financeiro 📚", HomeUiState(level = 1).getLevelTitle())
        assertEquals("Colecionador de Moedas 💰", HomeUiState(level = 5).getLevelTitle())
        assertEquals("Super Poupador 🚀", HomeUiState(level = 10).getLevelTitle())
        assertEquals("Mestre das Finanças 🏆", HomeUiState(level = 20).getLevelTitle())
    }

    @Test
    fun `get XP progress percent returns correct value`() {
        val state = HomeUiState(currentXp = 750, xpToNextLevel = 1000)

        assertEquals(75f, state.getXpProgressPercent())
    }

    @Test
    fun `get XP progress percent handles zero xpToNextLevel`() {
        val state = HomeUiState(currentXp = 500, xpToNextLevel = 0)

        assertEquals(0f, state.getXpProgressPercent())
    }

    @Test
    fun `missions filtered to IN_PROGRESS only`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)
        val missions = vm.uiState.value.missions

        assertTrue(missions.all { it.status == MissionStatus.IN_PROGRESS })
        assertTrue(missions.size <= 2)
    }

    @Test
    fun `format currency contains BRL symbol`() {
        val result = formatCurrency(10.0)
        assertTrue(result.contains("R$"))
        assertTrue(result.contains("10"))
    }

    @Test
    fun `get grouped transactions groups by date`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val grouped = getGroupedTransactions(repo.getTransactions())

        assertTrue(grouped.isNotEmpty())
    }

    @Test
    fun `deposit amount defaults to 5`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)

        assertEquals(5.0, vm.uiState.value.depositAmount)
    }

    @Test
    fun `quick amounts include expected values`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = HomeViewModel(repo)

        vm.selectQuickAmount(1.0)
        assertEquals(1.0, vm.uiState.value.depositAmount)

        vm.selectQuickAmount(50.0)
        assertEquals(50.0, vm.uiState.value.depositAmount)
    }
}
