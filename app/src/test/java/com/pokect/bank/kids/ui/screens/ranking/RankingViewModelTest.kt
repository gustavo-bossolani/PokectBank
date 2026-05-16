package com.pokect.bank.kids.ui.screens.ranking

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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RankingViewModelTest {

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
    fun `initial state has 5 members sorted by XP descending`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)
        val state = vm.uiState.value

        assertEquals(5, state.members.size)
        assertEquals("Mamãe", state.members[0].member.name)
        assertEquals(9200, state.members[0].member.xp)
        assertEquals("Pedro", state.members[4].member.name)
        assertEquals(2100, state.members[4].member.xp)
    }

    @Test
    fun `positions are assigned correctly`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)
        val state = vm.uiState.value

        assertEquals(1, state.members[0].position)
        assertEquals(2, state.members[1].position)
        assertEquals(3, state.members[2].position)
        assertEquals(4, state.members[3].position)
        assertEquals(5, state.members[4].position)
    }

    @Test
    fun `currentUser is identified`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)
        val state = vm.uiState.value

        assertNotNull(state.currentUser)
        assertEquals("Lucas", state.currentUser?.name)
        assertTrue(state.currentUser?.isCurrentUser == true)
    }

    @Test
    fun `weeklyChallenge is loaded`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)
        val state = vm.uiState.value

        assertNotNull(state.weeklyChallenge)
        assertEquals("Economia da Semana", state.weeklyChallenge?.title)
    }

    @Test
    fun `joinWeeklyChallenge sets hasJoined to true`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)

        vm.joinWeeklyChallenge("wc1")

        kotlinx.coroutines.delay(100)

        val state = vm.uiState.value
        assertTrue(state.weeklyChallenge?.hasJoined == true)
    }

    @Test
    fun `joinWeeklyChallenge adds user to participants`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)

        vm.joinWeeklyChallenge("wc1")

        kotlinx.coroutines.delay(100)

        val state = vm.uiState.value
        assertTrue(state.weeklyChallenge?.participants?.contains("fm1") == true)
    }

    @Test
    fun `joinWeeklyChallenge is idempotent`() = runTest(testDispatcher) {
        val repo = AppRepository()
        val vm = RankingViewModel(repo)

        vm.joinWeeklyChallenge("wc1")
        vm.joinWeeklyChallenge("wc1")

        kotlinx.coroutines.delay(100)

        val state = vm.uiState.value
        val participants = state.weeklyChallenge?.participants ?: emptyList()
        assertEquals(3, participants.size)
        assertEquals(1, participants.count { it == "fm1" })
    }
}
