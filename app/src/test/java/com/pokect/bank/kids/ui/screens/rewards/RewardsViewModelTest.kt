package com.pokect.bank.kids.ui.screens.rewards

import com.pokect.bank.kids.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RewardsViewModelTest {

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
        val vm = RewardsViewModel(repo)
        val state = vm.uiState.value

        assertEquals(85, state.coins)
        assertEquals(4, state.rewards.size)
        assertFalse(state.showRedemptionModal)
        assertEquals(null, state.selectedReward)
        assertFalse(state.showConfetti)
    }

    @Test
    fun `canAfford is true when coins sufficient`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)
        val escudo = vm.uiState.value.rewards.find { it.reward.id == "r1" }

        assertEquals(true, escudo?.canAfford)
    }

    @Test
    fun `canAfford is false when coins insufficient`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)
        val bau = vm.uiState.value.rewards.find { it.reward.id == "r4" }

        assertEquals(false, bau?.canAfford)
    }

    @Test
    fun `redeemReward sets selectedReward and showRedemptionModal`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)

        vm.redeemReward("r1")

        val state = vm.uiState.value
        assertEquals("r1", state.selectedReward?.id)
        assertTrue(state.showRedemptionModal)
    }

    @Test
    fun `confirmRedemption deducts coins and shows confetti`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)
        val initialCoins = vm.uiState.value.coins

        vm.redeemReward("r1")
        vm.confirmRedemption()

        advanceTimeBy(500)

        assertEquals(initialCoins - 50, vm.uiState.value.coins)
        assertTrue(vm.uiState.value.showConfetti)
        assertEquals("Prêmio resgatado!", vm.uiState.value.redemptionSuccessMessage)
    }

    @Test
    fun `confirmRedemption marks reward as redeemed`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)

        vm.redeemReward("r1")
        vm.confirmRedemption()

        advanceTimeBy(500)

        val redeemedReward = vm.uiState.value.rewards.find { it.reward.id == "r1" }
        assertTrue(redeemedReward?.reward?.redeemed == true)
    }

    @Test
    fun `dismissRedemptionModal clears selection`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)

        vm.redeemReward("r1")
        vm.dismissRedemptionModal()

        val state = vm.uiState.value
        assertFalse(state.showRedemptionModal)
        assertEquals(null, state.selectedReward)
    }

    @Test
    fun `cannot redeem when coins insufficient`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)
        val initialCoins = vm.uiState.value.coins

        vm.redeemReward("r4")
        vm.confirmRedemption()

        advanceTimeBy(500)

        assertEquals(initialCoins, vm.uiState.value.coins)
        assertFalse(vm.uiState.value.showConfetti)
    }

    @Test
    fun `dismissConfetti clears confetti and message`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)

        vm.redeemReward("r1")
        vm.confirmRedemption()

        advanceTimeBy(500)

        vm.dismissConfetti()

        assertFalse(vm.uiState.value.showConfetti)
        assertEquals("", vm.uiState.value.redemptionSuccessMessage)
    }

    @Test
    fun `progressPercent is calculated correctly`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RewardsViewModel(repo)
        val escudo = vm.uiState.value.rewards.find { it.reward.id == "r1" }

        assertTrue(escudo != null)
        assertEquals(1f, escudo.progressPercent, 0.01f)
    }
}
